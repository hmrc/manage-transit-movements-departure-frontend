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

package controllers.transport.equipment

import config.FrontendAppConfig
import controllers.actions._
import forms.AddAnotherFormProvider
import models.journeyDomain.transport.equipment.EquipmentDomain
import models.{Index, LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.transport.TransportNavigatorProvider
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.transport.equipment.AddAnotherEquipmentViewModel.AddAnotherEquipmentViewModelProvider
import views.html.transport.equipment.AddAnotherEquipmentView

import javax.inject.Inject

class AddAnotherEquipmentController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  navigatorProvider: TransportNavigatorProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherEquipmentView,
  viewModelProvider: AddAnotherEquipmentViewModelProvider
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode)
      val form      = formProvider(viewModel.prefix, viewModel.allowMoreEquipments)
      viewModel.equipmentsCount match {
        case 0 => Redirect(routes.AddTransportEquipmentYesNoController.onPageLoad(lrn, mode))
        case _ => Ok(view(form, lrn, mode, viewModel, viewModel.allowMoreEquipments))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode)
      val form      = formProvider(viewModel.prefix, viewModel.allowMoreEquipments)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, mode, viewModel, viewModel.allowMoreEquipments)),
          {
            case true =>
              Redirect(
                UserAnswersNavigator.nextPage[EquipmentDomain](request.userAnswers, mode)(EquipmentDomain.userAnswersReader(Index(viewModel.equipmentsCount)))
              )
            case false =>
              Redirect(navigatorProvider(mode).nextPage(request.userAnswers))
          }
        )
  }
}
