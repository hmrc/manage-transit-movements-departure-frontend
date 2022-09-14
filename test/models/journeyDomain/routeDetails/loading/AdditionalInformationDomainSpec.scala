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

package models.journeyDomain.routeDetails.loading

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.routeDetails.loading.PlaceOfLoadingCountryPage
import pages.routeDetails.locationOfGoods.contact._

class AdditionalInformationDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {
  // TODO: Update tests once Place of loading has been added
  "AdditionalInformation" - {

    "can be parsed from UserAnswers" - {

      "when additional information has a country" ignore {
        val country1 = arbitrary[Country].sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(PlaceOfLoadingCountryPage)(country1)

        val expectedResult = AdditionalInformationDomain(
          country = country1
        )

        val result: EitherType[AdditionalInformationDomain] = UserAnswersReader[AdditionalInformationDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when additional contact has no country" ignore {

        val placeOfLoading = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(???)(placeOfLoading)

        val result: EitherType[AdditionalInformationDomain] = UserAnswersReader[AdditionalInformationDomain].run(userAnswers)

        result.left.value.page mustBe PlaceOfLoadingCountryPage
      }

      "when additional information has no place of loading" ignore {
        val country1 = arbitrary[Country].sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(PlaceOfLoadingCountryPage)(country1)

        val result: EitherType[AdditionalInformationDomain] = UserAnswersReader[AdditionalInformationDomain].run(userAnswers)

        result.left.value.page mustBe ???
      }
    }
  }
}