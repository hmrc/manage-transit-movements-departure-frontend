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

package models.journeyDomain.routeDetails.loadingAndUnloading.loading

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.routeDetails.loadingAndUnloading.loading.{CountryPage, LocationPage}

class AdditionalInformationDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {
  "AdditionalInformation" - {

    "can be parsed from UserAnswers" - {

      "when additional information has a country" in {
        val country1      = arbitrary[Country].sample.value
        val loadingPlace1 = Gen.alphaNumStr.sample.value.take(35)

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(CountryPage)(country1)
          .unsafeSetVal(LocationPage)(loadingPlace1)

        val expectedResult = AdditionalInformationDomain(
          country = country1,
          location = loadingPlace1
        )

        val result: EitherType[AdditionalInformationDomain] = UserAnswersReader[AdditionalInformationDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when additional information has no country" in {

        val placeOfLoading = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(LocationPage)(placeOfLoading)

        val result: EitherType[AdditionalInformationDomain] = UserAnswersReader[AdditionalInformationDomain].run(userAnswers)

        result.left.value.page mustBe CountryPage
      }

      "when additional information has no place of loading" in {
        val country1 = arbitrary[Country].sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(CountryPage)(country1)

        val result: EitherType[AdditionalInformationDomain] = UserAnswersReader[AdditionalInformationDomain].run(userAnswers)

        result.left.value.page mustBe LocationPage
      }
    }
  }
}
