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

package controllers.preTaskList

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.actions.{Actions, PreTaskListCompletedAction}
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import models.journeyDomain.{PreTaskListDomain, ReaderError, UserAnswersReader}
import models.{LocalReferenceNumber, NormalMode}
import pages.sections.PreTaskListSection
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.preTaskList.PreTaskListViewModel.PreTaskListViewModelProvider
import views.html.preTaskList.CheckYourAnswersView

import scala.concurrent.ExecutionContext

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val frontendAppConfig: FrontendAppConfig,
  actions: Actions,
  checkIfPreTaskListAlreadyCompleted: PreTaskListCompletedAction,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView,
  viewModelProvider: PreTaskListViewModelProvider,
  sessionService: SessionService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted) {
      implicit request =>
        UserAnswersReader[PreTaskListDomain](frontendAppConfig.isPreLodgeEnabled).run(request.userAnswers) match {
          case Left(ReaderError(page, _, _)) =>
            logger.warn(s"[preTaskList.CheckYourAnswersController][$lrn] Shouldn't be here yet. Redirecting to ${page.path}")
            Redirect(page.route(request.userAnswers, NormalMode).getOrElse(controllers.routes.ErrorController.technicalDifficulties()))
          case _ =>
            val section = viewModelProvider.apply(request.userAnswers).section
            Ok(view(lrn, Seq(section)))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted)
    .async {
      implicit request =>
        PreTaskListSection
          .updateTask(frontendAppConfig.isPreLodgeEnabled)
          .writeToSession(sessionRepository)
          .navigateTo(controllers.routes.TaskListController.onPageLoad(lrn))
          .map(sessionService.remove(_))
    }

}
