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
import controllers.routes._
import controllers.traderDetails.routes._
import generators.Generators
import models.DeclarationType.Option4
import models.{DeclarationType, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.DeclarationTypePage
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
    "status" - {
      "must be NotStarted" in {
        val task = TraderDetailsTask(emptyUserAnswers)
        task.status mustBe NotStarted
      }
    }
  }

  "href" - {
    "must be None" - {
      "when status is CannotStartYet" in {
        val href = TraderDetailsTask.href(emptyUserAnswers, CannotStartYet)
        href mustNot be(defined)
      }
    }

    "must point to Transit Holder EORI Yes No" - {
      "when status is NotStarted or InProgress" - {
        "and declaration type is not TIR" in {
          forAll(
            arbitrary[DeclarationType].suchThat(_ != Option4),
            Gen.oneOf(NotStarted, InProgress)
          ) {
            (declarationType, status) =>
              val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, declarationType)
              val href        = TraderDetailsTask.href(userAnswers, status)
              href.get mustBe TransitHolderEoriYesNoController.onPageLoad(userAnswers.lrn, NormalMode).url
          }
        }
      }
    }

    "must point to Transit Procedure TIR identification number" - {
      "when status is NotStarted or InProgress" - {
        "and declaration type is TIR" ignore {
          forAll(Gen.oneOf(NotStarted, InProgress)) {
            status =>
              val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, Option4)
              val href        = TraderDetailsTask.href(userAnswers, status)
              href.get mustBe ???
          }
        }
      }
    }

    "must point to session expired" - {
      "when status is NotStarted or InProgress" - {
        "and declaration type is undefined" ignore {
          forAll(Gen.oneOf(NotStarted, InProgress)) {
            status =>
              val href = TraderDetailsTask.href(emptyUserAnswers, status)
              href.get mustBe SessionExpiredController.onPageLoad()
          }
        }
      }
    }

    "must point to trader details check your answers" - {
      "when status is Completed" ignore {
        val href = TraderDetailsTask.href(emptyUserAnswers, Completed)
        href.get mustBe ???
      }
    }
  }
}
