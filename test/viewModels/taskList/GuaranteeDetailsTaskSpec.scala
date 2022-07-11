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

package viewModels.taskList

import base.SpecBase
import controllers.guaranteeDetails.{routes => gdRoutes}
import generators.{Generators, GuaranteeDetailsUserAnswersGenerator}
import models.DeclarationType.Option4
import models.guaranteeDetails.GuaranteeType
import models.{DeclarationType, Index, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.guaranteeDetails._
import pages.preTaskList.DeclarationTypePage
import viewModels.taskList.TaskStatus._

class GuaranteeDetailsTaskSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with GuaranteeDetailsUserAnswersGenerator {

  "name" - {
    "must be Guarantee Details" - {
      "when status is CannotStartYet" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = GuaranteeDetailsTask(CannotStartYet, href)
            task.name mustBe "Guarantee details"
        }
      }
    }

    "must be Add guarantee details" - {
      "when status is NotStarted" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = GuaranteeDetailsTask(NotStarted, href)
            task.name mustBe "Add guarantee details"
        }
      }
    }

    "must be Edit guarantee details" - {
      "when status is Completed" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = GuaranteeDetailsTask(Completed, href)
            task.name mustBe "Edit guarantee details"
        }
      }

      "when status is InProgress" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = GuaranteeDetailsTask(InProgress, href)
            task.name mustBe "Edit guarantee details"
        }
      }
    }
  }

  "id" - {
    "must be guarantee-details" in {
      val task = GuaranteeDetailsTask(emptyUserAnswers)
      task.id mustBe "guarantee-details"
    }
  }

  "apply" - {
    "when NotStarted" - {
      "and TIR declaration type" in {
        val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, Option4)
        val task        = GuaranteeDetailsTask(userAnswers)
        task.status mustBe NotStarted
        task.href.get mustBe gdRoutes.GuaranteeAddedTIRController.onPageLoad(userAnswers.lrn).url
      }

      "and non-TIR declaration type" in {
        forAll(arbitrary[DeclarationType](arbitraryNonOption4DeclarationType)) {
          declarationType =>
            val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, declarationType)
            val task        = GuaranteeDetailsTask(userAnswers)
            task.status mustBe NotStarted
            task.href.get mustBe gdRoutes.GuaranteeTypeController.onPageLoad(userAnswers.lrn, NormalMode, Index(0)).url
        }
      }
    }

    "when InProgress" - {

      "and 0,1,2,4,5,9 guarantee type" in {
        forAll(arbitrary[DeclarationType](arbitraryNonOption4DeclarationType), arbitrary[GuaranteeType](arbitrary012459GuaranteeType)) {
          (declarationType, guaranteeType) =>
            val userAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(GuaranteeTypePage(index), guaranteeType)

            val task = GuaranteeDetailsTask(userAnswers)
            task.status mustBe InProgress
            task.href.get mustBe gdRoutes.ReferenceNumberController.onPageLoad(userAnswers.lrn, NormalMode, index).url
        }
      }
    }

    "when Completed" - {
      "when TIR declaration type" in {
        val initialAnswers = emptyUserAnswers.setValue(DeclarationTypePage, Option4)
        forAll(arbitraryGuaranteeDetailsAnswers(initialAnswers)) {
          userAnswers =>
            val task = GuaranteeDetailsTask(userAnswers)
            task.status mustBe Completed
            task.href.get mustBe gdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn, Index(0)).url
        }
      }

      "when non-TIR declaration type" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val initialAnswers  = emptyUserAnswers.setValue(DeclarationTypePage, declarationType)
        forAll(arbitraryGuaranteeDetailsAnswers(initialAnswers)) {
          userAnswers =>
            val task = GuaranteeDetailsTask(userAnswers)
            task.status mustBe Completed
            task.href.get mustBe gdRoutes.AddAnotherGuaranteeController.onPageLoad(userAnswers.lrn).url
        }
      }
    }
  }
}
