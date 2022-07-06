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
import models.DeclarationType
import models.DeclarationType.Option4
import models.domain.{EitherType, UserAnswersReader}
import models.guaranteeDetails.GuaranteeType._
import models.journeyDomain.guaranteeDetails.GuaranteeDomain._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.guaranteeDetails._
import pages.preTaskList.DeclarationTypePage

class GuaranteeDomainSpec extends SpecBase with Generators {

  "GuaranteeDomain" - {

    "can be parsed from UserAnswers" - {
      "when 0, 1, 2, 4, 5, 9 guarantee type" in {
        val guaranteeType = Gen
          .oneOf(
            GuaranteeWaiver,
            ComprehensiveGuarantee,
            IndividualGuarantee,
            FlatRateVoucher,
            GuaranteeWaiverSecured,
            IndividualGuaranteeMultiple
          )
          .sample
          .value

        val grn = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(GuaranteeTypePage(index), guaranteeType)
          .setValue(ReferenceNumberPage(index), grn)

        val expectedResult = FullGuarantee(
          `type` = guaranteeType,
          grn = grn
        )(index)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      // TODO - what is J?
      "when A, J, R guarantee type" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val guaranteeType = Gen
          .oneOf(
            GuaranteeWaiverByAgreement,
            GuaranteeNotRequired
          )
          .sample
          .value

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, declarationType)
          .setValue(GuaranteeTypePage(index), guaranteeType)

        val expectedResult = GuaranteeTypeOnly(
          `type` = guaranteeType
        )(index)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when B guarantee type" in {
        val guaranteeType = TIRGuarantee

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, Option4)
          .setValue(GuaranteeTypePage(index), guaranteeType)

        val expectedResult = GuaranteeTypeOnly(
          `type` = guaranteeType
        )(index)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when 8 guarantee type" in {
        val guaranteeType = GuaranteeNotRequiredExemptPublicBody

        val userAnswers = emptyUserAnswers
          .setValue(GuaranteeTypePage(index), guaranteeType)

        val expectedResult = GuaranteeWithOtherReference(
          `type` = guaranteeType,
          otherReference = ""
        )(index)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when 3 guarantee type" in {
        val guaranteeType = CashDepositGuarantee

        val userAnswers = emptyUserAnswers
          .setValue(GuaranteeTypePage(index), guaranteeType)

        val expectedResult = GuaranteeWithOptionalOtherReference(
          `type` = guaranteeType,
          otherReference = None
        )(index)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from user answers" - {}
  }
}
