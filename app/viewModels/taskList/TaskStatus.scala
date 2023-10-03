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

import models.SubmissionState
import play.api.libs.json._

sealed trait TaskStatus {
  def messageKey(submissionState: SubmissionState.Value): String
  val tag: String
  val jsonString: String
  def isCompleted: Boolean   = this == TaskStatus.Completed
  def isUnavailable: Boolean = this == TaskStatus.Unavailable
  def isError: Boolean       = this == TaskStatus.Error
}

object TaskStatus {

  case object Completed extends TaskStatus {

    override def messageKey(submissionState: SubmissionState.Value): String = submissionState match {
      case SubmissionState.RejectedPendingChanges => "taskStatus.amended"
      case _                                      => "taskStatus.completed"
    }
    override val tag: String        = "govuk-tag--green"
    override val jsonString: String = "completed"
  }

  case object InProgress extends TaskStatus {
    override def messageKey(submissionState: SubmissionState.Value): String = "taskStatus.inProgress"
    override val tag: String                                                = "govuk-tag--blue"
    override val jsonString: String                                         = "in-progress"
  }

  case object NotStarted extends TaskStatus {
    override def messageKey(submissionState: SubmissionState.Value): String = "taskStatus.notStarted"
    override val tag: String                                                = "govuk-tag--grey"
    override val jsonString: String                                         = "not-started"
  }

  case object CannotStartYet extends TaskStatus {
    override def messageKey(submissionState: SubmissionState.Value): String = "taskStatus.cannotStartYet"
    override val tag: String                                                = "govuk-tag--red"
    override val jsonString: String                                         = "cannot-start-yet"
  }

  case object Unavailable extends TaskStatus {
    override def messageKey(submissionState: SubmissionState.Value): String = "taskStatus.completed"
    override val tag: String                                                = "govuk-tag--green"
    override val jsonString: String                                         = "unavailable"
  }

  case object Error extends TaskStatus {
    override def messageKey(submissionState: SubmissionState.Value): String = "taskStatus.error"
    override val tag: String                                                = "govuk-tag--red"
    override val jsonString: String                                         = "error"
  }

  implicit val reads: Reads[TaskStatus] = (json: JsValue) => {
    json.validate[String].flatMap {
      case Completed.jsonString      => JsSuccess(Completed)
      case InProgress.jsonString     => JsSuccess(InProgress)
      case NotStarted.jsonString     => JsSuccess(NotStarted)
      case CannotStartYet.jsonString => JsSuccess(CannotStartYet)
      case Error.jsonString          => JsSuccess(Error)
      case Unavailable.jsonString    => JsSuccess(Unavailable)
      case x                         => JsError(s"$x is not a valid task status")
    }
  }

  implicit val writes: Writes[TaskStatus] = Writes {
    x => JsString(x.jsonString)
  }
}
