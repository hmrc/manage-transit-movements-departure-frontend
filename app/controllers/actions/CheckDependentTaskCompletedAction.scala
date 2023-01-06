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

import controllers.preTaskList.routes._
import controllers.routes._
import models.domain.UserAnswersReader
import models.journeyDomain.PreTaskListDomain
import models.requests.DataRequest
import play.api.Logging
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.runtime.universe.{typeOf, TypeTag}

class CheckDependentTaskCompletedAction[T: TypeTag](implicit val executionContext: ExecutionContext, userAnswersReader: UserAnswersReader[T])
    extends ActionFilter[DataRequest]
    with Logging {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] =
    userAnswersReader.run(request.userAnswers) match {
      case Right(_) =>
        Future.successful(None)
      case Left(_) =>
        if (typeOf[T] =:= typeOf[PreTaskListDomain]) {
          logger.info(s"User is redirected to 'LRN' page when trying to access the URL: ${request.request.path}")
          Future.successful(Some(Redirect(LocalReferenceNumberController.onPageLoad())))
        } else {
          logger.info(s"User is redirected to 'task-list' page when trying to access the URL: ${request.request.path}")
          Future.successful(Some(Redirect(TaskListController.onPageLoad(request.userAnswers.lrn))))
        }
    }
}

trait CheckDependentTaskCompletedActionProvider {
  def apply[T: TypeTag](implicit userAnswersReader: UserAnswersReader[T]): ActionFilter[DataRequest]
}

class CheckDependentTaskCompletedActionProviderImpl @Inject() (implicit val ec: ExecutionContext) extends CheckDependentTaskCompletedActionProvider {
  override def apply[T: TypeTag](implicit userAnswersReader: UserAnswersReader[T]): ActionFilter[DataRequest] = new CheckDependentTaskCompletedAction()
}
