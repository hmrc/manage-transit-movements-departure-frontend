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

package models.journeyDomain.transport

import base.SpecBase
import generators.Generators
import models.Index
import models.domain.{EitherType, UserAnswersReader}
import models.transport.supplyChainActors.SupplyChainActorType
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.transport.supplyChainActors.index.{IdentificationNumberPage, SupplyChainActorTypePage}

class SupplyChainActorDomainSpec extends SpecBase with Generators {

  "SupplyChainActorDomain" - {

    val role                 = arbitrary[SupplyChainActorType].sample.value
    val identificationNumber = Gen.alphaNumStr.sample.value

    "can be parsed from UserAnswers" - {

      "when at least one supply chain actor" in {
        val userAnswers = emptyUserAnswers
          .setValue(SupplyChainActorTypePage(actorIndex), role)
          .setValue(IdentificationNumberPage(actorIndex), identificationNumber)

        val expectedResult = SupplyChainActorDomain(
          role = role,
          identification = identificationNumber
        )(index)

        val result: EitherType[SupplyChainActorDomain] = UserAnswersReader[SupplyChainActorDomain](SupplyChainActorDomain.userAnswersReader(index))
          .run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from user answers" - {
      "when no supply chain actors" in {
        val userAnswers = emptyUserAnswers

        val result: EitherType[SupplyChainActorDomain] = UserAnswersReader[SupplyChainActorDomain](SupplyChainActorDomain.userAnswersReader(index))
          .run(userAnswers)

        result.left.value.page mustBe SupplyChainActorTypePage(Index(0))
      }
    }
  }
}
