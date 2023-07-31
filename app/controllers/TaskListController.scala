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
import config.FrontendAppConfig
import connectors.SubmissionConnector
import controllers.actions.{Actions, DependentTaskAction}
import models.LocalReferenceNumber
import models.SubmissionState.{reads, NotSubmitted}
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HttpReads.{is2xx, is4xx}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.taskList.TaskListViewModel
import views.html.TaskListView

import java.time.temporal.ChronoUnit.DAYS
import java.time.{Clock, Duration, Instant, LocalDate, LocalDateTime, Period, ZoneOffset}
import scala.concurrent.ExecutionContext

class TaskListController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  checkPreTaskListCompleted: DependentTaskAction,
  val controllerComponents: MessagesControllerComponents,
  view: TaskListView,
  viewModel: TaskListViewModel,
  submissionConnector: SubmissionConnector,
  appConfig: FrontendAppConfig
)(implicit ec: ExecutionContext, clock: Clock)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkPreTaskListCompleted) {
      implicit request =>
        val tasks                = viewModel(request.userAnswers)
        val isSubmitted: Boolean = request.userAnswers.isSubmitted.getOrElse(NotSubmitted).showErrorContent

        viewModel(request.userAnswers)
        Ok(view(lrn, tasks, isSubmitted, request.userAnswers.expiryInDays.map(_.toInt)))
    }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkPreTaskListCompleted)
    .async {
      implicit request =>
        submissionConnector.post(lrn.value).map {
          case response if is2xx(response.status) =>
            logger.debug(s"TaskListController:onSubmit: success ${response.status}: ${response.body}")
            Redirect(controllers.routes.DeclarationSubmittedController.onPageLoad())
          case response if is4xx(response.status) =>
            logger.warn(s"TaskListController:onSubmit: bad request: ${response.status}: ${response.body}")
            BadRequest(response.body)
          case e =>
            logger.error(s"TaskListController:onSubmit: something went wrong: ${e.status}-${e.body}")
            InternalServerError(e.body)
        }
    }
}
