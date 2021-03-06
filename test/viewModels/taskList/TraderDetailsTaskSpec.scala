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
import controllers.traderDetails.holderOfTransit.{routes => holderOfTransitRoutes}
import controllers.traderDetails.{routes => traderDetailsRoutes}
import generators.{Generators, TraderDetailsUserAnswersGenerator}
import models.DeclarationType.Option4
import models.{DeclarationType, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.DeclarationTypePage
import pages.traderDetails.{holderOfTransit => hot}
import viewModels.taskList.TaskStatus._

class TraderDetailsTaskSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with TraderDetailsUserAnswersGenerator {

  "name" - {
    "must be Trader Details" - {
      "when status is CannotStartYet" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = TraderDetailsTask(CannotStartYet, href)
            task.name mustBe "Trader details"
        }
      }
    }

    "must be Add trader details" - {
      "when status is NotStarted" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = TraderDetailsTask(NotStarted, href)
            task.name mustBe "Add trader details"
        }
      }
    }

    "must be Edit trader details" - {
      "when status is Completed" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = TraderDetailsTask(Completed, href)
            task.name mustBe "Edit trader details"
        }
      }

      "when status is InProgress" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = TraderDetailsTask(InProgress, href)
            task.name mustBe "Edit trader details"
        }
      }
    }
  }

  "id" - {
    "must be trader-details" in {
      val task = TraderDetailsTask(emptyUserAnswers)
      task.id mustBe "trader-details"
    }
  }

  "apply" - {
    "when NotStarted" - {
      "and TIR declaration type" in {
        val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, Option4)
        val task        = TraderDetailsTask(userAnswers)
        task.status mustBe NotStarted
        task.href.get mustBe holderOfTransitRoutes.TirIdentificationYesNoController.onPageLoad(userAnswers.lrn, NormalMode).url
      }

      "and non-TIR declaration type" in {
        forAll(arbitrary[DeclarationType](arbitraryNonOption4DeclarationType)) {
          declarationType =>
            val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, declarationType)
            val task        = TraderDetailsTask(userAnswers)
            task.status mustBe NotStarted
            task.href.get mustBe holderOfTransitRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, NormalMode).url
        }
      }
    }

    "when InProgress" - {

      "and TIR declaration type" in {
        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, Option4)
          .setValue(hot.TirIdentificationYesNoPage, true)

        val task = TraderDetailsTask(userAnswers)
        task.status mustBe InProgress
        task.href.get mustBe holderOfTransitRoutes.TirIdentificationController.onPageLoad(userAnswers.lrn, NormalMode).url
      }

      "and non-TIR declaration type" in {
        forAll(arbitrary[DeclarationType](arbitraryNonOption4DeclarationType)) {
          declarationType =>
            val userAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(hot.EoriYesNoPage, true)

            val task = TraderDetailsTask(userAnswers)
            task.status mustBe InProgress
            task.href.get mustBe holderOfTransitRoutes.EoriController.onPageLoad(userAnswers.lrn, NormalMode).url
        }
      }
    }

    "when Completed" - {
      "when valid journey is completed" in {
        forAll(arbitraryTraderDetailsAnswers(emptyUserAnswers)) {
          userAnswers =>
            val task = TraderDetailsTask(userAnswers)
            task.status mustBe Completed
            task.href.get mustBe traderDetailsRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn).url
        }
      }
    }
  }
}
