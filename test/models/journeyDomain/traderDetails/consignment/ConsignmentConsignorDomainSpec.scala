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
import models.{Address, EoriNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.traderDetails.consignment._

class ConsignmentConsignorDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "ConsignmentConsignorDomain" - {

    val eori         = arbitrary[EoriNumber].sample.value
    val name         = Gen.alphaNumStr.sample.value
    val address      = arbitrary[Address].sample.value
    val contactName  = Gen.alphaNumStr.sample.value
    val contactPhone = Gen.alphaNumStr.sample.value

    "can be parsed from UserAnswers" - {

      "when has all consignment fields complete" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriYesNoPage)(true)
          .unsafeSetVal(consignor.EoriPage)(eori.value)
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(true)
          .unsafeSetVal(consignor.contact.NamePage)(contactName)
          .unsafeSetVal(consignor.contact.TelephoneNumberPage)(contactPhone)

        val expectedResult = ConsignmentConsignorDomain(
          eori = Some(eori),
          name = name,
          address = address,
          contact = Some(
            ConsignmentConsignorContactDomain(
              name = contactName,
              telephoneNumber = contactPhone
            )
          )
        )

        val result: EitherType[ConsignmentConsignorDomain] = UserAnswersReader[ConsignmentConsignorDomain].run(userAnswers)
        result.value mustBe expectedResult
      }

      "when has all consignment fields complete but eoriYesNo is false" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(true)
          .unsafeSetVal(consignor.contact.NamePage)(contactName)
          .unsafeSetVal(consignor.contact.TelephoneNumberPage)(contactPhone)

        val expectedResult = ConsignmentConsignorDomain(
          eori = None,
          name = name,
          address = address,
          contact = Some(
            ConsignmentConsignorContactDomain(
              name = contactName,
              telephoneNumber = contactPhone
            )
          )
        )

        val result: EitherType[ConsignmentConsignorDomain] = UserAnswersReader[ConsignmentConsignorDomain].run(userAnswers)
        result.value mustBe expectedResult
      }

      "when has all consignment fields complete but addContact is false" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriYesNoPage)(true)
          .unsafeSetVal(consignor.EoriPage)(eori.value)
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(false)

        val expectedResult = ConsignmentConsignorDomain(
          eori = Some(eori),
          name = name,
          address = address,
          contact = None
        )

        val result: EitherType[ConsignmentConsignorDomain] = UserAnswersReader[ConsignmentConsignorDomain].run(userAnswers)
        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when EoriYesNo is missing" in {

        val userAnswers = emptyUserAnswers

        val result: EitherType[ConsignmentConsignorDomain] = UserAnswersReader[ConsignmentConsignorDomain].run(userAnswers)

        result.left.value.page mustBe consignor.EoriYesNoPage
      }

      "when EoriYesNoPage is true and EoriPage is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriYesNoPage)(true)

        val result: EitherType[ConsignmentConsignorDomain] = UserAnswersReader[ConsignmentConsignorDomain].run(userAnswers)
        result.left.value.page mustBe consignor.EoriPage
      }

      "when NamePage is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriYesNoPage)(false)

        val result: EitherType[ConsignmentConsignorDomain] = UserAnswersReader[ConsignmentConsignorDomain].run(userAnswers)
        result.left.value.page mustBe consignor.NamePage
      }

      "when AddressPage is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(name)

        val result: EitherType[ConsignmentConsignorDomain] = UserAnswersReader[ConsignmentConsignorDomain].run(userAnswers)
        result.left.value.page mustBe consignor.AddressPage
      }

      "when AddContactPage is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.AddressPage)(address)

        val result: EitherType[ConsignmentConsignorDomain] = UserAnswersReader[ConsignmentConsignorDomain].run(userAnswers)
        result.left.value.page mustBe consignor.AddContactPage
      }

      "when contact name page is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(true)

        val result: EitherType[ConsignmentConsignorDomain] = UserAnswersReader[ConsignmentConsignorDomain].run(userAnswers)
        result.left.value.page mustBe consignor.contact.NamePage
      }

      "when contact telephone number page is missing" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(consignor.EoriYesNoPage)(false)
          .unsafeSetVal(consignor.NamePage)(name)
          .unsafeSetVal(consignor.AddressPage)(address)
          .unsafeSetVal(consignor.AddContactPage)(true)
          .unsafeSetVal(consignor.contact.NamePage)(contactName)

        val result: EitherType[ConsignmentConsignorDomain] = UserAnswersReader[ConsignmentConsignorDomain].run(userAnswers)
        result.left.value.page mustBe consignor.contact.TelephoneNumberPage
      }
    }
  }
}
