/*
 * Copyright 2022 HM Revenue & Customs
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

import controllers.routes
import models.domain.UserAnswersReader
import models.journeyDomain.TaskDomain
import models.requests.DataRequest
import play.api.Logging
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckTaskAlreadyCompletedAction[T <: TaskDomain](implicit val executionContext: ExecutionContext, userAnswersReader: UserAnswersReader[T])
    extends ActionFilter[DataRequest]
    with Logging {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] =
    UserAnswersReader[T].run(request.userAnswers) match {
      case Right(_) =>
        logger.info(s"User is redirected to 'task-list' page when trying to access the URL: ${request.request.path}")
        Future.successful(Some(Redirect(routes.TaskListController.onPageLoad(request.userAnswers.lrn))))
      case Left(_) =>
        Future.successful(None)
    }
}

trait CheckTaskAlreadyCompletedActionProvider {
  def apply[T <: TaskDomain](implicit userAnswersReader: UserAnswersReader[T]): ActionFilter[DataRequest]
}

class CheckTaskAlreadyCompletedActionProviderImpl @Inject() (implicit val ec: ExecutionContext) extends CheckTaskAlreadyCompletedActionProvider {
  override def apply[T <: TaskDomain](implicit userAnswersReader: UserAnswersReader[T]): ActionFilter[DataRequest] = new CheckTaskAlreadyCompletedAction()
}
