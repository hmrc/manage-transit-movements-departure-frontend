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

package pages.guaranteeDetails

import models.DeclarationType.Option4
import models.guaranteeDetails.GuaranteeType
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.preTaskList.DeclarationTypePage
import pages.sections.GuaranteeSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class GuaranteeTypePage(index: Index) extends QuestionPage[GuaranteeType] {

  override def path: JsPath = GuaranteeSection(index).path \ toString

  override def toString: String = "guaranteeType"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    userAnswers.get(DeclarationTypePage) map {
      case Option4 => controllers.guaranteeDetails.routes.GuaranteeAddedTIRController.onPageLoad(userAnswers.lrn)
      case _       => controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(userAnswers.lrn, mode, index)
    }
}
