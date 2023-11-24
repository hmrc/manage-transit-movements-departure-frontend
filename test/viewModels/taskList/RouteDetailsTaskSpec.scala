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

class RouteDetailsTaskSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "name" - {
    "must be Route details" - {
      "when status is CannotStartYet" in {
        val task = RouteDetailsTask(CannotStartYet)
        task.name mustBe "Route details"
      }
    }

    "must be Add route details" - {
      "when status is NotStarted" in {
        val task = RouteDetailsTask(NotStarted)
        task.name mustBe "Add route details"
      }
    }

    "must be Edit route details" - {
      "when status is Completed" in {
        val task = RouteDetailsTask(Completed)
        task.name mustBe "Edit route details"
      }

      "when status is InProgress" in {
        val task = RouteDetailsTask(InProgress)
        task.name mustBe "Edit route details"
      }

      "when status is Amended" in {
        val task = RouteDetailsTask(Amended)
        task.name mustBe "Edit route details"
      }
    }
  }

  "id" - {
    "must be route-details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = RouteDetailsTask(taskStatus)
          task.id mustBe "route-details"
      }
    }
  }

  "href" - {
    "must end with /route-details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = RouteDetailsTask(taskStatus)
          task.href(lrn)(frontendAppConfig) must endWith(s"/route-details/$lrn")
      }
    }
  }
}
