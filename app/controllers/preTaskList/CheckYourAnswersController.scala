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

package controllers.preTaskList

import com.google.inject.Inject
import controllers.actions.{Actions, CheckTaskAlreadyCompletedActionProvider}
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import models.domain.UserAnswersReader
import models.journeyDomain.{PreTaskListDomain, ReaderError}
import models.{LocalReferenceNumber, NormalMode}
import pages.preTaskList.DetailsConfirmedPage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.preTaskList.PreTaskListViewModel.PreTaskListViewModelProvider
import views.html.preTaskList.CheckYourAnswersView

import scala.concurrent.ExecutionContext

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  checkIfTaskAlreadyCompleted: CheckTaskAlreadyCompletedActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView,
  viewModelProvider: PreTaskListViewModelProvider
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfTaskAlreadyCompleted[PreTaskListDomain]) {
      implicit request =>
        UserAnswersReader[PreTaskListDomain].run(request.userAnswers) match {
          case Left(ReaderError(page, _)) if page != DetailsConfirmedPage =>
            logger.warn(s"[preTaskList.CheckYourAnswersController][$lrn] Shouldn't be here yet. Redirecting to ${page.path}")
            Redirect(page.route(request.userAnswers, NormalMode).getOrElse(controllers.routes.SessionExpiredController.onPageLoad()))
          case _ =>
            val section = viewModelProvider.apply(request.userAnswers).section
            Ok(view(lrn, Seq(section)))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfTaskAlreadyCompleted[PreTaskListDomain])
    .async {
      implicit request =>
        DetailsConfirmedPage
          .writeToUserAnswers(true)
          .writeToSession()
          .navigateTo(controllers.routes.TaskListController.onPageLoad(lrn))
    }

}
