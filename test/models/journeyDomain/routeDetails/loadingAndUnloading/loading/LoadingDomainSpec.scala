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
import models.reference.{Country, UnLocode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.routeDetails.loadingAndUnloading.loading._

class LoadingDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val unLocode     = arbitrary[UnLocode].sample.value
  private val country      = arbitrary[Country].sample.value
  private val loadingPlace = Gen.alphaNumStr.sample.value.take(35)

  "LoadingDomain" - {

    "can be parsed from UserAnswers" - {

      "when addUnLocode is Yes" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddUnLocodeYesNoPage)(true)
          .unsafeSetVal(PlaceOfLoadingUnLocodePage)(unLocode)
          .unsafeSetVal(AddExtraInformationYesNoPage)(true)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(PlaceOfLoadingLocationPage)(loadingPlace)

        val expectedResult = LoadingDomain(
          unLocode = Some(unLocode),
          additionalInformation = Some(AdditionalInformationDomain(country, loadingPlace))
        )

        val result: EitherType[LoadingDomain] = UserAnswersReader[LoadingDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when addUnLocode is No" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddUnLocodeYesNoPage)(false)
          .unsafeSetVal(CountryPage)(country)
          .unsafeSetVal(PlaceOfLoadingLocationPage)(loadingPlace)

        val expectedResult = LoadingDomain(
          unLocode = None,
          additionalInformation = Some(AdditionalInformationDomain(country, loadingPlace))
        )

        val result: EitherType[LoadingDomain] = UserAnswersReader[LoadingDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {
      "when  add UnLocode is Yes but UnLocode has no value" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(AddUnLocodeYesNoPage)(true)

        val result: EitherType[LoadingDomain] = UserAnswersReader[LoadingDomain].run(userAnswers)

        result.left.value.page mustBe PlaceOfLoadingUnLocodePage
      }
    }
  }
}
