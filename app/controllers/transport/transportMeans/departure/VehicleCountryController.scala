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

package controllers.transport.transportMeans.departure

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.NationalityFormProvider
import models.journeyDomain.transport.TransportDomain
import models.{LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.transport.TransportMeansNavigatorProvider
import pages.transport.transportMeans.departure.VehicleCountryPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.NationalitiesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.transport.transportMeans.departure.VehicleCountryView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class VehicleCountryController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TransportMeansNavigatorProvider,
  actions: Actions,
  formProvider: NationalityFormProvider,
  service: NationalitiesService,
  val controllerComponents: MessagesControllerComponents,
  view: VehicleCountryView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getNationalities().map {
        nationalityList =>
          val form = formProvider("transport.transportMeans.departure.vehicleCountry", nationalityList)
          val preparedForm = request.userAnswers.get(VehicleCountryPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, lrn, nationalityList.nationalities, mode))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.getNationalities().flatMap {
        nationalityList =>
          val form = formProvider("transport.transportMeans.departure.vehicleCountry", nationalityList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, nationalityList.nationalities, mode))),
              value => {
                implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
                VehicleCountryPage.writeToUserAnswers(value).updateTask[TransportDomain]().writeToSession().navigate()
              }
            )
      }
  }
}
