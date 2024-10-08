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

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.preTaskList.TIRCarnetReferenceFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.{PreTaskListNavigatorProvider, UserAnswersNavigator}
import pages.preTaskList.TIRCarnetReferencePage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.preTaskList.TirCarnetReferenceView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TIRCarnetReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: PreTaskListNavigatorProvider,
  actions: Actions,
  checkIfPreTaskListAlreadyCompleted: PreTaskListCompletedAction,
  formProvider: TIRCarnetReferenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: TirCarnetReferenceView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private val form = formProvider()

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    actions
      .requireData(lrn)
      .andThen(checkIfPreTaskListAlreadyCompleted) {
        implicit request =>
          val preparedForm = request.userAnswers.get(TIRCarnetReferencePage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, lrn, mode))
      }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted)
    .async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode))),
            value => {
              val navigator: UserAnswersNavigator = navigatorProvider(mode)
              TIRCarnetReferencePage.writeToUserAnswers(value).writeToSession(sessionRepository).navigateWith(navigator)
            }
          )
    }
}
