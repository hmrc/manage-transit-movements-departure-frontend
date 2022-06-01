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
import models.{Address, DeclarationType}
import models.DeclarationType.Option4
import models.domain.{EitherType, UserAnswersReader}
import models.reference.{Country, CountryCode}
import pages.preTaskList.DeclarationTypePage
import pages.traderDetails.holderOfTransit._

class HolderOfTransitDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

//  "HolderOfTransit" - {
//
//    "can be parsed from UserAnswers" - {
//
//      "when holder has no eori" in {
//        val declarationType = Gen.oneOf(DeclarationType.values.filterNot(_ == Option4)).sample.value
//        val name            = Gen.alphaNumStr.sample.value
//        val address         = arbitrary[Address].sample.value
//
//        val userAnswers = emptyUserAnswers
//          .unsafeSetVal(DeclarationTypePage)(declarationType)
//          .unsafeSetVal(EoriYesNoPage)(false)
//          .unsafeSetVal(NamePage)(name)
//          .unsafeSetVal(AddressPage)(address)
//          .unsafeSetVal(AddContactPage)(false)
//
//        val expectedResult = HolderOfTransitDomain(
//          tir = None,
//          eori = None,
//          name = name,
//          additionalContact = None,
//          address = address
//        )
//
//        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)
//
//        result.value mustBe expectedResult
//      }
//
//      "when holder has an eori" in {
//        val declarationType = Gen.oneOf(DeclarationType.values.filterNot(_ == Option4)).sample.value
//        val name            = Gen.alphaNumStr.sample.value
//        val eori            = arbitrary[EoriNumber].sample.value
//        val address         = arbitrary[Address].sample.value
//
//        val userAnswers = emptyUserAnswers
//          .unsafeSetVal(DeclarationTypePage)(declarationType)
//          .unsafeSetVal(EoriYesNoPage)(true)
//          .unsafeSetVal(EoriPage)(eori.value)
//          .unsafeSetVal(NamePage)(name)
//          .unsafeSetVal(AddressPage)(address)
//          .unsafeSetVal(AddContactPage)(false)
//
//        val expectedResult = HolderOfTransitDomain(
//          tir = None,
//          eori = Some(eori),
//          name = name,
//          additionalContact = None,
//          address = address
//        )
//
//        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)
//
//        result.value mustBe expectedResult
//      }
//
//      "when holder has no TIR id" in {
//        val name    = Gen.alphaNumStr.sample.value
//        val address = arbitrary[Address].sample.value
//
//        val userAnswers = emptyUserAnswers
//          .unsafeSetVal(DeclarationTypePage)(Option4)
//          .unsafeSetVal(TirIdentificationYesNoPage)(false)
//          .unsafeSetVal(NamePage)(name)
//          .unsafeSetVal(AddressPage)(address)
//          .unsafeSetVal(AddContactPage)(false)
//
//        val expectedResult = HolderOfTransitDomain(
//          tir = None,
//          eori = None,
//          name = name,
//          additionalContact = None,
//          address = address
//        )
//
//        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)
//
//        result.value mustBe expectedResult
//      }
//
//      "when holder has TIR id" in {
//        val name    = Gen.alphaNumStr.sample.value
//        val tirId   = Gen.alphaNumStr.sample.value
//        val address = arbitrary[Address].sample.value
//
//        val userAnswers = emptyUserAnswers
//          .unsafeSetVal(DeclarationTypePage)(Option4)
//          .unsafeSetVal(TirIdentificationYesNoPage)(true)
//          .unsafeSetVal(TirIdentificationPage)(tirId)
//          .unsafeSetVal(NamePage)(name)
//          .unsafeSetVal(AddressPage)(address)
//          .unsafeSetVal(AddContactPage)(false)
//
//        val expectedResult = HolderOfTransitDomain(
//          tir = Some(tirId),
//          eori = None,
//          name = name,
//          additionalContact = None,
//          address = address
//        )
//
//        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)
//
//        result.value mustBe expectedResult
//      }
//
//      "when holder has a contact" in {
//        val name                   = Gen.alphaNumStr.sample.value
//        val address                = arbitrary[Address].sample.value
//        val contactName            = Gen.alphaNumStr.sample.value
//        val contactTelephoneNumber = Gen.alphaNumStr.sample.value
//
//        val userAnswers = emptyUserAnswers
//          .unsafeSetVal(DeclarationTypePage)(Option4)
//          .unsafeSetVal(TirIdentificationYesNoPage)(false)
//          .unsafeSetVal(NamePage)(name)
//          .unsafeSetVal(AddressPage)(address)
//          .unsafeSetVal(AddContactPage)(true)
//          .unsafeSetVal(ContactNamePage)(contactName)
//          .unsafeSetVal(ContactTelephoneNumberPage)(contactTelephoneNumber)
//
//        val expectedResult = HolderOfTransitDomain(
//          tir = None,
//          eori = None,
//          name = name,
//          additionalContact = Some(AdditionalContactDomain(contactName, contactTelephoneNumber)),
//          address = address
//        )
//
//        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)
//
//        result.value mustBe expectedResult
//
//      }
//    }
//
//    "cannot be parsed from UserAnswers" - {
//
//      "when answered yes to EoriYesNo but no eori provided" in {
//        val declarationType = Gen.oneOf(DeclarationType.values.filterNot(_ == Option4)).sample.value
//
//        val userAnswers = emptyUserAnswers
//          .unsafeSetVal(DeclarationTypePage)(declarationType)
//          .unsafeSetVal(EoriYesNoPage)(true)
//
//        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)
//
//        result.left.value.page mustBe EoriPage
//      }
//
//      "when answered yes to TirIdentificationYesNo but no TIR identification provided" in {
//        val userAnswers = emptyUserAnswers
//          .unsafeSetVal(DeclarationTypePage)(Option4)
//          .unsafeSetVal(TirIdentificationYesNoPage)(true)
//
//        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)
//
//        result.left.value.page mustBe TirIdentificationPage
//      }
//
//      "when answered yes to AddContact but no contact name provided" in {
//        val name    = Gen.alphaNumStr.sample.value
//        val address = arbitrary[Address].sample.value
//
//        val userAnswers = emptyUserAnswers
//          .unsafeSetVal(DeclarationTypePage)(Option4)
//          .unsafeSetVal(TirIdentificationYesNoPage)(false)
//          .unsafeSetVal(NamePage)(name)
//          .unsafeSetVal(AddressPage)(address)
//          .unsafeSetVal(AddContactPage)(true)
//
//        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)
//
//        result.left.value.page mustBe ContactNamePage
//      }
//
//      "when Option4 declaration type and TirIdentificationYesNo not answered" in {
//        val userAnswers = emptyUserAnswers
//          .unsafeSetVal(DeclarationTypePage)(Option4)
//
//        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)
//
//        result.left.value.page mustBe TirIdentificationYesNoPage
//      }
//
//      "when non-Option4 declaration type and EoriYesNo not answered" in {
//        val declarationType = Gen.oneOf(DeclarationType.values.filterNot(_ == Option4)).sample.value
//
//        val userAnswers = emptyUserAnswers
//          .unsafeSetVal(DeclarationTypePage)(declarationType)
//
//        val result: EitherType[HolderOfTransitDomain] = UserAnswersReader[HolderOfTransitDomain].run(userAnswers)
//
//        result.left.value.page mustBe EoriYesNoPage
//      }
//    }
//  }

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

            result.value mustBe an[HolderOfTransitTIR]
        }
      }
    }

    "cannot be parsed from UserAnswer" - {

      "when Declaration type is missing" in {}
    }

  }

  "HolderOfTransitEori" - {

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are answered" in {}

      "when all optional pages are answered" in {}

    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing" in {}

    }
  }

  "HolderOfTransitTIR" - {

    "can be parsed from UserAnswers" - {

      "when all mandatory pages are answered" in {}

      "when all optional pages are answered" in {}

    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing" in {}

    }
  }

}
