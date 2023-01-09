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

package pages.transport.transportMeans

import controllers.transport.transportMeans.routes
import models.transport.transportMeans.BorderModeOfTransport
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.transport.{TransportMeansActiveSection, TransportSection}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object BorderModeOfTransportPage extends QuestionPage[BorderModeOfTransport] {

  override def path: JsPath = TransportSection.path \ toString

  override def toString: String = "borderModeOfTransport"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.BorderModeOfTransportController.onPageLoad(userAnswers.lrn, mode))

  override def cleanup(value: Option[BorderModeOfTransport], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(_) =>
        userAnswers.remove(TransportMeansActiveSection(Index(0)))
      case None =>
        super.cleanup(value, userAnswers)
    }
}
