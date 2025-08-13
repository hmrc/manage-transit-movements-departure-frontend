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
import models.LocalReferenceNumber
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.taskList.TaskStatus.*

class ItemsTaskSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  "name" - {
    "must be Items" - {
      "when status is CannotStartYet" in {
        val task = ItemsTask(CannotStartYet)
        task.name mustEqual "Items"
      }

      "when status is CannotContinue" in {
        val task = ItemsTask(CannotContinue)
        task.name mustEqual "Items"
      }

      "when status is Unavailable" in {
        val task = ItemsTask(Unavailable)
        task.name mustEqual "Items"
      }
    }

    "must be Add items" - {
      "when status is NotStarted" in {
        val task = ItemsTask(NotStarted)
        task.name mustEqual "Add items"
      }
    }

    "must be Edit items" - {
      "when status is Completed" in {
        val task = ItemsTask(Completed)
        task.name mustEqual "Edit items"
      }

      "when status is InProgress" in {
        val task = ItemsTask(InProgress)
        task.name mustEqual "Edit items"
      }

      "when status is Amended" in {
        val task = ItemsTask(Amended)
        task.name mustEqual "Edit items"
      }
    }

    "must be Amend items" - {
      "when status is Error" in {
        val task = ItemsTask(Error)
        task.name mustEqual "Amend items"
      }
    }
  }

  "id" - {
    "must be items" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = ItemsTask(taskStatus)
          task.id mustEqual "items"
      }
    }
  }

  "href" - {
    "must end with /items/:lrn" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = ItemsTask(taskStatus)
          task.href(lrn)(frontendAppConfig) must endWith(s"/items/$lrn")
      }
    }
  }

  "toTaskListItem" - {
    "must have a href undefined" - {
      "when task has status CannotStartYet, CannotContinue, Unavailable" in {
        forAll(Gen.oneOf(CannotStartYet, CannotContinue, Unavailable), arbitrary[LocalReferenceNumber]) {
          (taskStatus, lrn) =>
            val task   = ItemsTask(taskStatus)
            val result = task.toTaskListItem(lrn)(messages, frontendAppConfig)
            result.href must not be defined
        }
      }
    }

    "must have a href defined" - {
      "when task status is something else" in {
        forAll(Gen.oneOf(Completed, InProgress, NotStarted, Error, Amended)) {
          taskStatus =>
            val task   = ItemsTask(taskStatus)
            val result = task.toTaskListItem(lrn)(messages, frontendAppConfig)
            result.href.value must endWith(s"/items/$lrn")
        }
      }
    }
  }
}
