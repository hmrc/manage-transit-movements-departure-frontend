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
import controllers.routes
import controllers.traderDetails.holderOfTransit.{routes => holderOfTransitRoutes}
import generators.Generators
import models.DeclarationType.Option4
import models.{DeclarationType, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.DeclarationTypePage
import pages.traderDetails.holderOfTransit.{EoriPage, EoriYesNoPage}
import viewModels.taskList.TaskStatus._

class TraderDetailsTaskSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

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
    "must be general-information" in {
      val task = TraderDetailsTask(emptyUserAnswers)
      task.id mustBe "trader-details"
    }
  }

  "apply" - {
    "when NotStarted" - {
      "and declaration type undefined" in {
        val task = TraderDetailsTask(emptyUserAnswers)
        task.status mustBe NotStarted
        task.href.get mustBe routes.SessionExpiredController.onPageLoad().url
      }

      "and TIR declaration type" ignore {
        val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, Option4)
        val task        = TraderDetailsTask(userAnswers)
        task.status mustBe NotStarted
        task.href.get mustBe ???
      }

      "and non-TIR declaration type" in {
        forAll(arbitrary[DeclarationType].suchThat(_ != Option4)) {
          declarationType =>
            val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, declarationType)
            val task        = TraderDetailsTask(userAnswers)
            task.status mustBe NotStarted
            task.href.get mustBe holderOfTransitRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, NormalMode).url
        }
      }
    }

    "when InProgress" - {

      val baseAnswers = emptyUserAnswers.setValue(EoriYesNoPage, true)

      "and declaration type undefined" ignore {
        val task = TraderDetailsTask(baseAnswers)
        task.status mustBe InProgress
        task.href.get mustBe routes.SessionExpiredController.onPageLoad().url
      }

      "and TIR declaration type" ignore {
        val userAnswers = baseAnswers.setValue(DeclarationTypePage, Option4)
        val task        = TraderDetailsTask(userAnswers)
        task.status mustBe InProgress
        task.href.get mustBe ???
      }

      "and non-TIR declaration type" ignore {
        forAll(arbitrary[DeclarationType].suchThat(_ != Option4)) {
          declarationType =>
            val userAnswers = baseAnswers.setValue(DeclarationTypePage, declarationType)
            val task        = TraderDetailsTask(userAnswers)
            task.status mustBe InProgress
            task.href.get mustBe holderOfTransitRoutes.EoriYesNoController.onPageLoad(userAnswers.lrn, NormalMode).url
        }
      }
    }

    "when Completed" - {
      "when holder of transit eori known" in {
        val userAnswers = emptyUserAnswers
          .setValue(EoriYesNoPage, true)
          .setValue(EoriPage, eoriNumber.value)

        val task = TraderDetailsTask(userAnswers)
        task.status mustBe Completed
        task.href.get mustBe "#"
      }

      "when holder of transit eori unknown" in {
        val userAnswers = emptyUserAnswers
          .setValue(EoriYesNoPage, false)

        val task = TraderDetailsTask(userAnswers)
        task.status mustBe Completed
        task.href.get mustBe "#"
      }
    }
  }
}
