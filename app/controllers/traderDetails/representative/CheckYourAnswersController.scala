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

package controllers.traderDetails.representative

import com.google.inject.Inject
import controllers.actions.Actions
import models.{DeclarationType, LocalReferenceNumber, Mode, NormalMode, UserAnswers}
import pages.preTaskList.DeclarationTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.traderDetails.RepresentativeViewModel.RepresentativeSubSectionViewModel
import views.html.traderDetails.representative.CheckYourAnswersView

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView,
  viewModel: RepresentativeSubSectionViewModel
)() extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val sections = viewModel(request.userAnswers)
      Ok(view(lrn, sections))
  }

  def onSubmit(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      declarationTypeRoute(request.userAnswers, NormalMode)
  }

  private def declarationTypeRoute(ua: UserAnswers, mode: Mode): Result =
    ua.get(DeclarationTypePage) match {
      case Some(DeclarationType.Option4) => Redirect(controllers.traderDetails.consignment.consignor.routes.EoriYesNoController.onPageLoad(ua.lrn, mode))
      case _                             => Redirect(controllers.traderDetails.consignment.routes.ApprovedOperatorController.onPageLoad(ua.lrn, mode))
    }
}
