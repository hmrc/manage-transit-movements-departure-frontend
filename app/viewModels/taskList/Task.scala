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

import config.FrontendAppConfig
import models.LocalReferenceNumber
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.tasklist._
import viewModels.taskList.TaskStatus._

trait Task {
  val section: String
  val status: TaskStatus
}

abstract class TaskListTask extends Task {
  val id: String

  def href(lrn: LocalReferenceNumber)(implicit config: FrontendAppConfig): String

  val messageKey: String

  def name(implicit messages: Messages): String = messages {
    status match {
      case Completed | InProgress | Amended              => s"task.$messageKey.edit"
      case Error                                         => s"task.$messageKey.amend"
      case NotStarted                                    => s"task.$messageKey.add"
      case CannotStartYet | CannotContinue | Unavailable => s"task.$messageKey"
    }
  }

  def toTaskListItem(
    lrn: LocalReferenceNumber
  )(implicit messages: Messages, config: FrontendAppConfig): TaskListItem =
    TaskListItem(
      title = TaskListItemTitle(name.toText),
      status = status.toTaskListItemStatus(id),
      href = status match {
        case CannotStartYet | CannotContinue | Unavailable => None
        case _                                             => Some(href(lrn))
      }
    )
}

object Task {

  def apply(section: String, status: TaskStatus): Option[Task] = section match {
    case PreTaskListTask.section      => Some(PreTaskListTask(status))
    case TraderDetailsTask.section    => Some(TraderDetailsTask(status))
    case RouteDetailsTask.section     => Some(RouteDetailsTask(status))
    case TransportTask.section        => Some(TransportTask(status))
    case DocumentsTask.section        => Some(DocumentsTask(status))
    case ItemsTask.section            => Some(ItemsTask(status))
    case GuaranteeDetailsTask.section => Some(GuaranteeDetailsTask(status))
    case _                            => None
  }
}
