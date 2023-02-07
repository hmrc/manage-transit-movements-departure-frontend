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

package models.journeyDomain.guaranteeDetails

import base.SpecBase
import generators.Generators
import models.DeclarationType.Option4
import models.GuaranteeType._
import models.domain._
import models.journeyDomain.guaranteeDetails.GuaranteeDomain.GuaranteeOfTypesAB
import models.{DeclarationType, Index}
import org.scalacheck.Arbitrary.arbitrary
import pages.guaranteeDetails.guarantee.GuaranteeTypePage
import pages.preTaskList.DeclarationTypePage

class GuaranteeDetailsDomainSpec extends SpecBase with Generators {

  "GuaranteeDetailsDomain" - {

    "can be parsed from UserAnswers" - {
      "when TIR declaration type" in {
        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, Option4)
          .setValue(GuaranteeTypePage(Index(0)), TIRGuarantee)

        val expectedResult = GuaranteeDetailsDomain(
          Seq(
            GuaranteeOfTypesAB(
              `type` = TIRGuarantee
            )(Index(0))
          )
        )

        val result: EitherType[GuaranteeDetailsDomain] = UserAnswersReader[GuaranteeDetailsDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when non-TIR declaration type" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, declarationType)
          .setValue(GuaranteeTypePage(Index(0)), GuaranteeWaiverByAgreement)

        val expectedResult = GuaranteeDetailsDomain(
          Seq(
            GuaranteeOfTypesAB(
              `type` = GuaranteeWaiverByAgreement
            )(Index(0))
          )
        )

        val result: EitherType[GuaranteeDetailsDomain] = UserAnswersReader[GuaranteeDetailsDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from user answers" - {
      "when guarantees empty" in {
        val declarationType = arbitrary[DeclarationType].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, declarationType)

        val result: EitherType[GuaranteeDetailsDomain] = UserAnswersReader[GuaranteeDetailsDomain].run(userAnswers)

        result.left.value.page mustBe GuaranteeTypePage(Index(0))
      }
    }
  }

}
