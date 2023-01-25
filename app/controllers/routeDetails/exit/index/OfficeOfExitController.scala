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
import forms.CustomsOfficeForCountryFormProvider
import models.journeyDomain.routeDetails.RouteDetailsDomain
import models.{Index, LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.routeDetails.OfficeOfExitNavigatorProvider
import pages.routeDetails.exit.index.{OfficeOfExitCountryPage, OfficeOfExitPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.{CountriesService, CustomsOfficesService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.exit.index.OfficeOfExitView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfExitController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: OfficeOfExitNavigatorProvider,
  actions: Actions,
  formProvider: CustomsOfficeForCountryFormProvider,
  customsOfficesService: CustomsOfficesService,
  countriesService: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  view: OfficeOfExitView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(OfficeOfExitCountryPage(index)))
    .async {
      implicit request =>
        val country = request.arg
        customsOfficesService.getCustomsOfficesOfExitForCountry(country.code).map {
          customsOfficeList =>
            val form = formProvider("routeDetails.exit.index.officeOfExit", customsOfficeList, country.description)
            val preparedForm = request.userAnswers.get(OfficeOfExitPage(index)) match {
              case None        => form
              case Some(value) => form.fill(value)
            }

            Ok(view(preparedForm, lrn, customsOfficeList.customsOffices, country.description, index, mode))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(OfficeOfExitCountryPage(index)))
    .async {
      implicit request =>
        val country = request.arg
        customsOfficesService.getCustomsOfficesOfExitForCountry(country.code).flatMap {
          customsOfficeList =>
            val form = formProvider("routeDetails.exit.index.officeOfExit", customsOfficeList, country.description)
            form
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, customsOfficeList.customsOffices, country.description, index, mode))),
                value =>
                  for {
                    ctcCountries                          <- countriesService.getCountryCodesCTC()
                    customsSecurityAgreementAreaCountries <- countriesService.getCustomsSecurityAgreementAreaCountries()
                    result <- {
                      implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, index, ctcCountries, customsSecurityAgreementAreaCountries)
                      OfficeOfExitPage(index)
                        .writeToUserAnswers(value)
                        .updateTask()(RouteDetailsDomain.userAnswersReader(ctcCountries.countryCodes, customsSecurityAgreementAreaCountries.countryCodes))
                        .writeToSession()
                        .navigate()
                    }
                  } yield result
              )
        }
    }
}
