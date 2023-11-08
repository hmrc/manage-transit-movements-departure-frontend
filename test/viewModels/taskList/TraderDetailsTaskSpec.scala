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
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.taskList.TaskStatus._

class TraderDetailsTaskSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "name" - {
    "must be Trader Details" - {
      "when status is CannotStartYet" in {
        val task = TraderDetailsTask(CannotStartYet)
        task.name mustBe "Trader details"
      }
    }

    "must be Add trader details" - {
      "when status is NotStarted" in {
        val task = TraderDetailsTask(NotStarted)
        task.name mustBe "Add trader details"
      }
    }

    "must be Edit trader details" - {
      "when status is Completed" in {
        val task = TraderDetailsTask(Completed)
        task.name mustBe "Edit trader details"
      }

      "when status is InProgress" in {
        val task = TraderDetailsTask(InProgress)
        task.name mustBe "Edit trader details"
      }

      "when status is Amended" in {
        val task = TraderDetailsTask(Amended)
        task.name mustBe "Edit trader details"
      }
    }
  }

  "id" - {
    "must be trader-details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = TraderDetailsTask(taskStatus)
          task.id mustBe "trader-details"
      }
    }
  }

  "href" - {
    "must end with /trader-details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = TraderDetailsTask(taskStatus)
          task.href(lrn)(frontendAppConfig) must endWith(s"/trader-details/$lrn")
      }
    }
  }
}
