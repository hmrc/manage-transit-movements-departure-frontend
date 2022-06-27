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

import controllers.traderDetails.holderOfTransit.contact.{routes => contactRoutes}
import controllers.traderDetails.holderOfTransit.routes._
import models.{Address, Mode, UserAnswers}
import pages.traderDetails.holderOfTransit._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class HolderOfTransitCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers) {

  def tirIdentificationYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = TirIdentificationYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.holderOfTransit.tirIdentificationYesNo",
    id = Some("has-transit-holder-tir-id"),
    call = TirIdentificationYesNoController.onPageLoad(lrn, mode)
  )

  def tirIdentification: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = TirIdentificationPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.holderOfTransit.tirIdentification",
    id = Some("transit-holder-tir-id-number"),
    call = TirIdentificationController.onPageLoad(lrn, mode)
  )

  def eoriYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = EoriYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.holderOfTransit.eoriYesNo",
    id = Some("has-transit-holder-eori"),
    call = EoriYesNoController.onPageLoad(lrn, mode)
  )

  def eori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = EoriPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.holderOfTransit.eori",
    id = Some("transit-holder-eori-number"),
    call = EoriController.onPageLoad(lrn, mode)
  )

  def name: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = NamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.holderOfTransit.name",
    id = Some("transit-holder-name"),
    call = NameController.onPageLoad(lrn, mode)
  )

  def address: Option[SummaryListRow] = getAnswerAndBuildRow[Address](
    page = AddressPage,
    formatAnswer = formatAsAddress,
    prefix = "traderDetails.holderOfTransit.address",
    id = Some("transit-holder-address"),
    call = AddressController.onPageLoad(lrn, mode)
  )

  def addContact: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddContactPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.holderOfTransit.addContact",
    id = Some("has-transit-holder-contact"),
    call = AddContactController.onPageLoad(lrn, mode)
  )

  def contactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = contact.NamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.holderOfTransit.contact.name",
    id = Some("transit-holder-contact-name"),
    call = contactRoutes.NameController.onPageLoad(lrn, mode)
  )

  def contactTelephoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = contact.TelephoneNumberPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.holderOfTransit.contact.telephoneNumber",
    id = Some("transit-holder-contact-phone-number"),
    call = contactRoutes.TelephoneNumberController.onPageLoad(lrn, mode)
  )
}
