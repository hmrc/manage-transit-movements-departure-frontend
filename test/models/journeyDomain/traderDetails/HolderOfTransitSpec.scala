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
import org.scalacheck.Arbitrary.arbitrary
import pages.traderDetails.holderOfTransit._

class HolderOfTransitSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "HolderOfTransit" - {

    "can be parsed from UserAnswers" - {

      "when holder has no eori" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(false)

        val expectedResult = HolderOfTransit(
          eori = None
        )

        val result: EitherType[HolderOfTransit] = UserAnswersReader[HolderOfTransit].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when holder has an eori" in {

        val eori = arbitrary[EoriNumber].sample.value

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(true)
          .unsafeSetVal(EoriPage)(eori.value)

        val expectedResult = HolderOfTransit(
          eori = Some(eori)
        )

        val result: EitherType[HolderOfTransit] = UserAnswersReader[HolderOfTransit].run(userAnswers)

        result.value mustBe expectedResult

      }
    }

    "cannot be parsed from UserAnswers" - {

      "when answered yes to EoriYesNo but no eori provided" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(EoriYesNoPage)(true)

        val result: EitherType[HolderOfTransit] = UserAnswersReader[HolderOfTransit].run(userAnswers)

        result.left.value.page mustBe EoriPage
      }
    }
  }
}
