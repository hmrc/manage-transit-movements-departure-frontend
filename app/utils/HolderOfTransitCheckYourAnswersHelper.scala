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

package utils

import controllers.traderDetails.holderOfTransit.routes._
import models.{Address, Mode, UserAnswers}
import pages.traderDetails.holderOfTransit._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow

class HolderOfTransitCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers) {

  def eoriYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = EoriYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.holderOfTransit.eoriYesNo",
    id = None,
    call = EoriYesNoController.onPageLoad(lrn, mode)
  )

  def eori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = EoriPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.holderOfTransit.eori",
    id = None,
    call = EoriController.onPageLoad(lrn, mode)
  )

  def name: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = NamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.holderOfTransit.name",
    id = None,
    call = NameController.onPageLoad(lrn, mode)
  )

  def address: Option[SummaryListRow] = getAnswerAndBuildRow[Address](
    page = AddressPage,
    formatAnswer = formatAsAddress,
    prefix = "traderDetails.holderOfTransit.address",
    id = None,
    call = AddressController.onPageLoad(lrn, mode)
  )

  def addContact: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddContactPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.holderOfTransit.addContact",
    id = None,
    call = AddContactController.onPageLoad(lrn, mode)
  )

  def contactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ContactNamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.holderOfTransit.contactName",
    id = None,
    call = ContactNameController.onPageLoad(lrn, mode)
  )
}
