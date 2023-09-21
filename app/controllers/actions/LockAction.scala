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

import com.google.inject.Inject
import controllers.routes
import models.LockCheck._
import models.requests.DataRequest
import play.api.Logging
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}
import services.LockService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

class LockActionProvider @Inject() (lockService: LockService)(implicit ec: ExecutionContext) {

  def apply(): ActionFilter[DataRequest] =
    new LockAction(lockService)
}

class LockAction(lockService: LockService)(implicit val executionContext: ExecutionContext) extends ActionFilter[DataRequest] with Logging {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    lockService.checkLock(request.userAnswers).map {
      case Unlocked         => None
      case Locked           => Some(Redirect(routes.LockedController.onPageLoad()))
      case LockCheckFailure => Some(Redirect(routes.ErrorController.technicalDifficulties()))
    }
  }
}
