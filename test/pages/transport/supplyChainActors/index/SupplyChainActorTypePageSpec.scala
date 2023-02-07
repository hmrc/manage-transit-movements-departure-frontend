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

package pages.transport.supplyChainActors.index

import models.transport.supplyChainActors.SupplyChainActorType
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours

class SupplyChainActorTypePageSpec extends PageBehaviours {

  "SupplyChainActorTypePage" - {

    beRetrievable[SupplyChainActorType](SupplyChainActorTypePage(actorIndex))

    beSettable[SupplyChainActorType](SupplyChainActorTypePage(actorIndex))

    beRemovable[SupplyChainActorType](SupplyChainActorTypePage(actorIndex))

    "cleanup" - {
      val identificationNumber = Gen.alphaNumStr.sample.value

      "when value changes" - {
        "must clean up identification number page" in {
          forAll(arbitrary[SupplyChainActorType]) {
            value =>
              forAll(arbitrary[SupplyChainActorType].retryUntil(_ != value)) {
                differentValue =>
                  val userAnswers = emptyUserAnswers
                    .setValue(SupplyChainActorTypePage(actorIndex), value)
                    .setValue(IdentificationNumberPage(actorIndex), identificationNumber)

                  val result = userAnswers.setValue(SupplyChainActorTypePage(actorIndex), differentValue)

                  result.get(IdentificationNumberPage(actorIndex)) mustNot be(defined)
              }
          }
        }
      }

      "when value has not changed" - {
        "must not clean up identification number page" in {
          forAll(arbitrary[SupplyChainActorType]) {
            value =>
              val userAnswers = emptyUserAnswers
                .setValue(SupplyChainActorTypePage(actorIndex), value)
                .setValue(IdentificationNumberPage(actorIndex), identificationNumber)

              val result = userAnswers.setValue(SupplyChainActorTypePage(actorIndex), value)

              result.get(IdentificationNumberPage(actorIndex)) must be(defined)
          }
        }
      }
    }
  }
}
