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

package controllers.routeDetails.officeOfExit

import config.FrontendAppConfig
import controllers.actions._
import forms.AddAnotherFormProvider
import models.requests.DataRequest
import models.{Index, LocalReferenceNumber, NormalMode}
import navigation.routeDetails.RouteDetailsNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ListItem
import viewModels.routeDetails.officeOfExit.AddAnotherOfficeOfExitViewModel.AddAnotherOfficeOfExitViewModelProvider
import views.html.routeDetails.officeOfExit.AddAnotherOfficeOfExitView

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
    formProvider("routeDetails.officeOfExit.addAnotherOfficeOfExit", allowMoreOfficesOfExit)

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      viewData flatMap {
        case (officesOfExit, numberOfOfficesOfExit, allowMoreOfficesOfExit) =>
          numberOfOfficesOfExit match {
            case 0 =>
              navigatorProvider() map {
                implicit navigator =>
                  Redirect(navigator.nextPage(request.userAnswers, NormalMode))
              }
            case _ => Future.successful(Ok(view(form(allowMoreOfficesOfExit), lrn, officesOfExit, allowMoreOfficesOfExit)))
          }
      }
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      viewData flatMap {
        case (officesOfExit, numberOfOfficesOfExit, allowMoreOfficesOfExit) =>
          form(allowMoreOfficesOfExit)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, officesOfExit, allowMoreOfficesOfExit))),
              {
                case true =>
                  Future.successful(Redirect(routes.OfficeOfExitCountryController.onPageLoad(lrn, Index(numberOfOfficesOfExit), NormalMode)))
                case false =>
                  navigatorProvider() map {
                    implicit navigator =>
                      Redirect(navigator.nextPage(request.userAnswers, NormalMode))
                  }
              }
            )
      }
  }

  private def viewData(implicit request: DataRequest[_], ec: ExecutionContext): Future[(Seq[ListItem], Int, Boolean)] =
    viewModelProvider.apply(request.userAnswers).map {
      viewModel =>
        val officesOfExit       = viewModel.listItems
        val numberOfOfficesExit = officesOfExit.length
        (officesOfExit, numberOfOfficesExit, numberOfOfficesExit < config.maxOfficesOfTransit)
    }
}
