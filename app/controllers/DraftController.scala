/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers

import config.FrontendAppConfig
import controllers.actions.Actions
import models.journeyDomain.{PreTaskListDomain, UserAnswersReader}
import models.{LocalReferenceNumber, NormalMode}
import navigation.PreTaskListNavigatorProvider
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject

class DraftController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  actions: Actions,
  val frontendAppConfig: FrontendAppConfig,
  navigatorProvider: PreTaskListNavigatorProvider
) extends FrontendBaseController {

  def draftRedirect(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      UserAnswersReader[PreTaskListDomain](frontendAppConfig.isPreLodgeEnabled).run(request.userAnswers) match {
        case Left(_) =>
          Redirect(navigatorProvider(NormalMode).nextPage(request.userAnswers, None))
        case Right(_) =>
          Redirect(controllers.routes.TaskListController.onPageLoad(lrn))
      }
  }

}
