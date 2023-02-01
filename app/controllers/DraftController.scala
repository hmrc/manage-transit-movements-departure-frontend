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

package controllers

import controllers.actions.{Actions, IdentifierAction}
import models.{domain, DeclarationType, LocalReferenceNumber, NormalMode}
import models.domain.UserAnswersReader
import models.journeyDomain.PreTaskListDomain
import navigation.PreTaskListNavigatorProvider
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.Future

class DraftController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  actions: Actions,
  navigatorProvider: PreTaskListNavigatorProvider
) extends FrontendBaseController {

  def draftRedirect(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn) {
    implicit request =>
      UserAnswersReader[PreTaskListDomain].run(request.userAnswers) match {
        case Left(value) =>
          Redirect(navigatorProvider(NormalMode).nextPage(request.userAnswers))
        case Right(value) =>
          Redirect(controllers.routes.TaskListController.onPageLoad(lrn))
      }

  }
}
