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

import config.FrontendAppConfig
import controllers.actions.{Actions, PreTaskListCompletedAction}
import controllers.{SettableOps, SettableOpsRunner}
import models.reference.AdditionalDeclarationType
import models.{LocalReferenceNumber, Mode}
import pages.preTaskList.StandardDeclarationPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.preTaskList.StandardDeclarationView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class StandardDeclarationController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  val controllerComponents: MessagesControllerComponents,
  checkIfPreTaskListAlreadyCompleted: PreTaskListCompletedAction,
  val config: FrontendAppConfig,
  actions: Actions,
  view: StandardDeclarationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted) {
      implicit request =>
        val standard: String = Messages("additionalDeclarationType.A")
        StandardDeclarationPage.writeToUserAnswers(AdditionalDeclarationType("A", standard)).writeToSession()
        Ok(view(lrn, mode))
    }

}
