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

package utils.cyaHelpers.transport

import models.journeyDomain.transport.equipment.EquipmentDomain
import models.transport.equipment.PaymentMethod
import models.{Index, Mode, UserAnswers}
import pages.transport.authorisationsAndLimit.limit.LimitDatePage
import pages.transport.carrierDetails.contact.{NamePage, TelephoneNumberPage}
import pages.transport.carrierDetails.{AddContactYesNoPage, IdentificationNumberPage}
import pages.transport.equipment.{AddPaymentMethodYesNoPage, AddTransportEquipmentYesNoPage, PaymentMethodPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import utils.cyaHelpers.AnswersHelper

import java.time.LocalDate

class TransportAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def limitDate: Option[SummaryListRow] = getAnswerAndBuildRow[LocalDate](
    page = LimitDatePage,
    formatAnswer = formatAsDate,
    prefix = "transport.authorisationsAndLimit.limit.limitDate",
    id = Some("change-limit-date")
  )

  def eoriNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = IdentificationNumberPage,
    formatAnswer = formatAsText,
    prefix = "transport.carrierDetails.identificationNumber",
    id = Some("change-eori-number")
  )

  def addContactPerson: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddContactYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.carrierDetails.addContactYesNo",
    id = Some("change-add-contact")
  )

  def contactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = NamePage,
    formatAnswer = formatAsText,
    prefix = "transport.carrierDetails.contact.name",
    id = Some("change-contact-name")
  )

  def contactTelephoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = TelephoneNumberPage,
    formatAnswer = formatAsText,
    prefix = "transport.carrierDetails.contact.telephoneNumber",
    id = Some("change-contact-telephone-number")
  )

  def addEquipment: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddTransportEquipmentYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.equipment.addTransportEquipmentYesNo",
    id = Some("change-add-equipment")
  )

  def equipment(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[EquipmentDomain](
    formatAnswer = _.asString.toText,
    prefix = "transport.checkYourAnswers.equipment",
    id = Some(s"change-transport-equipment-${index.display}"),
    args = index.display
  )(EquipmentDomain.userAnswersReader(index))

  def addPaymentMethod: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddPaymentMethodYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.equipment.addPaymentMethodYesNo",
    id = Some("change-add-payment-method")
  )

  def paymentMethod: Option[SummaryListRow] = getAnswerAndBuildRow[PaymentMethod](
    page = PaymentMethodPage,
    formatAnswer = formatEnumAsText(PaymentMethod.messageKeyPrefix),
    prefix = "transport.equipment.paymentMethod",
    id = Some("change-payment-method")
  )

}
