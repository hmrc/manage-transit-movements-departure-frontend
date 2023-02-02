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

package utils.cyaHelpers.guaranteeDetails

import models.GuaranteeType._
import models.reference.CurrencyCode
import models.{GuaranteeType, Index, Mode, UserAnswers}
import pages.guaranteeDetails.guarantee._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class GuaranteeCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode, index: Index)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def guaranteeType: Option[SummaryListRow] = getAnswerAndBuildRowWithDynamicLink[GuaranteeType](
    page = GuaranteeTypePage(index),
    formatAnswer = formatEnumAsText(GuaranteeType.messageKeyPrefix),
    prefix = "guaranteeDetails.guarantee.guaranteeType",
    id = Some("change-type")
  )(_ == TIRGuarantee)

  def guaranteeReferenceNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ReferenceNumberPage(index),
    formatAnswer = formatAsText,
    prefix = "guaranteeDetails.guarantee.referenceNumber",
    id = Some("change-reference-number")
  )

  def otherReferenceYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = OtherReferenceYesNoPage(index),
    formatAnswer = formatAsYesOrNo,
    prefix = "guaranteeDetails.guarantee.otherReferenceYesNo",
    id = Some("change-add-other-reference")
  )

  def otherReference: Option[SummaryListRow] =
    (userAnswers.get(GuaranteeTypePage(index)) match {
      case Some(CashDepositGuarantee)                 => Some("option3")
      case Some(GuaranteeNotRequiredExemptPublicBody) => Some("option8")
      case _                                          => None
    }).flatMap {
      key =>
        getAnswerAndBuildRow[String](
          page = OtherReferencePage(index),
          formatAnswer = formatAsText,
          prefix = s"guaranteeDetails.guarantee.otherReference.$key",
          id = Some("change-other-reference")
        )
    }

  def accessCode: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = AccessCodePage(index),
    formatAnswer = formatAsPassword,
    prefix = "guaranteeDetails.guarantee.accessCode",
    id = Some("change-access-code")
  )

  def liabilityCurrency: Option[SummaryListRow] = getAnswerAndBuildRow[CurrencyCode](
    page = CurrencyPage(index),
    formatAnswer = formatAsText,
    prefix = "guaranteeDetails.guarantee.currency",
    id = Some("change-liability-currency")
  )

  def liabilityAmount: Option[SummaryListRow] = getAnswerAndBuildRow[BigDecimal](
    page = LiabilityAmountPage(index),
    formatAnswer = formatAsCurrency,
    prefix = "guaranteeDetails.guarantee.liabilityAmount",
    id = Some("change-liability-amount")
  )

}
