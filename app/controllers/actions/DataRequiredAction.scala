/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.actions

import connectors.CacheConnector
import models.LocalReferenceNumber
import models.SubmissionState._
import models.requests.{DataRequest, OptionalDataRequest}
import play.api.Logging
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataRequiredAction(
  connector: CacheConnector
)(
  lrn: LocalReferenceNumber,
  ignoreSubmissionStatus: Boolean
)(implicit val executionContext: ExecutionContext)
    extends ActionRefiner[OptionalDataRequest, DataRequest]
    with Logging {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    lazy val failure = {
      logger.info(s"TaskListController: Departure with LRN $lrn has already been submitted")
      Left(Redirect(controllers.routes.SessionExpiredController.onPageLoad(lrn)))
    }

    request.userAnswers match {
      case Some(data) =>
        lazy val success = Right(DataRequest(request.request, request.eoriNumber, data))

        data.status match {
          case Submitted if ignoreSubmissionStatus => Future.successful(success)
          case Submitted                           => Future.successful(failure)
          case NotSubmitted                        => Future.successful(success)
          case _ =>
            connector.getMessages(lrn).map {
              messages =>
                if (messages.contains("IE029")) {
                  logger.warn(s"$lrn: Movement has been released for transit. Can no longer make changes.")
                  failure
                } else {
                  success
                }
            }
        }
      case _ =>
        Future.successful(failure)
    }
  }
}

trait DataRequiredActionProvider {
  def apply(lrn: LocalReferenceNumber, ignoreSubmissionStatus: Boolean): ActionRefiner[OptionalDataRequest, DataRequest]
}

class DataRequiredActionImpl @Inject() (
  connector: CacheConnector
)(implicit val executionContext: ExecutionContext)
    extends DataRequiredActionProvider {

  override def apply(lrn: LocalReferenceNumber, ignoreSubmissionStatus: Boolean): ActionRefiner[OptionalDataRequest, DataRequest] =
    new DataRequiredAction(connector)(lrn, ignoreSubmissionStatus)
}
