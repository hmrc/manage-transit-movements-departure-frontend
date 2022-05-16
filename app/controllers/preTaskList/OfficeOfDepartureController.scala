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

package controllers.preTaskList

import controllers.actions._
import forms.OfficeOfDepartureFormProvider
import models.{CountryList, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import pages.addItems.IsNonEuOfficePage
import pages.preTaskList.OfficeOfDeparturePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.{CountriesService, CustomsOfficesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.preTaskList.OfficeOfDepartureView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfDepartureController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @PreTaskListDetails navigator: Navigator,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: OfficeOfDepartureFormProvider,
  countriesService: CountriesService,
  customsOfficesService: CustomsOfficesService,
  val controllerComponents: MessagesControllerComponents,
  view: OfficeOfDepartureView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      customsOfficesService.getCustomsOfficesOfDeparture.flatMap {
        customsOfficeList =>
          val form = formProvider(customsOfficeList)
          val preparedForm = request.userAnswers
            .get(OfficeOfDeparturePage)
            .flatMap(
              x => customsOfficeList.getCustomsOffice(x.id)
            )
            .map(form.fill)
            .getOrElse(form)

          Future.successful(Ok(view(preparedForm, lrn, customsOfficeList.customsOffices, mode)))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(lrn) andThen requireData).async {
    implicit request =>
      customsOfficesService.getCustomsOfficesOfDeparture.flatMap {
        customsOfficeList =>
          val form = formProvider(customsOfficeList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, customsOfficeList.customsOffices, mode))),
              value =>
                for {
                  getNonEuCountries: CountryList <- countriesService.getNonEuTransitCountries()
                  isNotEu: Boolean = getNonEuCountries.getCountry(value.countryId).isDefined
                  ua1 <- Future.fromTry(request.userAnswers.set(OfficeOfDeparturePage, value))
                  ua2 <- Future.fromTry(ua1.set(IsNonEuOfficePage, isNotEu))
                  _   <- sessionRepository.set(ua2)
                } yield Redirect(navigator.nextPage(OfficeOfDeparturePage, mode, ua2))
            )
      }
  }

}
