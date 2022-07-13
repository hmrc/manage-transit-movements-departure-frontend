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

package utils.cyaHelpers.guaranteeDetails

import models.guaranteeDetails.GuaranteeType
import models.guaranteeDetails.GuaranteeType._
import models.{Index, Mode, UserAnswers}
import pages.guaranteeDetails._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class GuaranteeCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode, index: Index)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def guaranteeType: Option[SummaryListRow] = getAnswerAndBuildRowWithDynamicLink[GuaranteeType](
    page = GuaranteeTypePage(index),
    formatAnswer = formatEnumAsText(GuaranteeType.messageKeyPrefix),
    prefix = "guaranteeDetails.guaranteeType",
    id = Some("type")
  )(_ == TIRGuarantee)

  def guaranteeReferenceNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ReferenceNumberPage(index),
    formatAnswer = formatAsText,
    prefix = "guaranteeDetails.referenceNumber",
    id = Some("reference-number")
  )

  def otherReferenceYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = OtherReferenceYesNoPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "guaranteeDetails.otherReferenceYesNo",
    id = Some("add-other-reference")
  )

  def otherReference: Option[SummaryListRow] =
    (userAnswers.get(GuaranteeTypePage(index)) match {
      case Some(CashDepositGuarantee)                 => Some("guaranteeDetails.otherReference.option3")
      case Some(GuaranteeNotRequiredExemptPublicBody) => Some("guaranteeDetails.otherReference.option8")
      case _                                          => None
    }).flatMap {
      prefix =>
        getAnswerAndBuildRow[String](
          page = OtherReferencePage(index),
          formatAnswer = formatAsText,
          prefix = prefix,
          id = Some("other-reference")
        )
    }

  def accessCode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = AccessCodePage(index),
    formatAnswer = formatAsPassword,
    prefix = "guaranteeDetails.accessCode",
    id = Some("access-code")
  )

  def liabilityAmount: Option[SummaryListRow] = getAnswerAndBuildRow[BigDecimal](
    page = LiabilityAmountPage(index),
    formatAnswer = formatAsCurrency,
    prefix = "guaranteeDetails.liabilityAmount",
    id = Some("liability-amount")
  )
}
