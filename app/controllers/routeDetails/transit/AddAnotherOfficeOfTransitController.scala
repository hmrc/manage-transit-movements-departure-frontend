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

import config.FrontendAppConfig
import controllers.actions._
import controllers.routeDetails.transit.index.{routes => indexRoutes}
import controllers.routes.TaskListController
import forms.AddAnotherFormProvider
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, NormalMode}
import navigation.Navigator
import navigation.annotations.routeDetails.Transit
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.routeDetails.transit.AddAnotherOfficeOfTransitViewModel.AddAnotherOfficeOfTransitViewModelProvider
import views.html.routeDetails.transit.AddAnotherOfficeOfTransitView

import javax.inject.Inject

class AddAnotherOfficeOfTransitController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @Transit implicit val navigator: Navigator,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  config: FrontendAppConfig,
  viewModelProvider: AddAnotherOfficeOfTransitViewModelProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherOfficeOfTransitView
) extends FrontendBaseController
    with I18nSupport {

  private def form(allowMoreOfficesOfTransit: Boolean): Form[Boolean] =
    formProvider("routeDetails.transit.addAnotherOfficeOfTransit", allowMoreOfficesOfTransit)

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val (officesOfTransit, numberOfOfficesOfTransit, allowMoreOfficesOfTransit) = viewData
      numberOfOfficesOfTransit match {
        case 0 => Redirect(routes.AddOfficeOfTransitYesNoController.onPageLoad(lrn, NormalMode))
        case _ => Ok(view(form(allowMoreOfficesOfTransit), lrn, officesOfTransit, allowMoreOfficesOfTransit))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val (officesOfTransit, numberOfOfficesOfTransit, allowMoreOfficesOfTransit) = viewData
      form(allowMoreOfficesOfTransit)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, lrn, officesOfTransit, allowMoreOfficesOfTransit)),
          {
            case true  => Redirect(indexRoutes.OfficeOfTransitCountryController.onPageLoad(lrn, NormalMode, Index(numberOfOfficesOfTransit)))
            case false => Redirect(TaskListController.onPageLoad(lrn))
          }
        )
  }

  private def viewData(implicit request: DataRequest[_]): (Seq[ListItem], Int, Boolean) = {
    val officesOfTransit         = viewModelProvider.apply(request.userAnswers).listItems
    val numberOfOfficesOfTransit = officesOfTransit.length
    (officesOfTransit, numberOfOfficesOfTransit, numberOfOfficesOfTransit < config.maxOfficesOfTransit)
  }
}