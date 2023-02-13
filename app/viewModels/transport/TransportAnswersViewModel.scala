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

package viewModels.transport

import controllers.transport.authorisationsAndLimit.authorisations.{routes => authorisationsRoutes}
import controllers.transport.equipment.{routes => equipmentsRoutes}
import controllers.transport.supplyChainActors.{routes => supplyChainActorsRoutes}
import models.{CheckMode, RichOptionalJsArray, UserAnswers}
import pages.sections.transport.authorisationsAndLimit.AuthorisationsSection
import pages.sections.transport.equipment.EquipmentsSection
import pages.sections.transport.supplyChainActors.SupplyChainActorsSection
import play.api.i18n.Messages
import utils.cyaHelpers.transport.TransportAnswersHelper
import viewModels.Link
import viewModels.sections.Section
import viewModels.transport.transportMeans.TransportMeansAnswersViewModel.TransportMeansAnswersViewModelProvider

import javax.inject.Inject

case class TransportAnswersViewModel(sections: Seq[Section])

object TransportAnswersViewModel {

  class TransportAnswersViewModelProvider @Inject() (
    transportMeansAnswersViewModelProvider: TransportMeansAnswersViewModelProvider
  ) {

    // scalastyle:off method.length
    def apply(userAnswers: UserAnswers)(implicit messages: Messages): TransportAnswersViewModel = {
      val mode = CheckMode

      // TODO - pre-requisites section

      val transportMeansSections = transportMeansAnswersViewModelProvider.apply(userAnswers, mode).sections

      val helper = new TransportAnswersHelper(userAnswers, mode)

      val supplyChainActorsSection = Section(
        sectionTitle = messages("transport.checkYourAnswers.supplyChainActors"),
        rows = helper.addSupplyChainActor.toList ++ userAnswers
          .get(SupplyChainActorsSection)
          .mapWithIndex {
            (_, index) => helper.supplyChainActor(index)
          },
        addAnotherLink = Link(
          id = "add-or-remove-supply-chain-actors",
          text = messages("transport.checkYourAnswers.supplyChainActors.addOrRemove"),
          href = supplyChainActorsRoutes.AddAnotherSupplyChainActorController.onPageLoad(userAnswers.lrn, mode).url
        )
      )

      val authorisationsSection = Section(
        sectionTitle = messages("transport.checkYourAnswers.authorisations"),
        rows = helper.addAuthorisation.toList ++ userAnswers
          .get(AuthorisationsSection)
          .mapWithIndex {
            (_, index) => helper.authorisation(index)
          } ++ helper.limitDate.toList,
        addAnotherLink = Link(
          id = "add-or-remove-an-authorisation",
          text = messages("transport.checkYourAnswers.authorisations.addOrRemove"),
          href = authorisationsRoutes.AddAnotherAuthorisationController.onPageLoad(userAnswers.lrn, mode).url
        )
      )

      val carrierDetailsSection = Section(
        sectionTitle = messages("transport.checkYourAnswers.carrierDetails"),
        rows = Seq(
          helper.eoriNumber,
          helper.addContactPerson,
          helper.contactName,
          helper.contactTelephoneNumber
        ).flatten
      )

      val transportEquipmentSection = Section(
        sectionTitle = messages("transport.checkYourAnswers.transportEquipment"),
        rows = helper.addEquipment.toList ++ userAnswers
          .get(EquipmentsSection)
          .mapWithIndex {
            (_, index) => helper.equipment(index)
          },
        addAnotherLink = Link(
          id = "add-or-remove-transport-equipment",
          text = messages("transport.checkYourAnswers.transportEquipment.addOrRemove"),
          href = equipmentsRoutes.AddAnotherEquipmentController.onPageLoad(userAnswers.lrn, mode).url
        )
      )

      val transportChargesSection = Section(
        sectionTitle = messages("transport.checkYourAnswers.transportCharges"),
        rows = Seq(
          helper.addPaymentMethod,
          helper.paymentMethod
        ).flatten
      )

      val sections = transportMeansSections ++
        supplyChainActorsSection.toSeq ++
        authorisationsSection.toSeq ++
        carrierDetailsSection.toSeq ++
        transportEquipmentSection.toSeq ++
        transportChargesSection.toSeq

      new TransportAnswersViewModel(sections)
    }
    // scalastyle:on method.length
  }
}
