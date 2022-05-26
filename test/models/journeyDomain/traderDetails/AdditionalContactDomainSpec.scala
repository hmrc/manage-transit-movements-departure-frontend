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
import org.scalacheck.Gen
import pages.traderDetails.holderOfTransit._

class AdditionalContactDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "AdditionalContact" - {

    "can be parsed from UserAnswers" - {

      "when additional contact has a name and telephone number" in {
        val name            = Gen.alphaNumStr.sample.value
        val telephoneNumber = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(ContactNamePage)(name)
          .unsafeSetVal(ContactTelephoneNumberPage)(telephoneNumber)

        val expectedResult = AdditionalContactDomain(
          name = name,
          telephoneNumber = telephoneNumber
        )

        val result: EitherType[AdditionalContactDomain] = UserAnswersReader[AdditionalContactDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when additional contact has no name" in {
        val telephoneNumber = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(ContactTelephoneNumberPage)(telephoneNumber)

        val result: EitherType[AdditionalContactDomain] = UserAnswersReader[AdditionalContactDomain].run(userAnswers)

        result.left.value.page mustBe ContactNamePage
      }

      "when additional contact has no telephone number" in {
        val name = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(ContactNamePage)(name)

        val result: EitherType[AdditionalContactDomain] = UserAnswersReader[AdditionalContactDomain].run(userAnswers)

        result.left.value.page mustBe ContactTelephoneNumberPage
      }
    }
  }
}
