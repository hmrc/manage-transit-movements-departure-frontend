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
import models.{LocalReferenceNumber, SubmissionState}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AmendController @Inject() (
  cc: MessagesControllerComponents,
  actions: Actions,
  sessionRepository: SessionRepository
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport {

  def amendErrors(lrn: LocalReferenceNumber, departureId: String): Action[AnyContent] =
    amend(lrn, departureId, SubmissionState.Amendment)

  def amendGuaranteeErrors(lrn: LocalReferenceNumber, departureId: String): Action[AnyContent] =
    amend(lrn, departureId, SubmissionState.GuaranteeAmendment)

  private def amend(lrn: LocalReferenceNumber, departureId: String, status: SubmissionState.Value): Action[AnyContent] = actions
    .requireData(lrn)
    .async {
      implicit request =>
        sessionRepository
          .set(
            request.userAnswers
              .copy(departureId = Some(departureId))
              .copy(status = status)
          )
          .map {
            case true =>
              Redirect(controllers.routes.TaskListController.onPageLoad(lrn).url)
            case false =>
              Redirect(controllers.routes.ErrorController.technicalDifficulties().url)
          }
    }
}
