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

package viewModels.taskList

import base.SpecBase
import generators.Generators
import models.NormalMode
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.preRequisites._
import viewModels.taskList.TaskStatus._

class TransportTaskSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "name" - {
    "must be Transport details" - {
      "when status is CannotStartYet" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = TransportTask(CannotStartYet, href)
            task.name mustBe "Transport details"
        }
      }
    }

    "must be Add transport details" - {
      "when status is NotStarted" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = TransportTask(NotStarted, href)
            task.name mustBe "Add transport details"
        }
      }
    }

    "must be Edit route details" - {
      "when status is Completed" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = TransportTask(Completed, href)
            task.name mustBe "Edit transport details"
        }
      }

      "when status is InProgress" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = TransportTask(InProgress, href)
            task.name mustBe "Edit transport details"
        }
      }
    }
  }

  "id" - {
    "must be transport-details" in {
      val task = TransportTask(emptyUserAnswers)
      task.id mustBe "transport-details"
    }
  }

  "apply" - {
    "when NotStarted" in {
      val userAnswers = emptyUserAnswers
      val task        = TransportTask(userAnswers)
      task.status mustBe NotStarted
      task.href.get mustBe
        controllers.transport.preRequisites.routes.SameUcrYesNoController.onPageLoad(userAnswers.lrn, NormalMode).url
    }

    "when InProgress" ignore {
      val userAnswers = emptyUserAnswers
        .setValue(SameUcrYesNoPage, true)
      val task = TransportTask(userAnswers)
      task.status mustBe InProgress
      task.href.get mustBe ???
    }

    "when Completed" ignore {
      forAll(arbitraryTransportAnswers(emptyUserAnswers)) {
        userAnswers =>
          val task = TransportTask(userAnswers)
          task.status mustBe Completed
          task.href.get mustBe ???
      }
    }
  }
}
