/*
 * Copyright 2022 HM Revenue & Customs
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
import viewModels.taskList.TaskStatus.{CannotStartYet, Completed, InProgress, NotStarted}

abstract class Task {
  val status: TaskStatus
  val id: String
  val href: Option[String]
  val messageKey: String

  def name(implicit messages: Messages): String = messages {
    status match {
      case Completed | InProgress => s"task.$messageKey.edit"
      case NotStarted             => s"task.$messageKey.add"
      case CannotStartYet         => s"task.$messageKey"
    }
  }

  def isCompleted: Boolean = status == Completed
}
