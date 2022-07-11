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

package utils.cyaHelpers.traderDetails

import models.{Address, Mode, UserAnswers}
import pages.traderDetails.holderOfTransit._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class HolderOfTransitCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def tirIdentificationYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = TirIdentificationYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.holderOfTransit.tirIdentificationYesNo",
    id = Some("has-transit-holder-tir-id")
  )

  def tirIdentification: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = TirIdentificationPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.holderOfTransit.tirIdentification",
    id = Some("transit-holder-tir-id-number")
  )

  def eoriYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = EoriYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.holderOfTransit.eoriYesNo",
    id = Some("has-transit-holder-eori")
  )

  def eori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = EoriPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.holderOfTransit.eori",
    id = Some("transit-holder-eori-number")
  )

  def name: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = NamePage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.holderOfTransit.name",
    id = Some("transit-holder-name")
  )

  def address: Option[SummaryListRow] = getAnswerAndBuildRow[Address](
    page = AddressPage,
    formatAnswer = formatAsAddress,
    prefix = "traderDetails.holderOfTransit.address",
    id = Some("transit-holder-address")
  )

  def addContact: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddContactPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.holderOfTransit.addContact",
    id = Some("has-transit-holder-contact")
  )

  def contactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = contact.NamePage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.holderOfTransit.contact.name",
    id = Some("transit-holder-contact-name")
  )

  def contactTelephoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = contact.TelephoneNumberPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.holderOfTransit.contact.telephoneNumber",
    id = Some("transit-holder-contact-phone-number")
  )
}
