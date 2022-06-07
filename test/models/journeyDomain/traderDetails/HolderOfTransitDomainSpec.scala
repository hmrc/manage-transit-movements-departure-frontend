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
import models.DeclarationType.Option4
import models.domain.{EitherType, UserAnswersReader}
import models.reference.{Country, CountryCode}
import models.{Address, DeclarationType, EoriNumber}
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import pages.QuestionPage
import pages.preTaskList.DeclarationTypePage
import pages.traderDetails.holderOfTransit._

class HolderOfTransitDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "HolderOfTransitDomain" - {

    "can be parsed from UserAnswers" - {

      "when Declaration Type is TIR (Option 4)" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(DeclarationTypePage)(Option4)
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(NamePage)("name")
          .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

        result.value mustBe an[HolderOfTransitTIR]
      }

      "when Declaration Type is anything else" in {

        DeclarationType.values.filterNot(_ == Option4).foreach {
          declarationType =>
            val userAnswers = emptyUserAnswers
              .unsafeSetVal(DeclarationTypePage)(declarationType)
              .unsafeSetVal(EoriYesNoPage)(false)
              .unsafeSetVal(NamePage)("name")
              .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
              .unsafeSetVal(AddContactPage)(false)

            val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

            result.value mustBe an[HolderOfTransitEori]
        }
      }
    }

    "cannot be parsed from UserAnswer" - {

      "when Declaration type is missing" in {

        val userAnswers = emptyUserAnswers

        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)

        result.left.value.page mustBe DeclarationTypePage
      }
    }

  }

  "HolderOfTransitEori" - {

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are answered" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(false)
          .unsafeSetVal(NamePage)("name")
          .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitEori] = UserAnswersReader[HolderOfTransitEori].run(userAnswers)

        val expectedResult = HolderOfTransitEori(None, "name", Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")), None)

        result.value mustBe expectedResult

      }

      "when all optional pages are answered" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(true)
          .unsafeSetVal(EoriPage)("GB12345678901234")
          .unsafeSetVal(NamePage)("name")
          .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(ContactNamePage)("contactName")
          .unsafeSetVal(ContactTelephoneNumberPage)("123123")

        val result: EitherType[HolderOfTransitEori] = UserAnswersReader[HolderOfTransitEori].run(userAnswers)

        val expectedResult = HolderOfTransitEori(
          Some(EoriNumber("GB12345678901234")),
          "name",
          Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")),
          Some(AdditionalContactDomain("contactName", "123123"))
        )

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          EoriYesNoPage,
          NamePage,
          AddressPage,
          AddContactPage
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(false)
          .unsafeSetVal(NamePage)("name")
          .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .unsafeSetVal(AddContactPage)(false)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result: EitherType[HolderOfTransitEori] = UserAnswersReader[HolderOfTransitEori].run(invalidUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }

      "when eori number is missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(true)
          .unsafeSetVal(NamePage)("name")
          .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitEori] = UserAnswersReader[HolderOfTransitEori].run(userAnswers)

        result.left.value.page mustBe EoriPage
      }

      "when contact details are missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(false)
          .unsafeSetVal(NamePage)("name")
          .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .unsafeSetVal(AddContactPage)(true)

        val result: EitherType[HolderOfTransitEori] = UserAnswersReader[HolderOfTransitEori].run(userAnswers)

        result.left.value.page mustBe ContactNamePage
      }
    }
  }

  "HolderOfTransitTIR" - {

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are answered" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(NamePage)("name")
          .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitTIR] = UserAnswersReader[HolderOfTransitTIR].run(userAnswers)

        val expectedResult = HolderOfTransitTIR(None, "name", Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")), None)

        result.value mustBe expectedResult

      }

      "when all optional pages are answered" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(true)
          .unsafeSetVal(TirIdentificationPage)("GB12345678901234")
          .unsafeSetVal(NamePage)("name")
          .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .unsafeSetVal(AddContactPage)(true)
          .unsafeSetVal(ContactNamePage)("contactName")
          .unsafeSetVal(ContactTelephoneNumberPage)("123123")

        val result: EitherType[HolderOfTransitTIR] = UserAnswersReader[HolderOfTransitTIR].run(userAnswers)

        val expectedResult = HolderOfTransitTIR(
          Some("GB12345678901234"),
          "name",
          Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")),
          Some(AdditionalContactDomain("contactName", "123123"))
        )

        result.value mustBe expectedResult

      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Gen[QuestionPage[_]] = Gen.oneOf(
          TirIdentificationYesNoPage,
          NamePage,
          AddressPage,
          AddContactPage
        )

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(NamePage)("name")
          .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .unsafeSetVal(AddContactPage)(false)

        forAll(mandatoryPages) {
          mandatoryPage =>
            val invalidUserAnswers = userAnswers.unsafeRemove(mandatoryPage)

            val result: EitherType[HolderOfTransitTIR] = UserAnswersReader[HolderOfTransitTIR].run(invalidUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }

      "when tir identification number is missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(true)
          .unsafeSetVal(NamePage)("name")
          .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .unsafeSetVal(AddContactPage)(false)

        val result: EitherType[HolderOfTransitTIR] = UserAnswersReader[HolderOfTransitTIR].run(userAnswers)

        result.left.value.page mustBe TirIdentificationPage
      }

      "when contact details are missing when not optional" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TirIdentificationYesNoPage)(false)
          .unsafeSetVal(NamePage)("name")
          .unsafeSetVal(AddressPage)(Address("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .unsafeSetVal(AddContactPage)(true)

        val result: EitherType[HolderOfTransitTIR] = UserAnswersReader[HolderOfTransitTIR].run(userAnswers)

        result.left.value.page mustBe ContactNamePage
      }

    }
  }

}
