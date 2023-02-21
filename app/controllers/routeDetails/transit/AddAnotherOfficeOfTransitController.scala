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

package controllers.routeDetails.transit

import config.FrontendAppConfig
import controllers.actions._
import controllers.routeDetails.transit.index.{routes => indexRoutes}
import forms.AddAnotherFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.routeDetails.RouteDetailsNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.routeDetails.transit.AddAnotherOfficeOfTransitViewModel
import viewModels.routeDetails.transit.AddAnotherOfficeOfTransitViewModel.AddAnotherOfficeOfTransitViewModelProvider
import views.html.routeDetails.transit.AddAnotherOfficeOfTransitView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AddAnotherOfficeOfTransitController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: RouteDetailsNavigatorProvider,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  viewModelProvider: AddAnotherOfficeOfTransitViewModelProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherOfficeOfTransitView,
  countriesService: CountriesService
)(implicit ec: ExecutionContext, config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherOfficeOfTransitViewModel): Form[Boolean] =
    formProvider(viewModel.prefix, viewModel.allowMore)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      for {
        ctcCountries                          <- countriesService.getCountryCodesCTC()
        customsSecurityAgreementAreaCountries <- countriesService.getCustomsSecurityAgreementAreaCountries()
      } yield {
        val viewModel = viewModelProvider(request.userAnswers, mode, ctcCountries, customsSecurityAgreementAreaCountries)
        viewModel.count match {
          case 0 => Redirect(routes.AddOfficeOfTransitYesNoController.onPageLoad(lrn, mode))
          case _ => Ok(view(form(viewModel), lrn, viewModel))
        }
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      for {
        ctcCountries                          <- countriesService.getCountryCodesCTC()
        customsSecurityAgreementAreaCountries <- countriesService.getCustomsSecurityAgreementAreaCountries()
      } yield {
        val viewModel = viewModelProvider(request.userAnswers, mode, ctcCountries, customsSecurityAgreementAreaCountries)
        form(viewModel)
          .bindFromRequest()
          .fold(
            formWithErrors => BadRequest(view(formWithErrors, lrn, viewModel)),
            {
              case true => Redirect(indexRoutes.OfficeOfTransitCountryController.onPageLoad(lrn, mode, viewModel.nextIndex))
              case false =>
                val navigator: UserAnswersNavigator = navigatorProvider(mode, ctcCountries, customsSecurityAgreementAreaCountries)
                Redirect(navigator.nextPage(request.userAnswers))
            }
          )
      }
  }
}
