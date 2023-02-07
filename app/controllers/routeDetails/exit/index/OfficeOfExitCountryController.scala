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

package controllers.routeDetails.exit.index

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.CountryFormProvider
import models.CountryList.countriesOfRoutingReads
import models.journeyDomain.routeDetails.RouteDetailsDomain
import models.reference.Country
import models.requests.SpecificDataRequestProvider1
import models.{CountryList, Index, LocalReferenceNumber, Mode, RichOptionalJsArray}
import navigation.UserAnswersNavigator
import navigation.routeDetails.OfficeOfExitNavigatorProvider
import pages.routeDetails.exit.index.OfficeOfExitCountryPage
import pages.routeDetails.routing.CountryOfDestinationPage
import pages.sections.routeDetails.routing.CountriesOfRoutingSection
import play.api.data.FormError
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.{CountriesService, CustomsOfficesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.exit.index.OfficeOfExitCountryView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfExitCountryController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: OfficeOfExitNavigatorProvider,
  actions: Actions,
  formProvider: CountryFormProvider,
  countriesService: CountriesService,
  customsOfficesService: CustomsOfficesService,
  val controllerComponents: MessagesControllerComponents,
  view: OfficeOfExitCountryView,
  getMandatoryPage: SpecificDataRequiredActionProvider
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix: String = "routeDetails.exit.index.officeOfExitCountry"

  private type Request = SpecificDataRequestProvider1[Country]#SpecificDataRequest[_]

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    actions
      .requireData(lrn)
      .andThen(getMandatoryPage(CountryOfDestinationPage))
      .async {
        implicit request =>
          getCountries.map {
            countryList =>
              val form = formProvider(prefix, countryList)
              val preparedForm = request.userAnswers.get(OfficeOfExitCountryPage(index)) match {
                case None        => form
                case Some(value) => form.fill(value)
              }
              Ok(view(preparedForm, lrn, countryList.countries, index, mode))
          }
      }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    actions
      .requireData(lrn)
      .andThen(getMandatoryPage(CountryOfDestinationPage))
      .async {
        implicit request =>
          getCountries.flatMap {
            countryList =>
              val form = formProvider(prefix, countryList)
              form
                .bindFromRequest()
                .fold(
                  formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, countryList.countries, index, mode))),
                  value =>
                    customsOfficesService.getCustomsOfficesOfExitForCountry(value.code).flatMap {
                      case x if x.customsOffices.nonEmpty =>
                        for {
                          ctcCountries                          <- countriesService.getCountryCodesCTC()
                          customsSecurityAgreementAreaCountries <- countriesService.getCustomsSecurityAgreementAreaCountries()
                          result <- {
                            implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, index, ctcCountries, customsSecurityAgreementAreaCountries)
                            OfficeOfExitCountryPage(index)
                              .writeToUserAnswers(value)
                              .updateTask()(RouteDetailsDomain.userAnswersReader(ctcCountries.countryCodes, customsSecurityAgreementAreaCountries.countryCodes))
                              .writeToSession()
                              .navigate()
                          }
                        } yield result
                      case _ =>
                        val formWithErrors = form.withError(FormError("value", s"$prefix.error.noOffices"))
                        Future.successful(BadRequest(view(formWithErrors, lrn, countryList.countries, index, mode)))
                    }
                )
          }
      }

  private def getCountries(implicit request: Request): Future[CountryList] =
    request.userAnswers.get(CountriesOfRoutingSection).validate(countriesOfRoutingReads) match {
      case Some(x) if x.countries.nonEmpty =>
        countriesService
          .getCustomsSecurityAgreementAreaCountries()
          .map(
            securityCountries =>
              CountryList(
                x.countries
                  .filterNot(_ == request.arg)
                  .intersect(securityCountries.countries)
              )
          )
      case _ => countriesService.getCountries()
    }
}
