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

import controllers.traderDetails.routes
import models.DeclarationType.Option4
import models.{NormalMode, UserAnswers}
import pages.preTaskList.DeclarationTypePage
import play.api.i18n.Messages
import viewModels.taskList.TaskStatus._

case class TraderDetailsTask(status: TaskStatus, href: Option[String]) extends Task {

  override def name(implicit messages: Messages): String = messages {
    status match {
      case Completed | InProgress => "task.traderDetails.edit"
      case NotStarted             => "task.traderDetails.add"
      case CannotStartYet         => "task.traderDetails"
    }
  }

  override val id: String = "trader-details"
}

object TraderDetailsTask {

  def apply(userAnswers: UserAnswers): TraderDetailsTask = {
    val status: TaskStatus = NotStarted // TODO - use userAnswers to determine status
    new TraderDetailsTask(status, href(userAnswers, status))
  }

  def href(userAnswers: UserAnswers, status: TaskStatus): Option[String] = status match {
    case TaskStatus.CannotStartYet =>
      None
    case NotStarted | InProgress =>
      userAnswers.get(DeclarationTypePage) match {
        case Some(Option4) => ??? // TODO - redirect to Transit Procedure TIR identification number
        case Some(_)       => Some(routes.TransitHolderEoriYesNoController.onPageLoad(userAnswers.lrn, NormalMode).url)
        case None          => Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      }
    case Completed =>
      ??? // TODO - redirect to trader details check your answers
  }
}
