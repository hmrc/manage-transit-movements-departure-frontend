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

package controllers.routeDetails.locationOfGoods

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.CustomsOfficeFormProvider
import models.{LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import pages.preTaskList.OfficeOfDeparturePage
import pages.routeDetails.exit.index.OfficeOfExitCountryPage
import pages.routeDetails.locationOfGoods.LocationOfGoodsCustomsOfficeIdentifierPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CustomsOfficesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.locationOfGoods.LocationOfGoodsCustomsOfficeIdentifierView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LocationOfGoodsCustomsOfficeIdentifierController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @PreTaskListDetails implicit val navigator: Navigator,
  actions: Actions,
  formProvider: CustomsOfficeFormProvider,
  service: CustomsOfficesService,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: LocationOfGoodsCustomsOfficeIdentifierView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(OfficeOfDeparturePage))
    .async {
      implicit request =>
        val office = request.arg
        service.getCustomsOfficesOfDepartureForCountry(office.countryCode).map {
          customsOfficeList =>
            val form = formProvider("routeDetails.locationOfGoods.locationOfGoodsCustomsOfficeIdentifier", customsOfficeList)
            val preparedForm = request.userAnswers.get(LocationOfGoodsCustomsOfficeIdentifierPage) match {
              case None        => form
              case Some(value) => form.fill(value)
            }

            Ok(view(preparedForm, lrn, customsOfficeList.customsOffices, mode))
        }
    }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(OfficeOfDeparturePage))
    .async {
      implicit request =>
        val office = request.arg
        service.getCustomsOfficesOfDepartureForCountry(office.countryCode).flatMap {
          customsOfficeList =>
            val form = formProvider("routeDetails.locationOfGoods.locationOfGoodsCustomsOfficeIdentifier", customsOfficeList)
            form
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, customsOfficeList.customsOffices, mode))),
                value => LocationOfGoodsCustomsOfficeIdentifierPage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
              )
        }
    }
}
