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

package controllers.test

import com.google.inject.Inject
import controllers.actions.Actions
import models.{Index, LocalReferenceNumber}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.test.TestViewModel.TestSectionViewModel
import views.html.test.CheckYourAnswersView

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView,
  viewModel: TestSectionViewModel
)() extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(lrn: LocalReferenceNumber, fooIndex: Index, barIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      val sections = viewModel(request.userAnswers, fooIndex, barIndex)
      Ok(view(lrn, sections, fooIndex, barIndex))
  }

  def onSubmit(lrn: LocalReferenceNumber, fooIndex: Index, barIndex: Index): Action[AnyContent] = actions.requireData(lrn) {
    Redirect(controllers.routes.TaskListController.onPageLoad(lrn))
  }

}
