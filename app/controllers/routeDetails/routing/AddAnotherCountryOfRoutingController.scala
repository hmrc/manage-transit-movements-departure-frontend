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

package controllers.routeDetails.routing

import config.FrontendAppConfig
import controllers.actions._
import controllers.routeDetails.routing.index.{routes => indexRoutes}
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.AddAnotherFormProvider
import models.CountryList.countriesOfRoutingReads
import models.journeyDomain.routeDetails.RouteDetailsDomain
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode, RichOptionalJsArray}
import navigation.UserAnswersNavigator
import navigation.routeDetails.RoutingNavigatorProvider
import pages.routeDetails.routing.CountriesOfRoutingInSecurityAgreement
import pages.sections.routeDetails.routing.CountriesOfRoutingSection
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ListItem
import viewModels.routeDetails.routing.AddAnotherCountryOfRoutingViewModel.AddAnotherCountryOfRoutingViewModelProvider
import views.html.routeDetails.routing.AddAnotherCountryOfRoutingView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherCountryOfRoutingController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: RoutingNavigatorProvider,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  countriesService: CountriesService,
  config: FrontendAppConfig,
  viewModelProvider: AddAnotherCountryOfRoutingViewModelProvider,
  view: AddAnotherCountryOfRoutingView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(allowMoreCountries: Boolean): Form[Boolean] =
    formProvider("routeDetails.routing.addAnotherCountryOfRouting", allowMoreCountries)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val (countries, numberOfCountries, allowMoreCountries) = viewData(mode)
      numberOfCountries match {
        case 0 => Redirect(routes.BindingItineraryController.onPageLoad(lrn, mode))
        case _ => Ok(view(form(allowMoreCountries), lrn, mode, countries, allowMoreCountries))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      lazy val (countries, numberOfCountries, allowMoreCountries) = viewData(mode)
      form(allowMoreCountries)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, countries, allowMoreCountries))),
          {
            case true =>
              Future.successful(Redirect(indexRoutes.CountryOfRoutingController.onPageLoad(lrn, mode, Index(numberOfCountries))))
            case false =>
              request.userAnswers.get(CountriesOfRoutingSection).validate(countriesOfRoutingReads).map(_.countries) match {
                case Some(countriesOfRouting) =>
                  for {
                    ctcCountries                          <- countriesService.getCountryCodesCTC()
                    customsSecurityAgreementAreaCountries <- countriesService.getCustomsSecurityAgreementAreaCountries()
                    inSecurityAgreement = countriesOfRouting.map(_.code.code).forall(customsSecurityAgreementAreaCountries.countryCodes.contains(_))
                    result <- {
                      implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, ctcCountries, customsSecurityAgreementAreaCountries)
                      CountriesOfRoutingInSecurityAgreement
                        .writeToUserAnswers(inSecurityAgreement)
                        .updateTask()(RouteDetailsDomain.userAnswersReader(ctcCountries.countryCodes, customsSecurityAgreementAreaCountries.countryCodes))
                        .writeToSession()
                        .navigate()
                    }
                  } yield result
                case None =>
                  Future.successful(Redirect(controllers.routes.ErrorController.technicalDifficulties()))
              }
          }
        )
  }

  private def viewData(mode: Mode)(implicit request: DataRequest[_]): (Seq[ListItem], Int, Boolean) = {
    val countries         = viewModelProvider.apply(request.userAnswers, mode).listItems
    val numberOfCountries = countries.length
    (countries, numberOfCountries, numberOfCountries < config.maxCountriesOfRouting)
  }
}
