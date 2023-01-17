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

package controllers.transport.authorisationsAndLimit.authorisations

import config.FrontendAppConfig
import controllers.actions._
import controllers.transport.authorisationsAndLimit.authorisations.index.{routes => authorisationRoutes}
import forms.AddAnotherFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.transport.AuthorisationsAndLimitNavigatorProvider
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.transport.authorisations.AddAnotherAuthorisationViewModel.AddAnotherAuthorisationViewModelProvider
import views.html.transport.authorisationsAndLimit.authorisations.AddAnotherAuthorisationView

import javax.inject.Inject

class AddAnotherAuthorisationController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: AuthorisationsAndLimitNavigatorProvider,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherAuthorisationView,
  viewModelProvider: AddAnotherAuthorisationViewModelProvider
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode)
      val form      = formProvider("transport.authorisations.addAnotherAuthorisation", viewModel.allowMoreAuthorisations)
      viewModel.authorisations match {
        case 0 => Redirect(navigatorProvider(mode).nextPage(request.userAnswers))
        case _ => Ok(view(form, lrn, mode, viewModel, viewModel.allowMoreAuthorisations))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode)
      val form      = formProvider("transport.authorisations.addAnotherAuthorisation", viewModel.allowMoreAuthorisations)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, mode, viewModel, viewModel.allowMoreAuthorisations)),
          {
            case true =>
              Redirect(authorisationRoutes.AuthorisationTypeController.onPageLoad(lrn, mode, Index(viewModel.authorisations)))
            case false =>
              Redirect(navigatorProvider(mode).nextPage(request.userAnswers))
          }
        )
  }
}
