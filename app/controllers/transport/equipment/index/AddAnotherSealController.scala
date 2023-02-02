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

package controllers.transport.equipment.index

import config.FrontendAppConfig
import controllers.actions._
import forms.AddAnotherFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.transport.EquipmentNavigatorProvider
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.transport.equipment.AddAnotherSealViewModel.AddAnotherSealViewModelProvider
import views.html.transport.equipment.index.AddAnotherSealView

import javax.inject.Inject

class AddAnotherSealController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: EquipmentNavigatorProvider,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherSealView,
  viewModelProvider: AddAnotherSealViewModelProvider
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, equipmentIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode, equipmentIndex)
      val form      = formProvider("transport.equipment.index.addAnotherSeal", viewModel.allowMoreSeals)
      viewModel.numberOfSeals match {
        case 0 => Redirect(controllers.transport.equipment.index.routes.AddSealYesNoController.onPageLoad(lrn, mode, equipmentIndex))
        case _ => Ok(view(form, lrn, mode, viewModel, viewModel.allowMoreSeals, equipmentIndex))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, equipmentIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode, equipmentIndex)
      val form      = formProvider("transport.equipment.index.addAnotherSeal", viewModel.allowMoreSeals)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, mode, viewModel, viewModel.allowMoreSeals, equipmentIndex)),
          {
            case true =>
              Redirect(
                controllers.transport.equipment.index.seals.routes.IdentificationNumberController
                  .onPageLoad(lrn, mode, equipmentIndex, Index(viewModel.numberOfSeals))
              )
            case false =>
              Redirect(navigatorProvider(mode, equipmentIndex).nextPage(request.userAnswers))
          }
        )
  }
}
