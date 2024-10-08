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

import play.api.i18n.Messages
import play.api.libs.json._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.tag.Tag
import uk.gov.hmrc.govukfrontend.views.viewmodels.tasklist.TaskListItemStatus

sealed trait TaskStatus {
  def messageKey: String

  val tag: String

  val jsonString: String

  def isCompleted: Boolean =
    this == TaskStatus.Completed ||
      this == TaskStatus.Amended ||
      this == TaskStatus.Unavailable

  def toTaskListItemStatus(id: String)(implicit messages: Messages): TaskListItemStatus =
    TaskListItemStatus(
      tag = Some(
        Tag(
          content = messages(messageKey).toText,
          classes = tag,
          attributes = Map("id" -> s"$id-status")
        )
      )
    )
}

object TaskStatus {

  case object Completed extends TaskStatus {
    override def messageKey: String = "taskStatus.completed"
    override val tag: String        = "govuk-tag--green"
    override val jsonString: String = "completed"
  }

  case object InProgress extends TaskStatus {
    override def messageKey: String = "taskStatus.inProgress"
    override val tag: String        = "govuk-tag--blue"
    override val jsonString: String = "in-progress"
  }

  case object NotStarted extends TaskStatus {
    override def messageKey: String = "taskStatus.notStarted"
    override val tag: String        = "govuk-tag--grey"
    override val jsonString: String = "not-started"
  }

  case object Amended extends TaskStatus {
    override def messageKey: String = "taskStatus.amended"
    override val tag: String        = "govuk-tag--green"
    override val jsonString: String = "amended"
  }

  case object CannotStartYet extends TaskStatus {
    override def messageKey: String = "taskStatus.cannotStartYet"
    override val tag: String        = "govuk-tag--red"
    override val jsonString: String = "cannot-start-yet"
  }

  case object CannotContinue extends TaskStatus {
    override def messageKey: String = "taskStatus.cannotContinue"
    override val tag: String        = "govuk-tag--yellow"
    override val jsonString: String = "cannot-continue"
  }

  case object Unavailable extends TaskStatus {
    override def messageKey: String = "taskStatus.completed"
    override val tag: String        = "govuk-tag--green"
    override val jsonString: String = "unavailable"
  }

  case object Error extends TaskStatus {
    override def messageKey: String = "taskStatus.error"
    override val tag: String        = "govuk-tag--red"
    override val jsonString: String = "error"
  }

  implicit val reads: Reads[TaskStatus] = (json: JsValue) =>
    json.validate[String].flatMap {
      case Completed.jsonString      => JsSuccess(Completed)
      case InProgress.jsonString     => JsSuccess(InProgress)
      case NotStarted.jsonString     => JsSuccess(NotStarted)
      case CannotStartYet.jsonString => JsSuccess(CannotStartYet)
      case CannotContinue.jsonString => JsSuccess(CannotContinue)
      case Error.jsonString          => JsSuccess(Error)
      case Unavailable.jsonString    => JsSuccess(Unavailable)
      case Amended.jsonString        => JsSuccess(Amended)
      case x                         => JsError(s"$x is not a valid task status")
    }

  implicit val writes: Writes[TaskStatus] = Writes {
    x => JsString(x.jsonString)
  }
}
