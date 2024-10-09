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

import config.RenderConfig
import controllers.actions.Actions
import models.LocalReferenceNumber
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.LockService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DeleteLockController @Inject() (
  actions: Actions,
  cc: MessagesControllerComponents,
  lockService: LockService,
  renderConfig: RenderConfig
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport
    with Logging {

  def delete(lrn: LocalReferenceNumber): Action[AnyContent] = actions.requireData(lrn).async {
    implicit request =>
      lockService.deleteLock(request.userAnswers).map {
        _ =>
          Redirect(renderConfig.signOutUrl)
      } recover {
        case exception =>
          logger.info("Failed to unlock session", exception)
          Redirect(renderConfig.signOutUrl)
      }
  }
}
