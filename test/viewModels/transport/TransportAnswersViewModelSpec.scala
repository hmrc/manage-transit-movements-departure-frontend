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

import base.SpecBase
import controllers.transport.authorisationsAndLimit.authorisations.{routes => authorisationsRoutes}
import controllers.transport.equipment.{routes => equipmentsRoutes}
import controllers.transport.supplyChainActors.{routes => supplyChainActorsRoutes}
import generators.Generators
import models.CheckMode
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.Link
import viewModels.transport.TransportAnswersViewModel.TransportAnswersViewModelProvider
import viewModels.transport.transportMeans.TransportMeansAnswersViewModel
import viewModels.transport.transportMeans.TransportMeansAnswersViewModel.TransportMeansAnswersViewModelProvider

class TransportAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val mockTransportMeansAnswersViewModelProvider = mock[TransportMeansAnswersViewModelProvider]

  "apply" - {
    "must return all sections" in {
      val mode = CheckMode
      forAll(arbitraryTransportAnswers(emptyUserAnswers)) {
        answers =>
          when(mockTransportMeansAnswersViewModelProvider.apply(any(), any())(any()))
            .thenReturn(TransportMeansAnswersViewModel(Nil))

          val viewModelProvider = new TransportAnswersViewModelProvider(mockTransportMeansAnswersViewModelProvider)
          val sections          = viewModelProvider.apply(answers).sections

          sections.size mustBe 5

          sections.head.sectionTitle.get mustBe "Supply chain actors"
          sections.head.addAnotherLink.get mustBe Link(
            "add-or-remove-supply-chain-actors",
            "Add or remove supply chain actors",
            supplyChainActorsRoutes.AddAnotherSupplyChainActorController.onPageLoad(answers.lrn, mode).url
          )

          sections(1).sectionTitle.get mustBe "Authorisation"
          sections(1).addAnotherLink.get mustBe Link(
            "add-or-remove-an-authorisation",
            "Add or remove an authorisation",
            authorisationsRoutes.AddAnotherAuthorisationController.onPageLoad(answers.lrn, mode).url
          )

          sections(2).sectionTitle.get mustBe "Carrier details"
          sections(2).addAnotherLink must not be defined

          sections(3).sectionTitle.get mustBe "Transport equipment"
          sections(3).addAnotherLink.get mustBe Link(
            "add-or-remove-transport-equipment",
            "Add or remove transport equipment",
            equipmentsRoutes.AddAnotherEquipmentController.onPageLoad(answers.lrn, mode).url
          )

          sections(4).sectionTitle.get mustBe "Transport charges"
          sections(4).addAnotherLink must not be defined

          verify(mockTransportMeansAnswersViewModelProvider).apply(eqTo(answers), eqTo(mode))(any())
      }
    }
  }
}
