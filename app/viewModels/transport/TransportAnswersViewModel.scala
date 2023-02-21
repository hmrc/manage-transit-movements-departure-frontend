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

import models.{CheckMode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.transport.TransportAnswersHelper
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

      val helper = new TransportAnswersHelper(userAnswers, mode)

      val preRequisitesSection = Section(
        rows = Seq(
          helper.usingSameUcr,
          helper.ucr,
          helper.countryOfDispatch,
          helper.transportedToSameCountry,
          helper.countryOfDestination,
          helper.usingContainersYesNo
        ).flatten
      )

      val transportMeansSections = transportMeansAnswersViewModelProvider.apply(userAnswers, mode).sections

      val supplyChainActorsSection = Section(
        sectionTitle = messages("transport.checkYourAnswers.supplyChainActors"),
        rows = helper.addSupplyChainActor.toList ++ helper.supplyChainActors,
        addAnotherLink = helper.addOrRemoveSupplyChainActors
      )

      val authorisationsSection = Section(
        sectionTitle = messages("transport.checkYourAnswers.authorisations"),
        rows = helper.addAuthorisation.toList ++ helper.authorisations ++ helper.limitDate.toList,
        addAnotherLink = helper.addOrRemoveAuthorisations
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
        rows = helper.addEquipment.toList ++ helper.equipments,
        addAnotherLink = helper.addOrRemoveEquipments
      )

      val transportChargesSection = Section(
        sectionTitle = messages("transport.checkYourAnswers.transportCharges"),
        rows = Seq(
          helper.addPaymentMethod,
          helper.paymentMethod
        ).flatten
      )

      val sections = preRequisitesSection.toSeq ++
        transportMeansSections ++
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
