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

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.EnumerableFormProvider
import models.reference.SecurityType
import models.{LocalReferenceNumber, Mode}
import navigation.{PreTaskListNavigatorProvider, UserAnswersNavigator}
import pages.preTaskList.SecurityDetailsTypePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.SecurityTypesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.preTaskList.SecurityDetailsTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SecurityDetailsTypeController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: PreTaskListNavigatorProvider,
  actions: Actions,
  checkIfPreTaskListAlreadyCompleted: PreTaskListCompletedAction,
  formProvider: EnumerableFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: SecurityDetailsTypeView,
  service: SecurityTypesService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(securityTypes: Seq[SecurityType]): Form[SecurityType] =
    formProvider[SecurityType]("securityDetailsType", securityTypes)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted)
    .async {
      implicit request =>
        service.getSecurityTypes().map {
          securityTypes =>
            val preparedForm = request.userAnswers.get(SecurityDetailsTypePage) match {
              case None        => form(securityTypes)
              case Some(value) => form(securityTypes).fill(value)
            }

            Ok(view(preparedForm, securityTypes, lrn, mode))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted)
    .async {
      implicit request =>
        service.getSecurityTypes().flatMap {
          securityTypes =>
            form(securityTypes)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, securityTypes, lrn, mode))),
                value => {
                  implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
                  SecurityDetailsTypePage.writeToUserAnswers(value).writeToSession().navigate()
                }
              )
        }
    }
}
