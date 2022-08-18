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

package controllers.routeDetails.officeOfExit

import controllers.actions._
import forms.CountryFormProvider
import models.{CountryList, LocalReferenceNumber, Mode}
import pages.routeDetails.officeOfExit.OfficeOfExitCountryPage
import pages.sections.routeDetails.CountriesOfRoutingSection
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.officeOfExit.OfficeOfExitCountryView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfExitCountryController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  //@OfficeOfExit implicit val navigator: Navigator,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: CountryFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: OfficeOfExitCountryView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).andThen(getMandatoryPage(CountriesOfRoutingSection)) {
    implicit request =>
      val countryList: CountryList = request.arg.as[CountryList]

      val form = formProvider("routeDetails.officeOfExit.officeOfExitCountry", countryList)
      val preparedForm = request.userAnswers.get(OfficeOfExitCountryPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, lrn, countryList.countries, mode))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(CountriesOfRoutingSection))
    .async {
      implicit request =>
        val countryList: CountryList = request.arg.as[CountryList]
        val form                     = formProvider("routeDetails.officeOfExit.officeOfExitCountry", countryList)
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, countryList.countries, mode))),
            value => ??? //OfficeOfExitCountryPage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
          )
    }
}
