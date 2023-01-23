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

import play.api.i18n.Messages
import play.api.libs.json._
import viewModels.taskList.TaskStatus._

abstract class Task {
  val status: TaskStatus
  val id: String
  val href: Option[String]
  val messageKey: String
  val section: String

  def name(implicit messages: Messages): String = messages {
    status match {
      case Completed | InProgress => s"task.$messageKey.edit"
      case NotStarted             => s"task.$messageKey.add"
      case CannotStartYet         => s"task.$messageKey"
    }
  }

  def isCompleted: Boolean = status == Completed
}

object Task {
  import play.api.libs.functional.syntax._

  def apply(section: String, status: TaskStatus, href: Option[String]): Option[Task] = section match {
    case TraderDetailsTask.section    => Some(TraderDetailsTask(status, href))
    case RouteDetailsTask.section     => Some(RouteDetailsTask(status, href))
    case TransportTask.section        => Some(TransportTask(status, href))
    case GuaranteeDetailsTask.section => Some(GuaranteeDetailsTask(status, href))
    case _                            => None
  }

  implicit val reads: Reads[Task] = (json: JsValue) => {
    type Tuple = (String, TaskStatus, Option[String])
    implicit val tupleReads: Reads[Tuple] = (
      (__ \ "section").read[String] and
        (__ \ "status").read[TaskStatus] and
        (__ \ "href").readNullable[String]
    ).tupled

    json.validate[Tuple].flatMap {
      case (section, status, href) =>
        Task(section, status, href) match {
          case Some(value) => JsSuccess(value)
          case None        => JsError(s"$section is not a valid task")
        }
    }
  }

  implicit val writes: Writes[Task] = (
    (__ \ "section").write[String] and
      (__ \ "status").write[TaskStatus] and
      (__ \ "href").writeNullable[String]
  ).apply {
    task => (task.section, task.status, task.href)
  }
}
