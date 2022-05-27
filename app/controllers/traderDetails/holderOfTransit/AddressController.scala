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

package controllers.traderDetails.holderOfTransit

import controllers.actions._
import forms.AddressFormProvider
import models.requests.SpecificDataRequestProvider1
import models.{Address, CountryList, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.HolderOfTransit
import pages.traderDetails.holderOfTransit.{AddressPage, NamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.traderDetails.holderOfTransit.AddressView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @HolderOfTransit navigator: Navigator,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: AddressFormProvider,
  countriesService: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: AddressView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[_]

  private def name(implicit request: Request): String = request.arg

  private def form(countryList: CountryList)(implicit request: Request): Form[Address] =
    formProvider("traderDetails.holderOfTransit.address", name, countryList)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(NamePage))
    .async {
      implicit request =>
        countriesService.getCountries().map {
          countryList =>
            val preparedForm = request.userAnswers.get(AddressPage) match {
              case None        => form(countryList)
              case Some(value) => form(countryList).fill(value)
            }

            Ok(view(preparedForm, lrn, mode, countryList.countries, name))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(NamePage))
    .async {
      implicit request =>
        countriesService.getCountries().flatMap {
          countryList =>
            form(countryList)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, countryList.countries, name))),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(AddressPage, value))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(AddressPage, mode, updatedAnswers))
              )
        }
    }
}
