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
import connectors.SubmissionConnector
import controllers.actions.{Actions, DependentTaskAction}
import models.{LocalReferenceNumber, SubmissionState}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HttpReads.is2xx
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.taskList.{TaskListTask, TaskListViewModel}
import views.html.TaskListView

import scala.concurrent.{ExecutionContext, Future}

class TaskListController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  checkPreTaskListCompleted: DependentTaskAction,
  val controllerComponents: MessagesControllerComponents,
  view: TaskListView,
  viewModel: TaskListViewModel,
  submissionConnector: SubmissionConnector
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkPreTaskListCompleted)
    .async {
      implicit request =>
        request.userAnswers.status match {
          case SubmissionState.Submitted =>
            logger.info(s"TaskListController: Departure with LRN $lrn has already been submitted")
            Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
          case status =>
            for {
              expiryInDays <- submissionConnector.getExpiryInDays(lrn.value)
              tasks: Seq[TaskListTask]      = viewModel(request.userAnswers)
              showSubmissionButton: Boolean = viewModel.showSubmissionButton(tasks)
            } yield {
              println(s"\n\n\n\n $showSubmissionButton \n\n\n\n")
              Ok(view(lrn, tasks, status.showErrorContent, expiryInDays, showSubmissionButton))
            }
        }
    }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkPreTaskListCompleted)
    .async {
      implicit request =>
        request.userAnswers.status match {
          case SubmissionState.Submitted =>
            logger.info(s"TaskListController: Departure with LRN $lrn has already been submitted")
            Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
          case _ =>
            submissionConnector.post(lrn.value).map {
              case response if is2xx(response.status) =>
                logger.debug(s"TaskListController:onSubmit: ${response.status}: ${response.body}")
                Redirect(controllers.routes.DeclarationSubmittedController.onPageLoad(lrn))
              case e =>
                logger.error(s"TaskListController:onSubmit: ${e.status}: ${e.body}")
                Redirect(routes.ErrorController.technicalDifficulties())
            }
        }
    }
}
