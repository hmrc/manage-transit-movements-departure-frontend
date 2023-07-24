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

import controllers.actions.Actions
import models.domain.UserAnswersReader
import models.journeyDomain.PreTaskListDomain
import models.{LocalReferenceNumber, NormalMode}
import navigation.PreTaskListNavigatorProvider
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.DuplicateService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DraftIndexController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  navigatorProvider: PreTaskListNavigatorProvider,
  val controllerComponents: MessagesControllerComponents,
  service: DuplicateService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def index(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      service.doesSubmissionExistForLrn(lrn).map {
        case true => Redirect(controllers.routes.DuplicateDraftLocalReferenceNumberController.onPageLoad(lrn))
        case false =>
          UserAnswersReader[PreTaskListDomain].run(request.userAnswers) match {
            case Left(value) =>
              Redirect(navigatorProvider(NormalMode).nextPage(request.userAnswers))
            case Right(value) =>
              Redirect(controllers.routes.TaskListController.onPageLoad(lrn))
          }
      }
  }

}
