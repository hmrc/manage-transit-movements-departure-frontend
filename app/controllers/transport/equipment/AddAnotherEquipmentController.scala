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
import controllers.transport.equipment.index.{routes => indexRoutes}
import javax.inject.Inject
import models.{Index, LocalReferenceNumber, Mode}
import pages.transport.preRequisites.ContainerIndicatorPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.transport.equipment.AddAnotherEquipmentViewModel.AddAnotherEquipmentViewModelProvider
import views.html.transport.equipment.AddAnotherEquipmentView

class AddAnotherEquipmentController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  view: AddAnotherEquipmentView,
  viewModelProvider: AddAnotherEquipmentViewModelProvider
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(ContainerIndicatorPage)) {
      implicit request =>
        val viewModel = viewModelProvider(request.userAnswers, mode)
        val form      = formProvider("transport.equipment.addAnotherEquipment", viewModel.allowMoreEquipments)
        viewModel.equipmentsCount match {
          case 0 =>
            if (request.arg) {
              Redirect(indexRoutes.ContainerIdentificationNumberController.onPageLoad(lrn, mode, Index(0)))
            } else {
              Redirect(routes.AddTransportEquipmentYesNoController.onPageLoad(lrn, mode))
            }
          case _ => Ok(view(form, lrn, mode, viewModel, viewModel.allowMoreEquipments))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode)
      val form      = formProvider("transport.equipment.addAnotherEquipment", viewModel.allowMoreEquipments)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, mode, viewModel, viewModel.allowMoreEquipments)),
          {
            case true => // TODO Sort out redirection logic
              Redirect(indexRoutes.ContainerIdentificationNumberController.onPageLoad(lrn, mode, Index(viewModel.equipmentsCount)))
            case false =>
              Redirect(???) // TODO Use the correct navigator provider to redirect
          }
        )
  }
}
