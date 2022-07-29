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

package controllers.routeDetails.transit

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.OfficeOfTransitFormProvider
import models.{Index, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.Transit
import pages.routeDetails.transit.{OfficeOfTransitCountryPage, OfficeOfTransitPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CustomsOfficesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.transit.OfficeOfTransitView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfTransitController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @Transit implicit val navigator: Navigator,
  actions: Actions,
  formProvider: OfficeOfTransitFormProvider,
  service: CustomsOfficesService,
  val controllerComponents: MessagesControllerComponents,
  view: OfficeOfTransitView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      request.userAnswers
        .get(OfficeOfTransitCountryPage(index))
        .map {
          country =>
            service.getCustomsOfficesForCountry(country.code).flatMap {
              customsOfficeList =>
                val form = formProvider("routeDetails.transit.officeOfTransit", customsOfficeList, country.description)
                val preparedForm = request.userAnswers.get(OfficeOfTransitPage(index)) match {
                  case None        => form
                  case Some(value) => form.fill(value)
                }
                Future.successful(Ok(view(preparedForm, lrn, customsOfficeList.customsOffices, country.description, mode, index)))
            }
        }
        .getOrElse(
          Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
        )
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      request.userAnswers
        .get(OfficeOfTransitCountryPage(index))
        .map {
          country =>
            service.getCustomsOfficesForCountry(country.code).flatMap {
              customsOfficeList =>
                val form = formProvider("routeDetails.transit.officeOfTransit", customsOfficeList, country.description)
                form
                  .bindFromRequest()
                  .fold(
                    formWithErrors =>
                      Future.successful(BadRequest(view(formWithErrors, lrn, customsOfficeList.customsOffices, country.description, mode, index))),
                    value => OfficeOfTransitPage(index).writeToUserAnswers(value).writeToSession().navigateWith(mode)
                  )
            }
        }
        .getOrElse(
          Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
        )
  }
}