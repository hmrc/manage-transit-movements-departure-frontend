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

package pages.preTaskList

import config.Constants.DeclarationType.TIR
import models.reference.DeclarationType
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.PreTaskListSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case object DeclarationTypePage extends QuestionPage[DeclarationType] {

  override def path: JsPath = PreTaskListSection.path \ toString

  override def toString: String = "declarationType"

  override def cleanup(value: Option[DeclarationType], userAnswers: UserAnswers): Try[UserAnswers] =
    value.map(_.code) match {
      case Some(declarationType) if declarationType != TIR => userAnswers.remove(TIRCarnetReferencePage)
      case _                                               => super.cleanup(value, userAnswers)
    }

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(controllers.preTaskList.routes.DeclarationTypeController.onPageLoad(userAnswers.lrn, mode))
}
