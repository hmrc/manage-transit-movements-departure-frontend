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
import models.requests.DataRequest
import models.{CountryList, Index, LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.routeDetails.RouteDetailsNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ListItem
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
  config: FrontendAppConfig,
  viewModelProvider: AddAnotherOfficeOfTransitViewModelProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherOfficeOfTransitView,
  countriesService: CountriesService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(allowMoreOfficesOfTransit: Boolean): Form[Boolean] =
    formProvider("routeDetails.transit.addAnotherOfficeOfTransit", allowMoreOfficesOfTransit)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      for {
        ctcCountries                          <- countriesService.getCountryCodesCTC()
        customsSecurityAgreementAreaCountries <- countriesService.getCustomsSecurityAgreementAreaCountries()
      } yield viewData(mode, ctcCountries, customsSecurityAgreementAreaCountries) match {
        case (officesOfTransit, numberOfOfficesOfTransit, allowMoreOfficesOfTransit) =>
          numberOfOfficesOfTransit match {
            case 0 =>
              val navigator: UserAnswersNavigator = navigatorProvider(mode, ctcCountries, customsSecurityAgreementAreaCountries)
              Redirect(navigator.nextPage(request.userAnswers))
            case _ =>
              Ok(view(form(allowMoreOfficesOfTransit), lrn, mode, officesOfTransit, allowMoreOfficesOfTransit))
          }
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      for {
        ctcCountries                          <- countriesService.getCountryCodesCTC()
        customsSecurityAgreementAreaCountries <- countriesService.getCustomsSecurityAgreementAreaCountries()
      } yield viewData(mode, ctcCountries, customsSecurityAgreementAreaCountries) match {
        case (officesOfTransit, numberOfOfficesOfTransit, allowMoreOfficesOfTransit) =>
          form(allowMoreOfficesOfTransit)
            .bindFromRequest()
            .fold(
              formWithErrors => BadRequest(view(formWithErrors, lrn, mode, officesOfTransit, allowMoreOfficesOfTransit)),
              {
                case true => Redirect(indexRoutes.OfficeOfTransitCountryController.onPageLoad(lrn, mode, Index(numberOfOfficesOfTransit)))
                case false =>
                  val navigator: UserAnswersNavigator = navigatorProvider(mode, ctcCountries, customsSecurityAgreementAreaCountries)
                  Redirect(navigator.nextPage(request.userAnswers))
              }
            )
      }
  }

  private def viewData(
    mode: Mode,
    ctcCountries: CountryList,
    customsSecurityAgreementAreaCountries: CountryList
  )(implicit request: DataRequest[_]): (Seq[ListItem], Int, Boolean) = {
    val viewModel                = viewModelProvider(request.userAnswers, mode, ctcCountries, customsSecurityAgreementAreaCountries)
    val officesOfTransit         = viewModel.listItems
    val numberOfOfficesOfTransit = officesOfTransit.length
    (officesOfTransit, numberOfOfficesOfTransit, numberOfOfficesOfTransit < config.maxOfficesOfTransit)
  }
}
