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

import controllers.transport.authorisationsAndLimit.authorisations.{routes => authorisationsRoutes}
import controllers.transport.equipment.{routes => equipmentsRoutes}
import controllers.transport.supplyChainActors.{routes => supplyChainActorsRoutes}
import models.journeyDomain.transport.authorisationsAndLimit.authorisations.AuthorisationDomain
import models.journeyDomain.transport.equipment.EquipmentDomain
import models.journeyDomain.transport.supplyChainActors.SupplyChainActorDomain
import models.reference.Country
import models.transport.equipment.PaymentMethod
import models.{Index, Mode, UserAnswers}
import pages.sections.transport.authorisationsAndLimit.AuthorisationsSection
import pages.sections.transport.equipment.EquipmentsSection
import pages.sections.transport.supplyChainActors.SupplyChainActorsSection
import pages.transport.authorisationsAndLimit.authorisations.AddAuthorisationsYesNoPage
import pages.transport.authorisationsAndLimit.limit.LimitDatePage
import pages.transport.carrierDetails.contact.{NamePage, TelephoneNumberPage}
import pages.transport.carrierDetails.{AddContactYesNoPage, IdentificationNumberPage}
import pages.transport.equipment.{AddPaymentMethodYesNoPage, AddTransportEquipmentYesNoPage, PaymentMethodPage}
import pages.transport.preRequisites._
import pages.transport.supplyChainActors.SupplyChainActorYesNoPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import utils.cyaHelpers.AnswersHelper
import viewModels.Link

import java.time.LocalDate

class TransportAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def usingSameUcr: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = SameUcrYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.preRequisites.sameUcrYesNo",
    id = Some("change-using-same-ucr")
  )

  def ucr: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = UniqueConsignmentReferencePage,
    formatAnswer = formatAsText,
    prefix = "transport.preRequisites.uniqueConsignmentReference",
    id = Some("change-ucr")
  )

  def countryOfDispatch: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryOfDispatchPage,
    formatAnswer = formatAsText,
    prefix = "transport.preRequisites.countryOfDispatch",
    id = Some("change-country-of-dispatch")
  )

  def transportedToSameCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = TransportedToSameCountryYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.preRequisites.transportedToSameCountryYesNo",
    id = Some("change-transported-to-same-country")
  )

  def countryOfDestination: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = ItemsDestinationCountryPage,
    formatAnswer = formatAsText,
    prefix = "transport.preRequisites.itemsDestinationCountry",
    id = Some("change-country-of-destination")
  )

  def usingContainersYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = ContainerIndicatorPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.preRequisites.containerIndicator",
    id = Some("change-using-containers")
  )

  def addAuthorisation: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddAuthorisationsYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.authorisations.addAuthorisationsYesNo",
    id = Some("change-add-authorisation")
  )

  def authorisations: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(AuthorisationsSection)(authorisation)

  def authorisation(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[AuthorisationDomain](
    formatAnswer = _.asString.toText,
    prefix = "transport.checkYourAnswers.authorisation",
    id = Some(s"change-authorisation-${index.display}"),
    args = index.display
  )(AuthorisationDomain.userAnswersReader(index))

  def addOrRemoveAuthorisations: Option[Link] = buildLink(AuthorisationsSection) {
    Link(
      id = "add-or-remove-an-authorisation",
      text = messages("transport.checkYourAnswers.authorisations.addOrRemove"),
      href = authorisationsRoutes.AddAnotherAuthorisationController.onPageLoad(userAnswers.lrn, mode).url
    )
  }

  def addSupplyChainActor: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = SupplyChainActorYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "transport.supplyChainActors.supplyChainActorYesNo",
    id = Some("change-add-supply-chain-actor")
  )

  def supplyChainActors: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(SupplyChainActorsSection)(supplyChainActor)

  def supplyChainActor(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[SupplyChainActorDomain](
    formatAnswer = _.asString.toText,
    prefix = "transport.checkYourAnswers.supplyChainActor",
    id = Some(s"change-supply-chain-actor-${index.display}"),
    args = index.display
  )(SupplyChainActorDomain.userAnswersReader(index))

  def addOrRemoveSupplyChainActors: Option[Link] = buildLink(SupplyChainActorsSection) {
    Link(
      id = "add-or-remove-supply-chain-actors",
      text = messages("transport.checkYourAnswers.supplyChainActors.addOrRemove"),
      href = supplyChainActorsRoutes.AddAnotherSupplyChainActorController.onPageLoad(userAnswers.lrn, mode).url
    )
  }

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

  def equipments: Seq[SummaryListRow] =
    getAnswersAndBuildSectionRows(EquipmentsSection)(equipment)

  def equipment(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[EquipmentDomain](
    formatAnswer = _.asString.toText,
    prefix = "transport.checkYourAnswers.equipment",
    id = Some(s"change-transport-equipment-${index.display}"),
    args = index.display
  )(EquipmentDomain.userAnswersReader(index))

  def addOrRemoveEquipments: Option[Link] = buildLink(EquipmentsSection) {
    Link(
      id = "add-or-remove-transport-equipment",
      text = messages("transport.checkYourAnswers.transportEquipment.addOrRemove"),
      href = equipmentsRoutes.AddAnotherEquipmentController.onPageLoad(userAnswers.lrn, mode).url
    )
  }

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
