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

package pages.transport.transportMeans.active

import controllers.transport.transportMeans.active.routes
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.transport.TransportMeansActiveSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class ConveyanceReferenceNumberYesNoPage(index: Index) extends QuestionPage[Boolean] {

  override def path: JsPath = TransportMeansActiveSection(index).path \ toString

  override def toString: String = "conveyanceReferenceNumberYesNo"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.ConveyanceReferenceNumberYesNoController.onPageLoad(userAnswers.lrn, mode, index))

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(false) => userAnswers.remove(ConveyanceReferenceNumberPage(index))
      case _           => super.cleanup(value, userAnswers)
    }
}
