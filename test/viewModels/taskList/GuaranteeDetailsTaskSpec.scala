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

import base.SpecBase
import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.taskList.TaskStatus._

class GuaranteeDetailsTaskSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "name" - {
    "must be Guarantee Details" - {
      "when status is CannotStartYet" in {
        val task = GuaranteeDetailsTask(CannotStartYet)
        task.name mustEqual "Guarantee details"
      }
    }

    "must be Add guarantee details" - {
      "when status is NotStarted" in {
        val task = GuaranteeDetailsTask(NotStarted)
        task.name mustEqual "Add guarantee details"
      }
    }

    "must be Edit guarantee details" - {
      "when status is Completed" in {
        val task = GuaranteeDetailsTask(Completed)
        task.name mustEqual "Edit guarantee details"
      }

      "when status is InProgress" in {
        val task = GuaranteeDetailsTask(InProgress)
        task.name mustEqual "Edit guarantee details"
      }

      "when status is Amended" in {
        val task = GuaranteeDetailsTask(Amended)
        task.name mustEqual "Edit guarantee details"
      }
    }

    "must be Amend guarantee details" - {
      "when status is Error" in {
        val task = GuaranteeDetailsTask(Error)
        task.name mustEqual "Amend guarantee details"
      }
    }
  }

  "id" - {
    "must be guarantee-details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = GuaranteeDetailsTask(taskStatus)
          task.id mustEqual "guarantee-details"
      }
    }
  }

  "href" - {
    "must end with /guarantee-details" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = GuaranteeDetailsTask(taskStatus)
          task.href(lrn)(frontendAppConfig) must endWith(s"/guarantee-details/$lrn")
      }
    }
  }
}
