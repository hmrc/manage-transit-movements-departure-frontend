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

class TaskStatusSpec extends SpecBase {

  "messageKey" - {
    "when Completed" in {
      TaskStatus.Completed.messageKey mustBe "taskStatus.completed"
    }

    "when InProgress" in {
      TaskStatus.InProgress.messageKey mustBe "taskStatus.inProgress"
    }

    "when NotStarted" in {
      TaskStatus.NotStarted.messageKey mustBe "taskStatus.notStarted"
    }

    "when CannotStartYet" in {
      TaskStatus.CannotStartYet.messageKey mustBe "taskStatus.cannotStartYet"
    }
  }

  "tag" - {
    "when Completed" in {
      TaskStatus.Completed.tag mustBe "govuk-tag--green"
    }

    "when InProgress" in {
      TaskStatus.InProgress.tag mustBe "govuk-tag--blue"
    }

    "when NotStarted" in {
      TaskStatus.NotStarted.tag mustBe "govuk-tag--grey"
    }

    "when CannotStartYet" in {
      TaskStatus.CannotStartYet.tag mustBe "govuk-tag--red"
    }
  }
}
