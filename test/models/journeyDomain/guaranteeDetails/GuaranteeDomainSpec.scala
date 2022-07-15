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
import pages.guaranteeDetails.guarantee._
import pages.preTaskList.DeclarationTypePage

class GuaranteeDomainSpec extends SpecBase with Generators {

  private val `0,1,2,4,9` = arbitrary01249GuaranteeType.arbitrary
  private val `3`         = Gen.const(CashDepositGuarantee)
  private val `5`         = Gen.const(GuaranteeWaiverSecured)
  private val `8`         = Gen.const(GuaranteeNotRequiredExemptPublicBody)
  private val `A,R`       = arbitraryARGuaranteeType.arbitrary
  private val `B`         = Gen.const(TIRGuarantee)

  "GuaranteeDomain" - {

    "can be parsed from UserAnswers" - {
      "when 0,1,2,4,5,9 guarantee type" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val guaranteeType   = `0,1,2,4,9`.sample.value
        val grn             = Gen.alphaNumStr.sample.value
        val accessCode      = Gen.alphaNumStr.sample.value
        val liabilityAmount = arbitrary[BigDecimal].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, declarationType)
          .setValue(GuaranteeTypePage(index), guaranteeType)
          .setValue(ReferenceNumberPage(index), grn)
          .setValue(AccessCodePage(index), accessCode)
          .setValue(LiabilityAmountPage(index), liabilityAmount)

        val expectedResult = GuaranteeOfTypes01249(
          `type` = guaranteeType,
          grn = grn,
          accessCode = accessCode,
          liabilityAmount = liabilityAmount
        )(index)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when 5 guarantee type" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val guaranteeType   = `5`.sample.value
        val grn             = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, declarationType)
          .setValue(GuaranteeTypePage(index), guaranteeType)
          .setValue(ReferenceNumberPage(index), grn)

        val expectedResult = GuaranteeOfType5(
          `type` = guaranteeType,
          grn = grn
        )(index)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when A,R guarantee type" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val guaranteeType   = `A,R`.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, declarationType)
          .setValue(GuaranteeTypePage(index), guaranteeType)

        val expectedResult = GuaranteeOfTypesABR(
          `type` = guaranteeType
        )(index)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when B guarantee type" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val guaranteeType   = `B`.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, declarationType)
          .setValue(DeclarationTypePage, Option4)
          .setValue(GuaranteeTypePage(index), guaranteeType)

        val expectedResult = GuaranteeOfTypesABR(
          `type` = guaranteeType
        )(index)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when 8 guarantee type" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val guaranteeType   = `8`.sample.value
        val otherReference  = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, declarationType)
          .setValue(GuaranteeTypePage(index), guaranteeType)
          .setValue(OtherReferencePage(index), otherReference)

        val expectedResult = GuaranteeOfType8(
          `type` = guaranteeType,
          otherReference = otherReference
        )(index)

        val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
          GuaranteeDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when 3 guarantee type" - {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val guaranteeType   = `3`.sample.value
        val otherReference  = Gen.alphaNumStr.sample.value

        "when otherReference provided" in {

          val userAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, declarationType)
            .setValue(GuaranteeTypePage(index), guaranteeType)
            .setValue(OtherReferenceYesNoPage(index), true)
            .setValue(OtherReferencePage(index), otherReference)

          val expectedResult = GuaranteeOfType3(
            `type` = guaranteeType,
            otherReference = Some(otherReference)
          )(index)

          val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
            GuaranteeDomain.userAnswersReader(index)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

        "when otherReference not provided" in {

          val userAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, declarationType)
            .setValue(GuaranteeTypePage(index), guaranteeType)
            .setValue(OtherReferenceYesNoPage(index), false)

          val expectedResult = GuaranteeOfType3(
            `type` = guaranteeType,
            otherReference = None
          )(index)

          val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
            GuaranteeDomain.userAnswersReader(index)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

      }
    }

    "cannot be parsed from user answers" - {

      "when non-TIR" - {
        "when 0,1,2,4,5,9 guarantee type" - {
          "when grn missing" in {
            val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
            val guaranteeType   = `0,1,2,4,9`.sample.value

            val userAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(GuaranteeTypePage(index), guaranteeType)

            val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
              GuaranteeDomain.userAnswersReader(index)
            ).run(userAnswers)

            result.left.value.page mustBe ReferenceNumberPage(index)
          }

          "when access code missing" in {
            val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
            val guaranteeType   = `0,1,2,4,9`.sample.value
            val grn             = Gen.alphaNumStr.sample.value

            val userAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(GuaranteeTypePage(index), guaranteeType)
              .setValue(ReferenceNumberPage(index), grn)

            val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
              GuaranteeDomain.userAnswersReader(index)
            ).run(userAnswers)

            result.left.value.page mustBe AccessCodePage(index)
          }

          "when liability amount missing" in {
            val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
            val guaranteeType   = `0,1,2,4,9`.sample.value
            val grn             = Gen.alphaNumStr.sample.value
            val accessCOde      = Gen.alphaNumStr.sample.value

            val userAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(GuaranteeTypePage(index), guaranteeType)
              .setValue(ReferenceNumberPage(index), grn)
              .setValue(AccessCodePage(index), accessCOde)

            val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
              GuaranteeDomain.userAnswersReader(index)
            ).run(userAnswers)

            result.left.value.page mustBe LiabilityAmountPage(index)
          }
        }

        "when B guarantee type" in {
          val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
          val guaranteeType   = `B`.sample.value

          val userAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, declarationType)
            .setValue(GuaranteeTypePage(index), guaranteeType)

          val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
            GuaranteeDomain.userAnswersReader(index)
          ).run(userAnswers)

          result.left.value.page mustBe GuaranteeTypePage(index)
        }

        "when 8 guarantee type" in {
          val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
          val guaranteeType   = `8`.sample.value

          val userAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, declarationType)
            .setValue(GuaranteeTypePage(index), guaranteeType)

          val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
            GuaranteeDomain.userAnswersReader(index)
          ).run(userAnswers)

          result.left.value.page mustBe OtherReferencePage(index)
        }

        "when 3 guarantee type" - {
          val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
          val guaranteeType   = `3`.sample.value

          "when otherReferenceYesNoPage is unanswered" in {

            val userAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(GuaranteeTypePage(index), guaranteeType)

            val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
              GuaranteeDomain.userAnswersReader(index)
            ).run(userAnswers)

            result.left.value.page mustBe OtherReferenceYesNoPage(index)
          }

          "when otherReferenceYesNoPage is true and otherReference is unanswered" in {

            val userAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(GuaranteeTypePage(index), guaranteeType)
              .setValue(OtherReferenceYesNoPage(index), true)

            val result: EitherType[GuaranteeDomain] = UserAnswersReader[GuaranteeDomain](
              GuaranteeDomain.userAnswersReader(index)
            ).run(userAnswers)

            result.left.value.page mustBe OtherReferencePage(index)
          }
        }

      }
    }
  }
}
