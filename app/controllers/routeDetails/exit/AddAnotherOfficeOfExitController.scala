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

package controllers.routeDetails.exit

import config.FrontendAppConfig
import controllers.actions._
import controllers.routeDetails.exit.index.{routes => indexRoutes}
import forms.AddAnotherFormProvider
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, Mode}
import navigation.routeDetails.RouteDetailsNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ListItem
import viewModels.routeDetails.exit.AddAnotherOfficeOfExitViewModel.AddAnotherOfficeOfExitViewModelProvider
import views.html.routeDetails.exit.AddAnotherOfficeOfExitView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddAnotherOfficeOfExitController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: RouteDetailsNavigatorProvider,
  actions: Actions,
  formProvider: AddAnotherFormProvider,
  config: FrontendAppConfig,
  viewModelProvider: AddAnotherOfficeOfExitViewModelProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherOfficeOfExitView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(allowMoreOfficesOfExit: Boolean): Form[Boolean] =
    formProvider("routeDetails.exit.addAnotherOfficeOfExit", allowMoreOfficesOfExit)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      lazy val (officesOfExit, numberOfOfficesOfExit, allowMoreOfficesOfExit) = viewData(mode)
      numberOfOfficesOfExit match {
        case 0 =>
          navigatorProvider(mode).map {
            navigator => Redirect(navigator.nextPage(request.userAnswers))
          }
        case _ => Future.successful(Ok(view(form(allowMoreOfficesOfExit), lrn, mode, officesOfExit, allowMoreOfficesOfExit)))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      lazy val (officesOfExit, numberOfOfficesOfExit, allowMoreOfficesOfExit) = viewData(mode)
      form(allowMoreOfficesOfExit)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, mode, officesOfExit, allowMoreOfficesOfExit))),
          {
            case true =>
              Future.successful(
                Redirect(indexRoutes.OfficeOfExitCountryController.onPageLoad(lrn, Index(numberOfOfficesOfExit), mode))
              )
            case false =>
              navigatorProvider(mode).map {
                navigator => Redirect(navigator.nextPage(request.userAnswers))
              }
          }
        )
  }

  private def viewData(mode: Mode)(implicit request: DataRequest[_]): (Seq[ListItem], Int, Boolean) = {
    val officesOfExit         = viewModelProvider.apply(request.userAnswers, mode).listItems
    val numberOfOfficesOfExit = officesOfExit.length
    (officesOfExit, numberOfOfficesOfExit, numberOfOfficesOfExit < config.maxOfficesOfExit)
  }
}
