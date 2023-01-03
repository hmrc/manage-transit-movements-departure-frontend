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

package viewModels.guaranteeDetails

import base.SpecBase
import generators.Generators
import models.DeclarationType.Option4
import models.GuaranteeType._
import models.{DeclarationType, GuaranteeType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.guaranteeDetails.guarantee.{GuaranteeTypePage, OtherReferenceYesNoPage}
import pages.preTaskList._

class GuaranteeViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "apply" - {

    "must return row for each answer" - {
      "when TIR" - {
        "must return 1 row" in {
          val initialAnswers = emptyUserAnswers.setValue(DeclarationTypePage, Option4)

          forAll(arbitraryGuaranteeAnswers(initialAnswers, index)) {
            answers =>
              val result  = GuaranteeViewModel(answers, index)
              val section = result.section
              section.sectionTitle mustNot be(defined)
              section.rows.length mustBe 1
          }
        }
      }

      "when not TIR" - {
        "when 0,1,2,4,9 guarantee type" - {
          "must return 4 rows" in {
            val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
            val guaranteeType   = arbitrary[GuaranteeType](arbitrary01249GuaranteeType).sample.value
            val initialAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(GuaranteeTypePage(index), guaranteeType)

            forAll(arbitraryGuaranteeAnswers(initialAnswers, index)) {
              answers =>
                val result  = GuaranteeViewModel(answers, index)
                val section = result.section
                section.sectionTitle mustNot be(defined)
                section.rows.length mustBe 4
            }
          }
        }

        "when 5 guarantee type" - {
          "must return 2 rows" in {
            val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
            val initialAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(GuaranteeTypePage(index), GuaranteeWaiverSecured)

            forAll(arbitraryGuaranteeAnswers(initialAnswers, index)) {
              answers =>
                val result  = GuaranteeViewModel(answers, index)
                val section = result.section
                section.sectionTitle mustNot be(defined)
                section.rows.length mustBe 2
            }
          }
        }

        "when A,R guarantee type" - {
          "must return 1 row" in {
            val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
            val guaranteeType   = arbitrary[GuaranteeType](arbitraryARGuaranteeType).sample.value
            val initialAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(GuaranteeTypePage(index), guaranteeType)

            forAll(arbitraryGuaranteeAnswers(initialAnswers, index)) {
              answers =>
                val result  = GuaranteeViewModel(answers, index)
                val section = result.section
                section.sectionTitle mustNot be(defined)
                section.rows.length mustBe 1
            }
          }
        }

        "when 8 guarantee type" - {
          "must return 2 rows" in {
            val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
            val initialAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(GuaranteeTypePage(index), GuaranteeNotRequiredExemptPublicBody)

            forAll(arbitraryGuaranteeAnswers(initialAnswers, index)) {
              answers =>
                val result  = GuaranteeViewModel(answers, index)
                val section = result.section
                section.sectionTitle mustNot be(defined)
                section.rows.length mustBe 2
            }
          }
        }

        "when 3 guarantee type" - {
          "when other ref answered" - {
            "must return 3 rows" in {
              val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
              val initialAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage, declarationType)
                .setValue(GuaranteeTypePage(index), CashDepositGuarantee)
                .setValue(OtherReferenceYesNoPage(index), true)

              forAll(arbitraryGuaranteeAnswers(initialAnswers, index)) {
                answers =>
                  val result  = GuaranteeViewModel(answers, index)
                  val section = result.section
                  section.sectionTitle mustNot be(defined)
                  section.rows.length mustBe 3
              }
            }
          }

          "when other ref unanswered" - {
            "must return 2 rows" in {
              val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
              val initialAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage, declarationType)
                .setValue(GuaranteeTypePage(index), CashDepositGuarantee)
                .setValue(OtherReferenceYesNoPage(index), false)

              forAll(arbitraryGuaranteeAnswers(initialAnswers, index)) {
                answers =>
                  val result  = GuaranteeViewModel(answers, index)
                  val section = result.section
                  section.sectionTitle mustNot be(defined)
                  section.rows.length mustBe 2
              }
            }
          }
        }
      }
    }
  }
}
