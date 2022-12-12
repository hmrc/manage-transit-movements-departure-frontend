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

package controllers.transport.transportMeans

import controllers.actions._
import models.{LocalReferenceNumber, Mode}
import navigation.transport.TransportNavigatorProvider
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.transport.transportMeans.TransportMeansAnswersViewModel.TransportMeansAnswersViewModelProvider
import views.html.transport.transportMeans.TransportMeansCheckYourAnswersView

import javax.inject.Inject

class TransportMeansCheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  navigatorProvider: TransportNavigatorProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: TransportMeansCheckYourAnswersView,
  viewModelProvider: TransportMeansAnswersViewModelProvider
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val sections = viewModelProvider(request.userAnswers, mode).sections
      Ok(view(lrn, mode, sections))
  }

  def onSubmit(lrn: LocalReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      Redirect(navigatorProvider(mode).nextPage(request.userAnswers))
  }
}
