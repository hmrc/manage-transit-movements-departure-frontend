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

package controllers.routeDetails.routing

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.CustomsOfficeFormProvider
import models.reference.CustomsOffice
import models.{CustomsOfficeList, LocalReferenceNumber, Mode}
import navigation.Navigator
import navigation.annotations.routeDetails.Routing
import pages.routeDetails.routing.OfficeOfDestinationPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CustomsOfficesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.routeDetails.routing.OfficeOfDestinationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfDestinationController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @Routing implicit val navigator: Navigator,
  actions: Actions,
  formProvider: CustomsOfficeFormProvider,
  service: CustomsOfficesService,
  val controllerComponents: MessagesControllerComponents,
  view: OfficeOfDestinationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(customsOfficeList: CustomsOfficeList): Form[CustomsOffice] =
    formProvider("routeDetails.routing.officeOfDestination", customsOfficeList)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getCustomsOfficesOfDeparture.map {
        customsOfficeList =>
          val preparedForm = request.userAnswers.get(OfficeOfDestinationPage) match {
            case None        => form(customsOfficeList)
            case Some(value) => form(customsOfficeList).fill(value)
          }

          Ok(view(preparedForm, lrn, customsOfficeList.customsOffices, mode))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getCustomsOfficesOfDeparture.flatMap {
        customsOfficeList =>
          form(customsOfficeList)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, customsOfficeList.customsOffices, mode))),
              value => OfficeOfDestinationPage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
            )
      }
  }
}
