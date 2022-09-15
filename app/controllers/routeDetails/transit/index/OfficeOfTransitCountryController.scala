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

package controllers.routeDetails.transit.index

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.CountryFormProvider
import models.CountryList.countriesOfRoutingReads
import models.requests.DataRequest
import models.{CountryList, Index, LocalReferenceNumber, Mode, RichOptionalJsArray}
import navigation.routeDetails.OfficeOfTransitNavigatorProvider
import pages.routeDetails.transit.index.OfficeOfTransitCountryPage
import pages.sections.routeDetails.routing.CountriesOfRoutingSection
import play.api.data.FormError
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.{CountriesService, CustomsOfficesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.transit.index.OfficeOfTransitCountryView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfTransitCountryController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: OfficeOfTransitNavigatorProvider,
  actions: Actions,
  formProvider: CountryFormProvider,
  countriesService: CountriesService,
  customsOfficesService: CustomsOfficesService,
  val controllerComponents: MessagesControllerComponents,
  view: OfficeOfTransitCountryView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix: String = "routeDetails.transit.officeOfTransitCountry"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      getCountries.map {
        countryList =>
          val form = formProvider(prefix, countryList)
          val preparedForm = request.userAnswers.get(OfficeOfTransitCountryPage(index)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, lrn, countryList.countries, mode, index))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      getCountries.flatMap {
        countryList =>
          val form = formProvider(prefix, countryList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, countryList.countries, mode, index))),
              value =>
                customsOfficesService.getCustomsOfficesOfTransitForCountry(value.code).flatMap {
                  case x if x.customsOffices.nonEmpty =>
                    navigatorProvider(index).flatMap {
                      implicit navigator =>
                        OfficeOfTransitCountryPage(index).writeToUserAnswers(value).writeToSession().navigateWith(mode)
                    }
                  case _ =>
                    val formWithErrors = form.withError(FormError("value", s"$prefix.error.noOffices"))
                    Future.successful(BadRequest(view(formWithErrors, lrn, countryList.countries, mode, index)))
                }
            )
      }
  }

  private def getCountries(implicit request: DataRequest[_]): Future[CountryList] =
    request.userAnswers.get(CountriesOfRoutingSection).validate(countriesOfRoutingReads) match {
      case Some(x) if x.countries.nonEmpty => Future.successful(x)
      case _                               => countriesService.getCountries()
    }
}
