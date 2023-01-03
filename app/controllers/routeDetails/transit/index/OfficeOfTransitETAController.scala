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

package controllers.routeDetails.transit.index

import config.FrontendAppConfig
import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.DateTimeFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.routeDetails.OfficeOfTransitNavigatorProvider
import pages.routeDetails.routing.CountryOfDestinationPage
import pages.routeDetails.transit.index.{OfficeOfTransitCountryPage, OfficeOfTransitETAPage, OfficeOfTransitPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.transit.index.OfficeOfTransitETAView

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfTransitETAController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  appConfig: FrontendAppConfig,
  navigatorProvider: OfficeOfTransitNavigatorProvider,
  formProvider: DateTimeFormProvider,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: OfficeOfTransitETAView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val localDate  = LocalDate.now()
  private val pastDate   = localDate.minusDays(appConfig.daysBefore)
  private val futureDate = localDate.plusDays(appConfig.daysAfter)

  private val form = formProvider("routeDetails.transit.index.officeOfTransitETA", pastDate, futureDate)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(OfficeOfTransitCountryPage(index), CountryOfDestinationPage))
    .andThen(getMandatoryPage.getSecond(OfficeOfTransitPage(index))) {
      implicit request =>
        request.arg match {
          case (country, customsOffice) =>
            val preparedForm = request.userAnswers.get(OfficeOfTransitETAPage(index)) match {
              case None        => form
              case Some(value) => form.fill(value)
            }
            Ok(view(preparedForm, lrn, country.description, customsOffice.name, mode, index))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(OfficeOfTransitCountryPage(index), CountryOfDestinationPage))
    .andThen(getMandatoryPage.getSecond(OfficeOfTransitPage(index)))
    .async {
      implicit request =>
        request.arg match {
          case (country, customsOffice) =>
            form
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, country.description, customsOffice.name, mode, index))),
                value =>
                  navigatorProvider(mode, index).flatMap {
                    implicit navigator =>
                      OfficeOfTransitETAPage(index).writeToUserAnswers(value).writeToSession().navigate()
                  }
              )
        }
    }
}
