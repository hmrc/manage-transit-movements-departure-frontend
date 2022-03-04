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

package controllers.routeDetails

import cats.data.OptionT
import cats.implicits._
import controllers.actions._
import forms.MovementDestinationCountryFormProvider
import models.reference.Country
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.RouteDetails
import pages.routeDetails.MovementDestinationCountryPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import renderer.Renderer
import repositories.SessionRepository
import services.CountriesService
import services.ExcludedCountriesService.movementDestinationCountryExcludedCountries
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.countryJsonList

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MovementDestinationCountryController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @RouteDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  countriesService: CountriesService,
  formProvider: MovementDestinationCountryFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport
    with Logging {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      (
        for {
          excludedCountries <- OptionT.fromOption[Future](movementDestinationCountryExcludedCountries(request.userAnswers))
          countryList       <- OptionT.liftF(countriesService.getDestinationCountries(request.userAnswers, excludedCountries))
          preparedForm = request.userAnswers
            .get(MovementDestinationCountryPage)
            .flatMap(countryList.getCountry)
            .map(formProvider(countryList).fill)
            .getOrElse(formProvider(countryList))
          page <- OptionT.liftF(renderPage(lrn, mode, preparedForm, countryList.countries, Results.Ok))
        } yield page
      ).getOrElseF {
        logger.warn(s"[Controller][MovementDestinationCountry][onPageLoad] OfficeOfDeparturePage is missing")
        Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      (
        for {
          excludedCountries <- OptionT.fromOption[Future](movementDestinationCountryExcludedCountries(request.userAnswers))
          countryList       <- OptionT.liftF(countriesService.getDestinationCountries(request.userAnswers, excludedCountries))
          page <- OptionT.liftF(
            formProvider(countryList)
              .bindFromRequest()
              .fold(
                formWithErrors => renderPage(lrn, mode, formWithErrors, countryList.countries, Results.BadRequest),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(MovementDestinationCountryPage, value.code))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(MovementDestinationCountryPage, mode, updatedAnswers))
              )
          )
        } yield page
      ).getOrElseF {
        logger.warn(s"[Controller][MovementDestinationCountry][onSubmit] OfficeOfDeparturePage is missing")
        Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
  }

  def renderPage(lrn: LocalReferenceNumber, mode: Mode, form: Form[Country], countries: Seq[Country], status: Results.Status)(implicit
    request: Request[AnyContent]
  ): Future[Result] = {
    val json = Json.obj(
      "form"        -> form,
      "lrn"         -> lrn,
      "mode"        -> mode,
      "countries"   -> countryJsonList(form.value, countries),
      "onSubmitUrl" -> routes.MovementDestinationCountryController.onSubmit(lrn, mode).url
    )

    renderer.render("movementDestinationCountry.njk", json).map(status(_))
  }
}
