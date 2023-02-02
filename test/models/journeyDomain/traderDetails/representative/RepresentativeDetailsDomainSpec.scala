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

package models.journeyDomain.traderDetails.representative

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import pages.traderDetails.representative._

class RepresentativeDetailsDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "RepresentativeDetails" - {

    "can be parsed from UserAnswers" - {

      "when representative adds name and telephone number details" in {
        val name            = Gen.alphaNumStr.sample.value
        val telephoneNumber = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(TelephoneNumberPage)(telephoneNumber)

        val expectedResult = RepresentativeDetailsDomain(
          name = name,
          telephoneNumber = telephoneNumber
        )

        val result: EitherType[RepresentativeDetailsDomain] = UserAnswersReader[RepresentativeDetailsDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when representative does not add a name" in {

        val telephoneNumber = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(TelephoneNumberPage)(telephoneNumber)

        val result: EitherType[RepresentativeDetailsDomain] = UserAnswersReader[RepresentativeDetailsDomain].run(userAnswers)

        result.left.value.page mustBe NamePage
      }

      "when representative does not add a telephone number" in {
        val name = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(NamePage)(name)

        val result: EitherType[RepresentativeDetailsDomain] = UserAnswersReader[RepresentativeDetailsDomain].run(userAnswers)

        result.left.value.page mustBe TelephoneNumberPage
      }
    }
  }
}
