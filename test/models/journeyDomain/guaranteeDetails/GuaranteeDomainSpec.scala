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

package models.journeyDomain.guaranteeDetails

import base.SpecBase
import generators.Generators
import models.domain.{EitherType, UserAnswersReader}
import models.guaranteeDetails.GuaranteeType
import models.guaranteeDetails.GuaranteeType.TIRGuarantee
import org.scalacheck.Arbitrary.arbitrary
import pages.guaranteeDetails.GuaranteeTypePage

class GuaranteeDomainSpec extends SpecBase with Generators {

  "GuaranteeDomain" - {

    val guaranteeType = arbitrary[GuaranteeType](arbitraryNonTIRGuaranteeType).sample.value

    "can be parsed from UserAnswers" - {
      "when valid data set" in {
        val userAnswers = emptyUserAnswers
          .setValue(GuaranteeTypePage(index), guaranteeType)

        val expectedResult = GuaranteeDomain(
          `type` = guaranteeType
        )(index)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from user answers" - {
      "when guarantee has TIR guarantee type" in {
        val userAnswers = emptyUserAnswers
          .setValue(GuaranteeTypePage(index), TIRGuarantee)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.left.value.page mustBe GuaranteeTypePage(index)
      }
    }
  }
}
