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

package controllers.transport.supplyChainActors

import config.FrontendAppConfig
import controllers.actions._
import controllers.transport.supplyChainActors.index.{routes => supplyChainActorRoutes}
import controllers.transport.supplyChainActors.{routes => supplyChainActorsRoutes}
import forms.AddAnotherFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.transport.AuthorisationNavigatorProvider
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.transport.supplyChainActors.AddAnotherSupplyChainActorViewModel.AddAnotherSupplyChainActorViewModelProvider
import views.html.transport.supplyChainActors.AddAnotherSupplyChainActorView

import javax.inject.Inject

class AddAnotherSupplyChainActorController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: AuthorisationNavigatorProvider,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherSupplyChainActorView,
  viewModelProvider: AddAnotherSupplyChainActorViewModelProvider
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode)
      val form      = formProvider("transport.supplyChainActors.addAnotherSupplyChainActor", viewModel.allowMoreSupplyChainActors)
      viewModel.supplyChainActors match {
        case 0 => Redirect(supplyChainActorsRoutes.SupplyChainActorYesNoController.onPageLoad(lrn, mode))
        case _ => Ok(view(form, lrn, mode, viewModel, viewModel.allowMoreSupplyChainActors))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode)
      val form      = formProvider("transport.supplyChainActors.addAnotherSupplyChainActor", viewModel.allowMoreSupplyChainActors)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, mode, viewModel, viewModel.allowMoreSupplyChainActors)),
          {
            case true =>
              Redirect(supplyChainActorRoutes.SupplyChainActorTypeController.onPageLoad(lrn, mode, Index(viewModel.supplyChainActors)))
            case false =>
              Redirect(navigatorProvider(mode, Index(0)).nextPage(request.userAnswers))
          }
        )
  }
}
