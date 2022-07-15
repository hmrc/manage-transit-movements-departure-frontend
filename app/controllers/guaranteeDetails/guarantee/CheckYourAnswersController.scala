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

package controllers.guaranteeDetails.guarantee

import com.google.inject.Inject
import controllers.actions.{Actions, SpecificDataRequiredActionProvider}
import controllers.guaranteeDetails.routes._
import controllers.routes._
import models.DeclarationType.Option4
import models.{Index, LocalReferenceNumber}
import pages.preTaskList.DeclarationTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.guaranteeDetails.GuaranteeViewModel.GuaranteeViewModelProvider
import views.html.guaranteeDetails.guarantee.CheckYourAnswersView

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView,
  viewModelProvider: GuaranteeViewModelProvider
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, index: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val section = viewModelProvider.apply(request.userAnswers, index).section
      Ok(view(lrn, index, Seq(section)))
  }

  def onSubmit(lrn: LocalReferenceNumber, index: Index): Action[AnyContent] = actions
    .requireData(lrn)
    .andThen(getMandatoryPage(DeclarationTypePage)) {
      implicit request =>
        request.arg match {
          case Option4 => Redirect(TaskListController.onPageLoad(lrn))
          case _       => Redirect(AddAnotherGuaranteeController.onPageLoad(lrn))
        }
    }
}
