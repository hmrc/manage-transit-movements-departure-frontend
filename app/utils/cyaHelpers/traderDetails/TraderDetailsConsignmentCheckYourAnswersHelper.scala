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
import models.{Address, Mode, UserAnswers}
import pages.traderDetails.consignment._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class TraderDetailsConsignmentCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers) {

  def consignorEoriYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignor.EoriYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignor.eoriYesNo",
    id = None,
    call = consignorRoutes.EoriYesNoController.onPageLoad(lrn, mode)
  )

  def consignorEori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.EoriPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.consignment.consignor.eori",
    id = None,
    call = consignorRoutes.EoriController.onPageLoad(lrn, mode)
  )

  def consignorName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.NamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.consignment.consignor.name",
    id = None,
    call = consignorRoutes.NameController.onPageLoad(lrn, mode)
  )

  def consignorAddress: Option[SummaryListRow] = getAnswerAndBuildRow[Address](
    page = consignor.AddressPage,
    formatAnswer = formatAsAddress,
    prefix = "traderDetails.consignment.consignor.address",
    id = None,
    call = consignorRoutes.AddressController.onPageLoad(lrn, mode)
  )

  def addConsignorContact: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignor.AddContactPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignor.addContact",
    id = None,
    call = consignorRoutes.AddContactController.onPageLoad(lrn, mode)
  )

  def consignorContactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.contact.NamePage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.consignment.consignor.contact.name",
    id = None,
    call = contactRoutes.NameController.onPageLoad(lrn, mode)
  )

  def consignorContactTelephoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = consignor.contact.TelephoneNumberPage,
    formatAnswer = formatAsLiteral,
    prefix = "traderDetails.consignment.consignor.contact.telephoneNumber",
    id = None,
    call = contactRoutes.TelephoneNumberController.onPageLoad(lrn, mode)
  )

  def moreThanOneConsignee: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignee.MoreThanOneConsigneePage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignee.moreThanOneConsignee",
    id = None,
    call = consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(lrn, mode)
  )

  def consigneeEoriYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = consignee.EoriYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "traderDetails.consignment.consignee.eoriYesNo",
    id = None,
    call = consigneeRoutes.EoriYesNoController.onPageLoad(lrn, mode)
  )
}
