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

class DocumentsTaskSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  "name" - {
    "must be Documents" - {
      "when status is CannotStartYet" in {
        val task = DocumentsTask(CannotStartYet)
        task.name mustEqual "Documents"
      }
    }

    "must be Add documents" - {
      "when status is NotStarted" in {
        val task = DocumentsTask(NotStarted)
        task.name mustEqual "Add documents"
      }
    }

    "must be Edit documents" - {
      "when status is Completed" in {
        val task = DocumentsTask(Completed)
        task.name mustEqual "Edit documents"
      }

      "when status is InProgress" in {
        val task = DocumentsTask(InProgress)
        task.name mustEqual "Edit documents"
      }

      "when status is Amended" in {
        val task = DocumentsTask(Amended)
        task.name mustEqual "Edit documents"
      }
    }

    "must be Amend documents" - {
      "when status is Error" in {
        val task = DocumentsTask(Error)
        task.name mustEqual "Amend documents"
      }
    }
  }

  "id" - {
    "must be documents" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = DocumentsTask(taskStatus)
          task.id mustEqual "documents"
      }
    }
  }

  "href" - {
    "must end with /documents/:lrn" in {
      forAll(arbitrary[TaskStatus]) {
        taskStatus =>
          val task = DocumentsTask(taskStatus)
          task.href(lrn)(frontendAppConfig) must endWith(s"/documents/$lrn")
      }
    }
  }
}
