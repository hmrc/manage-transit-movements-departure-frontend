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

package pages.transport.transportMeans.departure

import controllers.transport.transportMeans.departure.routes
import models.{InlandMode, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.transport.TransportMeansSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object InlandModePage extends QuestionPage[InlandMode] {

  override def path: JsPath = TransportMeansSection.path \ toString

  override def toString: String = "inlandMode"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.InlandModeController.onPageLoad(userAnswers.lrn, mode))
}
