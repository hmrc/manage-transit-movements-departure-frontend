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

import config.FrontendAppConfig
import models.SubmissionState.{Amendment, GuaranteeAmendment}
import models.{LocalReferenceNumber, SubmissionState, UserAnswers}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.tasklist.TaskListItem
import viewModels.taskList.TaskStatus._

case class TaskListViewModel(tasks: Seq[TaskListTask], submissionState: SubmissionState.Value) {

  def showErrorContent: Boolean = submissionState.showErrorContent

  def showSubmissionButton: Boolean = {
    def allCompletedOrAmended: Boolean = tasks.forall(_.status.isCompleted)
    submissionState match {
      case Amendment | GuaranteeAmendment => allCompletedOrAmended && tasks.exists(_.status == Amended)
      case _                              => allCompletedOrAmended
    }
  }

  def taskListItems(lrn: LocalReferenceNumber)(implicit messages: Messages, config: FrontendAppConfig): Seq[TaskListItem] =
    tasks.map(_.toTaskListItem(lrn))
}

object TaskListViewModel {

  class TaskListViewModelProvider() {

    def apply(userAnswers: UserAnswers): TaskListViewModel = {

      def task(section: String, dependentSections: Seq[String] = Nil): Option[Task] = {
        val tasks = userAnswers.tasks

        val status = if ((PreTaskListTask.section +: dependentSections).allCompleted(tasks)) {
          tasks.getOrElse(section, NotStarted)
        } else {
          tasks
            .get(section)
            .fold[TaskStatus](CannotStartYet) {
              _ => CannotContinue
            }
        }

        Task(section, status)
      }

      val tasks = Seq(
        task(TraderDetailsTask.section),
        task(RouteDetailsTask.section),
        task(TransportTask.section, Seq(TraderDetailsTask.section, RouteDetailsTask.section)),
        task(DocumentsTask.section),
        task(ItemsTask.section, Seq(TraderDetailsTask.section, RouteDetailsTask.section, TransportTask.section, DocumentsTask.section)),
        task(GuaranteeDetailsTask.section)
      ).flatten.collect {
        case task: TaskListTask => task
      }

      new TaskListViewModel(tasks, userAnswers.status)
    }
  }

}
