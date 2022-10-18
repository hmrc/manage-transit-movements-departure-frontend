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

package models.journeyDomain.traderDetails.consignment

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.reference.Country
import models.{DynamicAddress, EoriNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.traderDetails.consignment.consignee

class ConsignmentConsigneeDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "ConsignmentConsigneeDomain" - {

    val consigneeEori    = arbitrary[EoriNumber].sample.value
    val consigneeName    = Gen.alphaNumStr.sample.value
    val consigneeCountry = arbitrary[Country].sample.value
    val consigneeAddress = arbitrary[DynamicAddress].sample.value

    "can be parsed from UserAnswers" - {

      "when has all consignment fields complete" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignee.EoriYesNoPage)(true)
          .unsafeSetVal(consignee.EoriNumberPage)(consigneeEori.value)
          .unsafeSetVal(consignee.NamePage)(consigneeName)
          .unsafeSetVal(consignee.CountryPage)(consigneeCountry)
          .unsafeSetVal(consignee.AddressPage)(consigneeAddress)

        val expectedResult = ConsignmentConsigneeDomain(
          eori = Some(consigneeEori),
          name = consigneeName,
          country = consigneeCountry,
          address = consigneeAddress
        )
        val result: EitherType[ConsignmentConsigneeDomain] = UserAnswersReader[ConsignmentConsigneeDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when has all consignment fields complete not no Eori" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignee.EoriYesNoPage)(false)
          .unsafeSetVal(consignee.NamePage)(consigneeName)
          .unsafeSetVal(consignee.CountryPage)(consigneeCountry)
          .unsafeSetVal(consignee.AddressPage)(consigneeAddress)

        val expectedResult = ConsignmentConsigneeDomain(
          eori = None,
          name = consigneeName,
          country = consigneeCountry,
          address = consigneeAddress
        )
        val result: EitherType[ConsignmentConsigneeDomain] = UserAnswersReader[ConsignmentConsigneeDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when EoriYesNoPage is missing" in {

        val userAnswers = emptyUserAnswers

        val result: EitherType[ConsignmentConsigneeDomain] = UserAnswersReader[ConsignmentConsigneeDomain].run(userAnswers)

        result.left.value.page mustBe consignee.EoriYesNoPage
      }

      "when EoriYesNoPage is true and EoriPage is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignee.EoriYesNoPage)(true)

        val result: EitherType[ConsignmentConsigneeDomain] = UserAnswersReader[ConsignmentConsigneeDomain].run(userAnswers)

        result.left.value.page mustBe consignee.EoriNumberPage
      }

      "when NamePage is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignee.EoriYesNoPage)(false)

        val result: EitherType[ConsignmentConsigneeDomain] = UserAnswersReader[ConsignmentConsigneeDomain].run(userAnswers)

        result.left.value.page mustBe consignee.NamePage
      }

      "when CountryPage is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignee.EoriYesNoPage)(false)
          .unsafeSetVal(consignee.NamePage)(consigneeName)

        val result: EitherType[ConsignmentConsigneeDomain] = UserAnswersReader[ConsignmentConsigneeDomain].run(userAnswers)

        result.left.value.page mustBe consignee.CountryPage
      }

      "when AddressPage is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignee.EoriYesNoPage)(false)
          .unsafeSetVal(consignee.NamePage)(consigneeName)
          .unsafeSetVal(consignee.CountryPage)(consigneeCountry)

        val result: EitherType[ConsignmentConsigneeDomain] = UserAnswersReader[ConsignmentConsigneeDomain].run(userAnswers)

        result.left.value.page mustBe consignee.AddressPage
      }
    }
  }
}
