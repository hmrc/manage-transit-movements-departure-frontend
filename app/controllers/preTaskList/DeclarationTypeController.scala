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
import models.reference.DeclarationType
import models.{LocalReferenceNumber, Mode}
import navigation.{PreTaskListNavigatorProvider, UserAnswersNavigator}
import pages.preTaskList.{DeclarationTypePage, OfficeOfDeparturePage, ProcedureTypePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.DeclarationTypesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.preTaskList.DeclarationTypeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeclarationTypeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: PreTaskListNavigatorProvider,
  actions: Actions,
  checkIfPreTaskListAlreadyCompleted: PreTaskListCompletedAction,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: EnumerableFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DeclarationTypeView,
  service: DeclarationTypesService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(declarationTypes: Seq[DeclarationType]): Form[DeclarationType] =
    formProvider[DeclarationType]("declarationType", declarationTypes)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted)
    .andThen(getMandatoryPage.getFirst(OfficeOfDeparturePage))
    .andThen(getMandatoryPage.getSecond(ProcedureTypePage))
    .async {
      implicit request =>
        service.getDeclarationTypes(request.arg._1, request.arg._2).map {
          declarationTypes =>
            val preparedForm = request.userAnswers.get(DeclarationTypePage) match {
              case None        => form(declarationTypes)
              case Some(value) => form(declarationTypes).fill(value)
            }

            Ok(view(preparedForm, declarationTypes, lrn, mode))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(checkIfPreTaskListAlreadyCompleted)
    .andThen(getMandatoryPage.getFirst(OfficeOfDeparturePage))
    .andThen(getMandatoryPage.getSecond(ProcedureTypePage))
    .async {
      implicit request =>
        service.getDeclarationTypes(request.arg._1, request.arg._2).flatMap {
          declarationTypes =>
            form(declarationTypes)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, declarationTypes, lrn, mode))),
                value => {
                  val navigator: UserAnswersNavigator = navigatorProvider(mode)
                  DeclarationTypePage.writeToUserAnswers(value).writeToSession(sessionRepository).navigateWith(navigator)
                }
              )
        }
    }
}
