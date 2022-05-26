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

package models.journeyDomain.traderDetails

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.{Address, EoriNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.traderDetails.holderOfTransit._

class HolderOfTransitDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "HolderOfTransit" - {

    "can be parsed from UserAnswers" - {

      "when holder has no eori with contact" in {
        val name        = Gen.alphaNumStr.sample.value
        val contactName = Gen.alphaNumStr.sample.value
        val address     = arbitrary[Address].sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(EoriYesNoPage)(false)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(ContactNamePage)(contactName)

        val expectedResult = HolderOfTransitDomain(
          tir = None,
          eori = None,
          name = name,
          contactName = Some(contactName),
          address = address
        )

        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when holder has an eori and no contact" in {

        val eori    = arbitrary[EoriNumber].sample.value
        val name    = Gen.alphaNumStr.sample.value
        val address = arbitrary[Address].sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(EoriYesNoPage)(true)
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(false)

        val expectedResult = HolderOfTransitDomain(
          tir = None,
          eori = Some(eori),
          name = name,
          contactName = None,
          address = address
        )

        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

        result.value mustBe expectedResult

      }
    }

    "cannot be parsed from UserAnswers" - {

      "when answered yes to EoriYesNo but no eori provided" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(EoriYesNoPage)(true)

        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

        result.left.value.page mustBe EoriPage
      }

      "when answered yes to AddContact but no contact name provided" in {

        val name    = Gen.alphaNumStr.sample.value
        val address = arbitrary[Address].sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(EoriYesNoPage)(false)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(AddressPage)(address)
          .unsafeSetVal(AddContactPage)(true)

        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

        result.left.value.page mustBe ContactNamePage
      }
    }
  }
}
