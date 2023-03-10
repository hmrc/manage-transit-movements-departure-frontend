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

class TaskSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "apply" - {
    "when trader details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val result = Task.apply(".traderDetails", taskStatus)
          result.get mustBe TraderDetailsTask(taskStatus)
      }
    }

    "when route details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val result = Task.apply(".routeDetails", taskStatus)
          result.get mustBe RouteDetailsTask(taskStatus)
      }
    }

    "when transport details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val result = Task.apply(".transportDetails", taskStatus)
          result.get mustBe TransportTask(taskStatus)
      }
    }

    "when documents" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val result = Task.apply(".documents", taskStatus)
          result.get mustBe DocumentsTask(taskStatus)
      }
    }

    "when items" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val result = Task.apply(".items", taskStatus)
          result.get mustBe ItemsTask(taskStatus)
      }
    }

    "when guarantee details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val result = Task.apply(".guaranteeDetails", taskStatus)
          result.get mustBe GuaranteeDetailsTask(taskStatus)
      }
    }

    "when something else" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val result = Task.apply(".foo", taskStatus)
          result must not be defined
      }
    }
  }

}
