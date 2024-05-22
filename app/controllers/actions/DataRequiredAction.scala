/*
 * Copyright 2023 HM Revenue & Customs
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

import models.requests.{DataRequest, OptionalDataRequest}
import models.{LocalReferenceNumber, SubmissionState}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataRequiredAction(lrn: LocalReferenceNumber, ignoreSubmissionStatus: Boolean)(implicit val executionContext: ExecutionContext)
    extends ActionRefiner[OptionalDataRequest, DataRequest] {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] =
    request.userAnswers match {
      case Some(data) if ignoreSubmissionStatus || data.status != SubmissionState.Submitted =>
        Future.successful(Right(DataRequest(request.request, request.eoriNumber, data)))
      case _ =>
        Future.successful(Left(Redirect(controllers.routes.SessionExpiredController.onPageLoad(lrn))))
    }
}

trait DataRequiredActionProvider {
  def apply(lrn: LocalReferenceNumber, ignoreSubmissionStatus: Boolean): ActionRefiner[OptionalDataRequest, DataRequest]
}

class DataRequiredActionImpl @Inject() (implicit val executionContext: ExecutionContext) extends DataRequiredActionProvider {

  override def apply(lrn: LocalReferenceNumber, ignoreSubmissionStatus: Boolean): ActionRefiner[OptionalDataRequest, DataRequest] =
    new DataRequiredAction(lrn, ignoreSubmissionStatus)
}
