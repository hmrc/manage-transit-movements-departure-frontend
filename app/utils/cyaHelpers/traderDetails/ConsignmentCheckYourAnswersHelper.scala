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

import models.reference.Country
import models.{DynamicAddress, Mode, UserAnswers}
import pages.traderDetails.consignment._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class ConsignmentCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def approvedOperator: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = ApprovedOperatorPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.approvedOperator",
    id = Some("has-reduced-data-set")
  )

  def consignorEoriYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignor.EoriYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignor.eoriYesNo",
    id = Some("has-consignor-eori")
  )

  def consignorEori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.EoriPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignor.eori",
    id = Some("consignor-eori-number")
  )

  def consignorName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.NamePage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignor.name",
    id = Some("consignor-name")
  )

  def consignorCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = consignor.CountryPage,
    formatAnswer = formatAsCountry,
    prefix = "traderDetails.consignment.consignor.country",
    id = Some("consignor-country")
  )

  def consignorAddress: Option[SummaryListRow] = getAnswerAndBuildRow[DynamicAddress](
    page = consignor.AddressPage,
    formatAnswer = formatAsDynamicAddress,
    prefix = "traderDetails.consignment.consignor.address",
    id = Some("consignor-address")
  )

  def addConsignorContact: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignor.AddContactPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignor.addContact",
    id = Some("has-consignor-contact")
  )

  def consignorContactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.contact.NamePage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignor.contact.name",
    id = Some("consignor-contact-name")
  )

  def consignorContactTelephoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.contact.TelephoneNumberPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignor.contact.telephoneNumber",
    id = Some("consignor-contact-phone-number")
  )

  def moreThanOneConsignee: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = MoreThanOneConsigneePage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.moreThanOneConsignee",
    id = Some("has-more-than-one-consignee")
  )

  def consigneeEoriYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignee.EoriYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignee.eoriYesNo",
    id = Some("has-consignee-eori")
  )

  def consigneeEori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignee.EoriNumberPage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignee.eoriNumber",
    id = Some("consignee-eori-number")
  )

  def consigneeName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignee.NamePage,
    formatAnswer = formatAsText,
    prefix = "traderDetails.consignment.consignee.name",
    id = Some("consignee-name")
  )

  def consigneeCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = consignee.CountryPage,
    formatAnswer = formatAsCountry,
    prefix = "traderDetails.consignment.consignee.country",
    id = Some("consignee-country")
  )

  def consigneeAddress: Option[SummaryListRow] = getAnswerAndBuildRow[DynamicAddress](
    page = consignee.AddressPage,
    formatAnswer = formatAsDynamicAddress,
    prefix = "traderDetails.consignment.consignee.address",
    id = Some("consignee-address")
  )
}
