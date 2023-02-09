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
import models.{LocalReferenceNumber, Mode}
import navigation.UserAnswersNavigator
import navigation.routeDetails.RouteDetailsNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.routeDetails.exit.AddAnotherOfficeOfExitViewModel
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
  viewModelProvider: AddAnotherOfficeOfExitViewModelProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherOfficeOfExitView,
  countriesService: CountriesService
)(implicit ec: ExecutionContext, config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherOfficeOfExitViewModel): Form[Boolean] =
    formProvider(viewModel.prefix, viewModel.allowMore)

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode)
      viewModel.count match {
        case 0 => redirectToNextPage(mode)
        case _ => Future.successful(Ok(view(form(viewModel), lrn, viewModel)))
      }
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      val viewModel = viewModelProvider(request.userAnswers, mode)
      form(viewModel)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, lrn, viewModel))),
          {
            case true =>
              Future.successful(
                Redirect(indexRoutes.OfficeOfExitCountryController.onPageLoad(lrn, viewModel.nextIndex, mode))
              )
            case false => redirectToNextPage(mode)
          }
        )
  }

  private def redirectToNextPage(mode: Mode)(implicit request: DataRequest[_]): Future[Result] =
    for {
      ctcCountries                          <- countriesService.getCountryCodesCTC()
      customsSecurityAgreementAreaCountries <- countriesService.getCustomsSecurityAgreementAreaCountries()
    } yield {
      val navigator: UserAnswersNavigator = navigatorProvider(mode, ctcCountries, customsSecurityAgreementAreaCountries)
      Redirect(navigator.nextPage(request.userAnswers))
    }
}
