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
import models.SubmissionState
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class TaskStatusSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "messageKey" - {
    "when Completed" - {
      "and declaration rejected pending changes" in {
        TaskStatus.Completed.messageKey(SubmissionState.RejectedPendingChanges) mustBe "taskStatus.completed"
      }

      "and declaration not rejected pending changes" in {
        forAll(arbitrary[SubmissionState.Value].retryUntil(_ != SubmissionState.RejectedPendingChanges)) {
          submissionState =>
            TaskStatus.Completed.messageKey(submissionState) mustBe "taskStatus.completed"
        }
      }
    }

    "when InProgress" in {
      forAll(arbitrary[SubmissionState.Value]) {
        submissionState =>
          TaskStatus.InProgress.messageKey(submissionState) mustBe "taskStatus.inProgress"
      }
    }

    "when Amended" in {
      forAll(arbitrary[SubmissionState.Value]) {
        amended =>
          TaskStatus.Amended.messageKey(amended) mustBe "taskStatus.amended"
      }
    }

    "when NotStarted" in {
      forAll(arbitrary[SubmissionState.Value]) {
        submissionState =>
          TaskStatus.NotStarted.messageKey(submissionState) mustBe "taskStatus.notStarted"
      }
    }

    "when CannotStartYet" in {
      forAll(arbitrary[SubmissionState.Value]) {
        submissionState =>
          TaskStatus.CannotStartYet.messageKey(submissionState) mustBe "taskStatus.cannotStartYet"
      }
    }

    "when Error" in {
      forAll(arbitrary[SubmissionState.Value]) {
        submissionState =>
          TaskStatus.Error.messageKey(submissionState) mustBe "taskStatus.error"
      }
    }

    "when Unavailable" in {
      forAll(arbitrary[SubmissionState.Value]) {
        submissionState =>
          TaskStatus.Unavailable.messageKey(submissionState) mustBe "taskStatus.completed"
      }
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

    "when Unavailable" in {
      TaskStatus.Unavailable.tag mustBe "govuk-tag--green"
    }

    "when CannotStartYet" in {
      TaskStatus.CannotStartYet.tag mustBe "govuk-tag--red"
    }

    "when Error" in {
      TaskStatus.Error.tag mustBe "govuk-tag--red"
    }
  }

  "must serialise to json" - {
    "when completed" in {
      val result = Json.toJson[TaskStatus](TaskStatus.Completed)
      result mustBe JsString("completed")
    }

    "when in progress" in {
      val result = Json.toJson[TaskStatus](TaskStatus.InProgress)
      result mustBe JsString("in-progress")
    }

    "when not started" in {
      val result = Json.toJson[TaskStatus](TaskStatus.NotStarted)
      result mustBe JsString("not-started")
    }

    "when cannot start yet" in {
      val result = Json.toJson[TaskStatus](TaskStatus.CannotStartYet)
      result mustBe JsString("cannot-start-yet")
    }
    "when error" in {
      val result = Json.toJson[TaskStatus](TaskStatus.Error)
      result mustBe JsString("error")
    }

    "when unavailable" in {
      val result = Json.toJson[TaskStatus](TaskStatus.Unavailable)
      result mustBe JsString("unavailable")
    }
  }

  "must deserialise from json" - {
    "when completed" in {
      val result = JsString("completed").as[TaskStatus]
      result mustBe TaskStatus.Completed
    }

    "when in progress" in {
      val result = JsString("in-progress").as[TaskStatus]
      result mustBe TaskStatus.InProgress
    }

    "when not started" in {
      val result = JsString("not-started").as[TaskStatus]
      result mustBe TaskStatus.NotStarted
    }

    "when cannot start yet" in {
      val result = JsString("cannot-start-yet").as[TaskStatus]
      result mustBe TaskStatus.CannotStartYet
    }

    "when error" in {
      val result = JsString("error").as[TaskStatus]
      result mustBe TaskStatus.Error
    }

    "when unavailable" in {
      val result = JsString("unavailable").as[TaskStatus]
      result mustBe TaskStatus.Unavailable
    }

    "when something else" in {
      val result = JsString("foo").validate[TaskStatus]
      result mustBe a[JsError]
    }
  }
}
