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

import config.FrontendAppConfig
import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.EnumerableFormProvider
import models.reference.AdditionalDeclarationType
import models.{LocalReferenceNumber, Mode}
import navigation.{PreTaskListNavigatorProvider, UserAnswersNavigator}
import pages.preTaskList.AdditionalDeclarationTypePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.AdditionalDeclarationTypesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.preTaskList.AdditionalDeclarationTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdditionalDeclarationTypeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val frontendAppConfig: FrontendAppConfig,
  navigatorProvider: PreTaskListNavigatorProvider,
  actions: Actions,
  checkIfPreTaskListAlreadyCompleted: PreTaskListCompletedAction,
  formProvider: EnumerableFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AdditionalDeclarationTypeView,
  service: AdditionalDeclarationTypesService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(additionalDeclarationTypes: Seq[AdditionalDeclarationType]): Form[AdditionalDeclarationType] =
    formProvider[AdditionalDeclarationType]("additionalDeclarationType", additionalDeclarationTypes)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted)
    .async {
      implicit request =>
        if (frontendAppConfig.isPreLodgeEnabled) {
          service.getAdditionalDeclarationTypes().map {
            additionalDeclarationTypes =>
              val preparedForm = request.userAnswers.get(AdditionalDeclarationTypePage) match {
                case None        => form(additionalDeclarationTypes)
                case Some(value) => form(additionalDeclarationTypes).fill(value)
              }

              Ok(view(preparedForm, lrn, additionalDeclarationTypes, mode))
          }
        } else {
          Future.successful(Redirect(controllers.preTaskList.routes.StandardDeclarationController.onPageLoad(lrn)))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted)
    .async {
      implicit request =>
        if (frontendAppConfig.isPreLodgeEnabled) {

          service.getAdditionalDeclarationTypes().flatMap {
            additionalDeclarationTypes =>
              form(additionalDeclarationTypes)
                .bindFromRequest()
                .fold(
                  formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, additionalDeclarationTypes, mode))),
                  value => {
                    val navigator: UserAnswersNavigator = navigatorProvider(mode)
                    AdditionalDeclarationTypePage.writeToUserAnswers(value).writeToSession(sessionRepository).navigateWith(navigator)
                  }
                )
          }

        } else {
          Future.successful(Redirect(controllers.preTaskList.routes.StandardDeclarationController.onPageLoad(lrn)))
        }
    }
}
