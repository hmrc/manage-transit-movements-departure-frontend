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
import models.reference.UnLocode
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.loading.{PlaceOfLoadingAddUnLocodeYesNoPage, PlaceOfLoadingUnLocodePage}

class LoadingDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  private val unLocode1 = arbitrary[UnLocode].sample.value

  "LoadingDomain" - {

    "can be parsed from UserAnswers" - {

      "when addUnLocode is Yes" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(PlaceOfLoadingAddUnLocodeYesNoPage)(true)
          .unsafeSetVal(PlaceOfLoadingUnLocodePage)(unLocode1)

        val expectedResult = LoadingDomain(
          unLocode = Some(unLocode1)
        )

        val result: EitherType[LoadingDomain] = UserAnswersReader[LoadingDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {}
  }
}