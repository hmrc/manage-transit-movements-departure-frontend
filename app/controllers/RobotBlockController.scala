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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject

class RobotBlockController @Inject() (
  val controllerComponents: MessagesControllerComponents
) extends FrontendBaseController
    with I18nSupport {

  // Return 410, and an additional noindex header to stop robots from indexing the url associated to this controller
  def onPageLoad(path: String): Action[AnyContent] = Action {
    Gone.withHeaders("X-Robots-Tag" -> "noindex")
  }
}
