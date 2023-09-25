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

import models.{SubmissionState, UserAnswers}

case class TaskListViewModel(tasks: Seq[TaskListTask], submissionState: SubmissionState.Value) {

  def showErrorContent: Boolean = submissionState.showErrorContent

  def showSubmissionButton: Boolean = tasks.forall(_.isCompleted)
}

object TaskListViewModel {

  class TaskListViewModelProvider() {

    def apply(userAnswers: UserAnswers): TaskListViewModel = {

      def task(section: String, dependentSections: Seq[String] = Nil): Option[Task] = {
        val tasks = userAnswers.tasks
        val status = tasks.getOrElse(
          section,
          if ((PreTaskListTask.section +: dependentSections).allCompleted(tasks)) TaskStatus.NotStarted else TaskStatus.CannotStartYet
        )
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
