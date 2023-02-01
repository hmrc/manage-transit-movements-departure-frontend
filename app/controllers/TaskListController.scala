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

package controllers

import com.google.inject.Inject
import controllers.actions.{Actions, DependentTasksCompletedActionProvider}
import models.LocalReferenceNumber
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.ApiService
import uk.gov.hmrc.http.HttpReads.{is2xx, is4xx}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.taskList.{PreTaskListTask, TaskListViewModel}
import views.html.TaskListView

import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  checkDependentTasksCompleted: DependentTasksCompletedActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: TaskListView,
  viewModel: TaskListViewModel,
  apiService: ApiService,
  implicit val sessionRepository: SessionRepository
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkDependentTasksCompleted(PreTaskListTask.section)) {
      implicit request =>
        val tasks = viewModel(request.userAnswers)
        Ok(view(lrn, tasks))
    }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkDependentTasksCompleted(PreTaskListTask.section))
    .async {
      implicit request =>
        apiService.submitDeclaration(request.userAnswers).flatMap {
          case response if is2xx(response.status) =>
            for {
              _ <- sessionRepository.set(request.userAnswers.updateStatus(utils.Status.Submitted))
            } yield Redirect(controllers.routes.DeclarationSubmittedController.onPageLoad())
          case response if is4xx(response.status) =>
            // TODO - log and audit fail. How to handle this?
            Future.successful(BadRequest)
          case _ =>
            // TODO - log and audit fail. How to handle this?
            Future.successful(InternalServerError("Something went wrong"))
        }
    }
}
