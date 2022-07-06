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

import controllers.traderDetails.consignment.consignor.contact.{routes => contactRoutes}
import controllers.traderDetails.consignment.consignor.{routes => consignorRoutes}
import controllers.traderDetails.consignment.consignee.{routes => consigneeRoutes}
import controllers.traderDetails.consignment.{routes => consignmentRoutes}
import models.{Address, Mode, UserAnswers}
import pages.traderDetails.consignment._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class TraderDetailsConsignmentCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers) {

  def approvedOperator: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = ApprovedOperatorPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.approvedOperator",
    id = Some("has-reduced-data-set"),
    call = consignmentRoutes.ApprovedOperatorController.onPageLoad(lrn, mode)
  )

  def consignorEoriYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignor.EoriYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignor.eoriYesNo",
    id = Some("has-consignor-eori"),
    call = consignorRoutes.EoriYesNoController.onPageLoad(lrn, mode)
  )

  def consignorEori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.EoriPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.consignment.consignor.eori",
    id = Some("consignor-eori-number"),
    call = consignorRoutes.EoriController.onPageLoad(lrn, mode)
  )

  def consignorName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.NamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.consignment.consignor.name",
    id = Some("consignor-name"),
    call = consignorRoutes.NameController.onPageLoad(lrn, mode)
  )

  def consignorAddress: Option[SummaryListRow] = getAnswerAndBuildRow[Address](
    page = consignor.AddressPage,
    formatAnswer = formatAsAddress,
    prefix = "traderDetails.consignment.consignor.address",
    id = Some("consignor-address"),
    call = consignorRoutes.AddressController.onPageLoad(lrn, mode)
  )

  def addConsignorContact: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignor.AddContactPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignor.addContact",
    id = Some("has-consignor-contact"),
    call = consignorRoutes.AddContactController.onPageLoad(lrn, mode)
  )

  def consignorContactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.contact.NamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.consignment.consignor.contact.name",
    id = Some("consignor-contact-name"),
    call = contactRoutes.NameController.onPageLoad(lrn, mode)
  )

  def consignorContactTelephoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.contact.TelephoneNumberPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.consignment.consignor.contact.telephoneNumber",
    id = Some("consignor-contact-phone-number"),
    call = contactRoutes.TelephoneNumberController.onPageLoad(lrn, mode)
  )

  def moreThanOneConsignee: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = MoreThanOneConsigneePage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.moreThanOneConsignee",
    id = Some("has-more-than-one-consignee"),
    call = consignmentRoutes.MoreThanOneConsigneeController.onPageLoad(lrn, mode)
  )

  def consigneeEoriYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignee.EoriYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignee.eoriYesNo",
    id = Some("has-consignee-eori"),
    call = consigneeRoutes.EoriYesNoController.onPageLoad(lrn, mode)
  )

  def consigneeEori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignee.EoriNumberPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.consignment.consignee.eoriNumber",
    id = Some("consignee-eori-number"),
    call = consigneeRoutes.EoriNumberController.onPageLoad(lrn, mode)
  )

  def consigneeName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignee.NamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.consignment.consignee.name",
    id = Some("consignee-name"),
    call = consigneeRoutes.NameController.onPageLoad(lrn, mode)
  )

  def consigneeAddress: Option[SummaryListRow] = getAnswerAndBuildRow[Address](
    page = consignee.AddressPage,
    formatAnswer = formatAsAddress,
    prefix = "traderDetails.consignment.consignee.address",
    id = Some("consignee-address"),
    call = consigneeRoutes.AddressController.onPageLoad(lrn, mode)
  )
}
