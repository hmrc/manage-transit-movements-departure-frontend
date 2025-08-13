/*
 * Copyright 2024 HM Revenue & Customs
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

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.taskList.TaskStatus.*

class TraderDetailsTaskSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  "name" - {
    "must be Trader Details" - {
      "when status is CannotStartYet" in {
        val task = TraderDetailsTask(CannotStartYet)
        task.name mustEqual "Trader details"
      }
    }

    "must be Add trader details" - {
      "when status is NotStarted" in {
        val task = TraderDetailsTask(NotStarted)
        task.name mustEqual "Add trader details"
      }
    }

    "must be Edit trader details" - {
      "when status is Completed" in {
        val task = TraderDetailsTask(Completed)
        task.name mustEqual "Edit trader details"
      }

      "when status is InProgress" in {
        val task = TraderDetailsTask(InProgress)
        task.name mustEqual "Edit trader details"
      }

      "when status is Amended" in {
        val task = TraderDetailsTask(Amended)
        task.name mustEqual "Edit trader details"
      }
    }

    "must be Amend trader details" - {
      "when status is Error" in {
        val task = TraderDetailsTask(Error)
        task.name mustEqual "Amend trader details"
      }
    }
  }

  "id" - {
    "must be trader-details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = TraderDetailsTask(taskStatus)
          task.id mustEqual "trader-details"
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
