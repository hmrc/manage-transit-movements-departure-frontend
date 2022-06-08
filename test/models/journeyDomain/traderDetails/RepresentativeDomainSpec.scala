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
import models.EoriNumber
import models.domain.{EitherType, UserAnswersReader}
import models.traderDetails.representative.RepresentativeCapacity
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.traderDetails.representative.{CapacityPage, EoriPage, NamePage, TelephoneNumberPage}

class RepresentativeDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  val eori     = arbitrary[EoriNumber].sample.value
  val name     = Gen.alphaNumStr.sample.value
  val capacity = Gen.oneOf(RepresentativeCapacity.values).sample.value
  val phone    = Gen.alphaNumStr.sample.value

  "Representative" - {

    "can be parsed from UserAnswers" - {

      "when has all representative fields complete" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CapacityPage)(capacity)
          .unsafeSetVal(TelephoneNumberPage)(phone)

        val expectedResult = RepresentativeDomain(
          eori = eori,
          name = name,
          capacity = capacity,
          phone = phone
        )

        val result: EitherType[RepresentativeDomain] = UserAnswersReader[RepresentativeDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when representative has no eori" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CapacityPage)(capacity)
          .unsafeSetVal(TelephoneNumberPage)(phone)

        val result: EitherType[RepresentativeDomain] = UserAnswersReader[RepresentativeDomain].run(userAnswers)

        result.left.value.page mustBe EoriPage
      }

      "when representative has no name" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(CapacityPage)(capacity)
          .unsafeSetVal(TelephoneNumberPage)(phone)

        val result: EitherType[RepresentativeDomain] = UserAnswersReader[RepresentativeDomain].run(userAnswers)

        result.left.value.page mustBe NamePage
      }

      "when representative has no capacity" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(TelephoneNumberPage)(phone)

        val result: EitherType[RepresentativeDomain] = UserAnswersReader[RepresentativeDomain].run(userAnswers)

        result.left.value.page mustBe CapacityPage
      }

      "when representative has no phone" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriPage)(eori.value)
          .unsafeSetVal(NamePage)(name)
          .unsafeSetVal(CapacityPage)(capacity)

        val result: EitherType[RepresentativeDomain] = UserAnswersReader[RepresentativeDomain].run(userAnswers)

        result.left.value.page mustBe TelephoneNumberPage
      }
    }
  }
}
