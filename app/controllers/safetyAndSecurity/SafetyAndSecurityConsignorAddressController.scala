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

package controllers.safetyAndSecurity

import controllers.actions._
import controllers.{routes => mainRoutes}
import forms.CommonAddressFormProvider
import models.reference.{Country, CountryCode}
import models.{DependentSection, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.SafetyAndSecurityTraderDetails
import pages.safetyAndSecurity.{SafetyAndSecurityConsignorAddressPage, SafetyAndSecurityConsignorNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.countryJsonList

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SafetyAndSecurityConsignorAddressController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @SafetyAndSecurityTraderDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  checkDependentSection: CheckDependentSectionAction,
  countriesService: CountriesService,
  formProvider: CommonAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val template = "safetyAndSecurity/safetyAndSecurityConsignorAddress.njk"

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        countriesService.getCountries() flatMap {
          countryList =>
            request.userAnswers.get(SafetyAndSecurityConsignorNamePage) match {
              case Some(consignorName) =>
                val preparedForm = request.userAnswers.get(SafetyAndSecurityConsignorAddressPage) match {
                  case Some(value) => formProvider(countryList, consignorName).fill(value)
                  case None        => formProvider(countryList, consignorName)
                }

                val json = Json.obj(
                  "form"          -> preparedForm,
                  "lrn"           -> lrn,
                  "mode"          -> mode,
                  "consignorName" -> consignorName,
                  "countries"     -> countryJsonList(preparedForm.value.map(_.country), countryList.countries)
                )

                renderer.render(template, json).map(Ok(_))
              case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))

            }
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] =
    (identify
      andThen getData(lrn)
      andThen requireData
      andThen checkDependentSection(DependentSection.SafetyAndSecurity)).async {
      implicit request =>
        request.userAnswers.get(SafetyAndSecurityConsignorNamePage) match {
          case Some(consignorName) =>
            countriesService.getCountries() flatMap {
              countryList =>
                formProvider(countryList, consignorName)
                  .bindFromRequest()
                  .fold(
                    formWithErrors => {
                      val countryValue: Option[Country] = formWithErrors.data.get("country").flatMap {
                        country =>
                          countryList.getCountry(CountryCode(country))
                      }
                      val json = Json.obj(
                        "form"          -> formWithErrors,
                        "lrn"           -> lrn,
                        "mode"          -> mode,
                        "consignorName" -> consignorName,
                        "countries"     -> countryJsonList(countryValue, countryList.countries)
                      )

                      renderer.render(template, json).map(BadRequest(_))
                    },
                    value =>
                      for {
                        updatedAnswers <- Future.fromTry(request.userAnswers.set(SafetyAndSecurityConsignorAddressPage, value))
                        _              <- sessionRepository.set(updatedAnswers)
                      } yield Redirect(navigator.nextPage(SafetyAndSecurityConsignorAddressPage, mode, updatedAnswers))
                  )
            }
          case _ => Future.successful(Redirect(mainRoutes.SessionExpiredController.onPageLoad()))

        }
    }
}
