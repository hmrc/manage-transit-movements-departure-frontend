/*
 * Copyright 2022 HM Revenue & Customs
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
import forms.AddAnotherFormProvider
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, NormalMode}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.routeDetails.routing.AddAnotherCountryOfRoutingViewModel.AddAnotherCountryOfRoutingViewModelProvider
import views.html.routeDetails.routing.AddAnotherCountryOfRoutingView

import javax.inject.Inject

class AddAnotherCountryOfRoutingController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  val controllerComponents: MessagesControllerComponents,
  config: FrontendAppConfig,
  viewModelProvider: AddAnotherCountryOfRoutingViewModelProvider,
  view: AddAnotherCountryOfRoutingView
) extends FrontendBaseController
    with I18nSupport {

  private def form(allowMoreCountries: Boolean): Form[Boolean] =
    formProvider("routeDetails.routing.addAnotherCountryOfRouting", allowMoreCountries)

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val (countries, numberOfCountries, allowMoreCountries) = viewData
      numberOfCountries match {
        case 0 => Redirect(routes.BindingItineraryController.onPageLoad(lrn, NormalMode))
        case _ => Ok(view(form(allowMoreCountries), lrn, countries, allowMoreCountries))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      lazy val (countries, numberOfCountries, allowMoreCountries) = viewData
      form(allowMoreCountries)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, countries, allowMoreCountries)),
          {
            case true  => Redirect(indexRoutes.CountryOfRoutingController.onPageLoad(lrn, NormalMode, Index(numberOfCountries)))
            case false => Redirect(routes.CheckYourAnswersController.onPageLoad(lrn))
          }
        )
  }

  private def viewData(implicit request: DataRequest[_]): (Seq[ListItem], Int, Boolean) = {
    val countries         = viewModelProvider.apply(request.userAnswers).listItems
    val numberOfCountries = countries.length
    (countries, numberOfCountries, numberOfCountries < config.maxCountriesOfRouting)
  }
}
