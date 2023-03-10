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

import models.requests.DataRequest
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}
import viewModels.taskList.PreTaskListTask

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DependentTaskActionImpl @Inject() (implicit val executionContext: ExecutionContext) extends DependentTaskAction {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {

    def isTaskCompleted(task: String): Boolean =
      request.userAnswers.tasks.get(task).exists(_.isCompleted)

    if (isTaskCompleted(PreTaskListTask.section)) {
      Future.successful(None)
    } else {
      // TODO - use navigator to redirect to specific page
      Future.successful(Option(Redirect(controllers.preTaskList.routes.LocalReferenceNumberController.onPageLoad())))
    }
  }
}

trait DependentTaskAction extends ActionFilter[DataRequest]
