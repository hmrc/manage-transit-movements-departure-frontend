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

package controllers.routeDetails.routing

import com.google.inject.Inject
import controllers.actions.Actions
import models.{LocalReferenceNumber, Mode}
import navigation.routeDetails.RouteDetailsNavigatorProvider
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.routeDetails.routing.RoutingAnswersViewModel.RoutingAnswersViewModelProvider
import views.html.routeDetails.routing.CheckYourAnswersView

import scala.concurrent.ExecutionContext

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  navigatorProvider: RouteDetailsNavigatorProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView,
  viewModelProvider: RoutingAnswersViewModelProvider
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val sections = viewModelProvider(request.userAnswers, mode).sections
      Ok(view(lrn, mode, sections))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      navigatorProvider(mode).map {
        navigator => Redirect(navigator.nextPage(request.userAnswers))
      }
  }

}
