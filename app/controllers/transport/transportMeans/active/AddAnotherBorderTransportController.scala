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

package controllers.transport.transportMeans.active

import config.FrontendAppConfig
import controllers.actions._
import forms.YesNoFormProvider
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode, NormalMode}
import navigation.transport.TransportMeansNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ListItem
import viewModels.transport.transportMeans.active.AddAnotherBorderTransportViewModel.AddAnotherBorderTransportViewModelProvider
import views.html.transport.transportMeans.active.AddAnotherBorderTransportView

import javax.inject.Inject

class AddAnotherBorderTransportController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: TransportMeansNavigatorProvider,
  actions: Actions,
  formProvider: YesNoFormProvider,
  config: FrontendAppConfig,
  viewModelProvider: AddAnotherBorderTransportViewModelProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherBorderTransportView
) extends FrontendBaseController
    with I18nSupport {

  private def form(allowMoreActiveBorderTransports: Boolean): Form[Boolean] =
    formProvider("transport.transportMeans.active.addAnotherBorderTransport", allowMoreActiveBorderTransports)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val (activeBorderTransports, numberOfActiveBorderTransports, allowMoreActiveBorderTransports) = viewData
      numberOfActiveBorderTransports match {
        case 0 => Redirect(controllers.transport.transportMeans.routes.AnotherVehicleCrossingYesNoController.onPageLoad(lrn, mode))
        case _ => Ok(view(form(allowMoreActiveBorderTransports), lrn, mode, activeBorderTransports, allowMoreActiveBorderTransports))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      lazy val (activeBorderTransports, numberOfActiveBorderTransports, allowMoreActiveBorderTransports) = viewData
      form(allowMoreActiveBorderTransports)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, mode, activeBorderTransports, allowMoreActiveBorderTransports)),
          {
            case true  => Redirect(routes.IdentificationController.onPageLoad(lrn, NormalMode, Index(numberOfActiveBorderTransports)))
            case false => ??? // TODO: Redirect to CYA page
          }
        )
  }

  private def viewData(implicit request: DataRequest[_]): (Seq[ListItem], Int, Boolean) = {
    val activeBorderTransports         = viewModelProvider.apply(request.userAnswers).listItems
    val numberOfActiveBorderTransports = activeBorderTransports.length
    (activeBorderTransports, numberOfActiveBorderTransports, numberOfActiveBorderTransports < config.maxActiveBorderTransports)
  }
}
