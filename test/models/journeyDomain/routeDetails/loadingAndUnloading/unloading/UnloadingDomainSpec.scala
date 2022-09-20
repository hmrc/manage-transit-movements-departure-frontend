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

package models.journeyDomain.routeDetails.loadingAndUnloading.unloading

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.routeDetails.loadingAndUnloading.unloading.UnloadingDomain
import models.reference.UnLocode
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.unloading.{AddPlaceOfUnloadingPage, PlaceOfUnloadingUnLocodeYesNoPage}

class UnloadingDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {
  private val unLocode = arbitrary[UnLocode].sample.value
//  private val addUnLocode = arbitrary[Boolean].sample.value

  "UnloadingDomain" - {

    "can be parsed from UserAnswers" - {

      "when add a place of unloading UN/LOCODE is yes" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(PlaceOfUnloadingUnLocodeYesNoPage)(true)

        val expectedResult = UnloadingDomain(
          unLocode = Some(UnLocode("", ""))
        )

        val result: EitherType[UnloadingDomain] = UserAnswersReader[UnloadingDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when add a place of unloading UN/LOCODE is no" in {
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(PlaceOfUnloadingUnLocodeYesNoPage)(false)

        val expectedResult = UnloadingDomain(
          unLocode = None
        )

        val result: EitherType[UnloadingDomain] = UserAnswersReader[UnloadingDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }
  }
}
