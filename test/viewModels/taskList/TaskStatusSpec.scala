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
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class TaskStatusSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "messageKey" - {
    "when Completed" in {
      TaskStatus.Completed.messageKey mustEqual "taskStatus.completed"
    }

    "when InProgress" in {
      TaskStatus.InProgress.messageKey mustEqual "taskStatus.inProgress"
    }

    "when NotStarted" in {
      TaskStatus.NotStarted.messageKey mustEqual "taskStatus.notStarted"
    }

    "when CannotStartYet" in {
      TaskStatus.CannotStartYet.messageKey mustEqual "taskStatus.cannotStartYet"
    }

    "when Error" in {
      TaskStatus.Error.messageKey mustEqual "taskStatus.error"
    }

    "when Unavailable" in {
      TaskStatus.Unavailable.messageKey mustEqual "taskStatus.completed"
    }

    "when CannotContinue" in {
      TaskStatus.CannotContinue.messageKey mustEqual "taskStatus.cannotContinue"
    }
  }

  "tag" - {
    "when Completed" in {
      TaskStatus.Completed.tag mustEqual "govuk-tag--green"
    }

    "when InProgress" in {
      TaskStatus.InProgress.tag mustEqual "govuk-tag--blue"
    }

    "when NotStarted" in {
      TaskStatus.NotStarted.tag mustEqual "govuk-tag--grey"
    }

    "when Unavailable" in {
      TaskStatus.Unavailable.tag mustEqual "govuk-tag--green"
    }

    "when CannotStartYet" in {
      TaskStatus.CannotStartYet.tag mustEqual "govuk-tag--red"
    }

    "when Error" in {
      TaskStatus.Error.tag mustEqual "govuk-tag--red"
    }

    "when CannotContinue" in {
      TaskStatus.CannotContinue.tag mustEqual "govuk-tag--yellow"
    }
  }

  "must serialise to json" - {
    "when completed" in {
      val result = Json.toJson[TaskStatus](TaskStatus.Completed)
      result mustEqual JsString("completed")
    }

    "when in progress" in {
      val result = Json.toJson[TaskStatus](TaskStatus.InProgress)
      result mustEqual JsString("in-progress")
    }

    "when not started" in {
      val result = Json.toJson[TaskStatus](TaskStatus.NotStarted)
      result mustEqual JsString("not-started")
    }

    "when cannot start yet" in {
      val result = Json.toJson[TaskStatus](TaskStatus.CannotStartYet)
      result mustEqual JsString("cannot-start-yet")
    }

    "when error" in {
      val result = Json.toJson[TaskStatus](TaskStatus.Error)
      result mustEqual JsString("error")
    }

    "when unavailable" in {
      val result = Json.toJson[TaskStatus](TaskStatus.Unavailable)
      result mustEqual JsString("unavailable")
    }

    "when cannot continue" in {
      val result = Json.toJson[TaskStatus](TaskStatus.CannotContinue)
      result mustEqual JsString("cannot-continue")
    }
  }

  "must deserialise from json" - {
    "when completed" in {
      val result = JsString("completed").as[TaskStatus]
      result mustEqual TaskStatus.Completed
    }

    "when in progress" in {
      val result = JsString("in-progress").as[TaskStatus]
      result mustEqual TaskStatus.InProgress
    }

    "when not started" in {
      val result = JsString("not-started").as[TaskStatus]
      result mustEqual TaskStatus.NotStarted
    }

    "when cannot start yet" in {
      val result = JsString("cannot-start-yet").as[TaskStatus]
      result mustEqual TaskStatus.CannotStartYet
    }

    "when cannot-continue" in {
      val result = JsString("cannot-continue").as[TaskStatus]
      result mustEqual TaskStatus.CannotContinue
    }

    "when error" in {
      val result = JsString("error").as[TaskStatus]
      result mustEqual TaskStatus.Error
    }

    "when unavailable" in {
      val result = JsString("unavailable").as[TaskStatus]
      result mustEqual TaskStatus.Unavailable
    }

    "when amended" in {
      val result = JsString("amended").as[TaskStatus]
      result mustEqual TaskStatus.Amended
    }

    "when something else" in {
      val result = JsString("foo").validate[TaskStatus]
      result mustBe a[JsError]
    }
  }
}
