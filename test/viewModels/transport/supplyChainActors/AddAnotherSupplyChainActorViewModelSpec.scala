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

package viewModels.transport.supplyChainActors

import base.SpecBase
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.transport.supplyChainActors.AddAnotherSupplyChainActorViewModel.AddAnotherSupplyChainActorViewModelProvider

class AddAnotherSupplyChainActorViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one supply chain actor" in {
      forAll(arbitrary[Mode]) {
        mode =>
          val userAnswers = arbitrarySupplyChainActorAnswers(emptyUserAnswers, activeIndex).sample.value

          val result = new AddAnotherSupplyChainActorViewModelProvider()(userAnswers, mode)

          result.listItems.length mustBe 1
          result.title mustBe "You have added 1 supply chain actor"
          result.heading mustBe "You have added 1 supply chain actor"
          result.legend mustBe "Do you want to add another supply chain actor?"
          result.maxLimitLabel mustBe "You cannot add any more supply chain actors. To add another, you need to remove one first."
      }
    }

    "when there are multiple supply chain actors" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxSupplyChainActors)) {
        (mode, supplyChainActors) =>
          val userAnswers = (0 until supplyChainActors).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitrarySupplyChainActorAnswers(acc, Index(i)).sample.value
          }

          val result = new AddAnotherSupplyChainActorViewModelProvider()(userAnswers, mode)
          result.listItems.length mustBe supplyChainActors
          result.title mustBe s"You have added ${formatter.format(supplyChainActors)} supply chain actors"
          result.heading mustBe s"You have added ${formatter.format(supplyChainActors)} supply chain actors"
          result.legend mustBe "Do you want to add another supply chain actor?"
          result.maxLimitLabel mustBe "You cannot add any more supply chain actors. To add another, you need to remove one first."
      }
    }
  }
}
