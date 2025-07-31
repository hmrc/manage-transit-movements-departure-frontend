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

class TransportTaskSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  "name" - {
    "must be Transport details" - {
      "when status is CannotStartYet" in {
        val task = TransportTask(CannotStartYet)
        task.name mustEqual "Transport details"
      }
    }

    "must be Add transport details" - {
      "when status is NotStarted" in {
        val task = TransportTask(NotStarted)
        task.name mustEqual "Add transport details"
      }
    }

    "must be Edit transport details" - {
      "when status is Completed" in {
        val task = TransportTask(Completed)
        task.name mustEqual "Edit transport details"
      }

      "when status is InProgress" in {
        val task = TransportTask(InProgress)
        task.name mustEqual "Edit transport details"
      }

      "when status is Amended" in {
        val task = TransportTask(Amended)
        task.name mustEqual "Edit transport details"
      }
    }

    "must be Amend transport details" - {
      "when status is Error" in {
        val task = TransportTask(Error)
        task.name mustEqual "Amend transport details"
      }
    }
  }

  "id" - {
    "must be transport-details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = TransportTask(taskStatus)
          task.id mustEqual "transport-details"
      }
    }
  }

  "href" - {
    "must end with /transport-details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = TransportTask(taskStatus)
          task.href(lrn)(frontendAppConfig) must endWith(s"/transport-details/$lrn")
      }
    }
  }
}
