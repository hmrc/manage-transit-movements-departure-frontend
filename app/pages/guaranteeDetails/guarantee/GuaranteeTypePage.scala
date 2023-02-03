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

package pages.guaranteeDetails.guarantee

import controllers.guaranteeDetails.guarantee.routes
import models.DeclarationType.Option4
import models.{GuaranteeType, Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.preTaskList.DeclarationTypePage
import pages.sections.guaranteeDetails.GuaranteeSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class GuaranteeTypePage(index: Index) extends QuestionPage[GuaranteeType] {

  override def path: JsPath = GuaranteeSection(index).path \ toString

  override def toString: String = "guaranteeType"

  override def cleanup(value: Option[GuaranteeType], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(_) =>
        userAnswers
          .remove(ReferenceNumberPage(index))
          .flatMap(_.remove(AccessCodePage(index)))
          .flatMap(_.remove(CurrencyPage(index)))
          .flatMap(_.remove(LiabilityAmountPage(index)))
          .flatMap(_.remove(OtherReferenceYesNoPage(index)))
          .flatMap(_.remove(OtherReferencePage(index)))
      case None => super.cleanup(value, userAnswers)
    }

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    userAnswers.get(DeclarationTypePage) map {
      case Option4 => controllers.guaranteeDetails.routes.GuaranteeAddedTIRController.onPageLoad(userAnswers.lrn)
      case _       => routes.GuaranteeTypeController.onPageLoad(userAnswers.lrn, mode, index)
    }
}
