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

package models.JourneyDomain.traderDetails

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.traderDetails.ConsignmentConsigneeDomain
import models.reference.{Country, CountryCode}
import models.{Address, EoriNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.traderDetails.consignment.consignee
import pages.traderDetails.consignment.consignor

class ConsignmentConsigneeDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "ConsignmentConsigneeDomain" - {

    val consigneeEori    = arbitrary[EoriNumber].sample.value
    val consigneeName    = Gen.alphaNumStr.sample.value
    val consigneeAddress = Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description"))

    "can be parsed from UserAnswers" - {

      "when has all consignment fields complete" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignee.EoriYesNoPage)(true)
          .unsafeSetVal(consignee.EoriNumberPage)(consigneeEori.value)
          .unsafeSetVal(consignor.NamePage)(consigneeName)
          .unsafeSetVal(consignor.AddressPage)(consigneeAddress)

        val expectedResult = ConsignmentConsigneeDomain(
          eori = Some(consigneeEori),
          name = consigneeName,
          address = consigneeAddress
        )
        val result: EitherType[ConsignmentConsigneeDomain] = UserAnswersReader[ConsignmentConsigneeDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when has all consignment fields complete not no Eori" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignee.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(consigneeName)
          .unsafeSetVal(consignor.AddressPage)(consigneeAddress)

        val expectedResult = ConsignmentConsigneeDomain(
          eori = None,
          name = consigneeName,
          address = consigneeAddress
        )
        val result: EitherType[ConsignmentConsigneeDomain] = UserAnswersReader[ConsignmentConsigneeDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswer" - {

      "when Declaration type is missing" in {

        val userAnswers = emptyUserAnswers

        val result: EitherType[ConsignmentConsigneeDomain] = UserAnswersReader[ConsignmentConsigneeDomain].run(userAnswers)

        result.left.value.page mustBe consignee.EoriYesNoPage
      }
    }
  }
}
