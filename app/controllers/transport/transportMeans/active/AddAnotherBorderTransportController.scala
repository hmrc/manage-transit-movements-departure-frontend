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

package controllers.transport.transportMeans.active

import config.FrontendAppConfig
import controllers.actions._
import forms.AddAnotherFormProvider
import models.{Index, LocalReferenceNumber, Mode, NormalMode}
import navigation.transport.TransportMeansNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.transport.transportMeans.active.AddAnotherBorderTransportViewModel
import viewModels.transport.transportMeans.active.AddAnotherBorderTransportViewModel.AddAnotherBorderTransportViewModelProvider
import views.html.transport.transportMeans.active.AddAnotherBorderTransportView

import javax.inject.Inject

class AddAnotherBorderTransportController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  viewModelProvider: AddAnotherBorderTransportViewModelProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherBorderTransportView
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherBorderTransportViewModel): Form[Boolean] =
    formProvider(viewModel.prefix, viewModel.allowMoreActiveBorderTransports)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode)
      viewModel.activeBorderTransports match {
        case 0 => Redirect(controllers.transport.transportMeans.routes.AnotherVehicleCrossingYesNoController.onPageLoad(lrn, mode))
        case _ => Ok(view(form(viewModel), lrn, mode, viewModel))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      lazy val viewModel = viewModelProvider(request.userAnswers, mode)
      form(viewModel)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, mode, viewModel)),
          {
            case true  => Redirect(routes.IdentificationController.onPageLoad(lrn, NormalMode, Index(viewModel.activeBorderTransports)))
            case false => Redirect(controllers.transport.transportMeans.routes.TransportMeansCheckYourAnswersController.onPageLoad(lrn, mode))
          }
        )
  }
}
