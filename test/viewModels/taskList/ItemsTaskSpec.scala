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

class ItemsTaskSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "name" - {
    "must be Items" - {
      "when status is CannotStartYet" in {
        val task = ItemsTask(CannotStartYet)
        task.name mustBe "Items"
      }
    }

    "must be Add items" - {
      "when status is NotStarted" in {
        val task = ItemsTask(NotStarted)
        task.name mustBe "Add items"
      }
    }

    "must be Edit items" - {
      "when status is Completed" in {
        val task = ItemsTask(Completed)
        task.name mustBe "Edit items"
      }

      "when status is InProgress" in {
        val task = ItemsTask(InProgress)
        task.name mustBe "Edit items"
      }

      "when status is Amended" in {
        val task = ItemsTask(Amended)
        task.name mustBe "Edit items"
      }
    }

    "must be Amend items" - {
      "when status is Error" in {
        val task = ItemsTask(Error)
        task.name mustBe "Amend items"
      }
    }
  }

  "id" - {
    "must be items" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = ItemsTask(taskStatus)
          task.id mustBe "items"
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
}
