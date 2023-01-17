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

package models.journeyDomain.transport.carrierDetails

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import pages.transport.carrierDetails.contact._

class ContactPersonDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "ContactPersonDomain" - {

    "can be parsed from UserAnswers" - {

      "when contact person has a name and telephone number" in {
        val name            = Gen.alphaNumStr.sample.value
        val telephoneNumber = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(NamePage, name)
          .setValue(TelephoneNumberPage, telephoneNumber)

        val expectedResult = ContactPersonDomain(
          name = name,
          telephoneNumber = telephoneNumber
        )

        val result: EitherType[ContactPersonDomain] = UserAnswersReader[ContactPersonDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when contact person has no name" in {
        val telephoneNumber = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(TelephoneNumberPage, telephoneNumber)

        val result: EitherType[ContactPersonDomain] = UserAnswersReader[ContactPersonDomain].run(userAnswers)

        result.left.value.page mustBe NamePage
      }

      "when contact person has no telephone number" in {
        val name = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(NamePage, name)

        val result: EitherType[ContactPersonDomain] = UserAnswersReader[ContactPersonDomain].run(userAnswers)

        result.left.value.page mustBe TelephoneNumberPage
      }
    }
  }
}
