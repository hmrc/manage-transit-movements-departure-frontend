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
import models.SubmissionState
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.taskList.TaskListViewModel.TaskListViewModelProvider

class TaskListViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "apply" - {
    "when pre task list is completed" - {
      "must create tasks" in {
        val answers = emptyUserAnswers.copy(tasks = Map(PreTaskListTask.section -> TaskStatus.Completed))

        val viewModel = new TaskListViewModelProvider().apply(answers)
        val tasks     = viewModel.tasks

        tasks.size mustBe 6

        tasks.head.name mustBe "Add trader details"
        tasks.head.status mustBe TaskStatus.NotStarted
        tasks.head.href(answers.lrn)(frontendAppConfig) must endWith(s"/trader-details/$lrn")

        tasks(1).name mustBe "Add route details"
        tasks(1).status mustBe TaskStatus.NotStarted
        tasks(1).href(answers.lrn)(frontendAppConfig) must endWith(s"/route-details/$lrn")

        tasks(2).name mustBe "Transport details"
        tasks(2).status mustBe TaskStatus.CannotStartYet
        tasks(2).href(answers.lrn)(frontendAppConfig) must endWith(s"/transport-details/$lrn")

        tasks(3).name mustBe "Add documents"
        tasks(3).status mustBe TaskStatus.NotStarted
        tasks(3).href(answers.lrn)(frontendAppConfig) must endWith(s"/documents/$lrn")

        tasks(4).name mustBe "Items"
        tasks(4).status mustBe TaskStatus.CannotStartYet
        tasks(4).href(answers.lrn)(frontendAppConfig) must endWith(s"/items/$lrn")

        tasks(5).name mustBe "Add guarantee details"
        tasks(5).status mustBe TaskStatus.NotStarted
        tasks(5).href(answers.lrn)(frontendAppConfig) must endWith(s"/guarantee-details/$lrn")
      }
    }

    "when pre task list not completed" - {
      "everything must be 'cannot start yet'" in {
        forAll(arbitrary[TaskStatus](arbitraryIncompleteTaskStatus)) {
          taskStatus =>
            val tasks   = Map(PreTaskListTask.section -> taskStatus)
            val answers = emptyUserAnswers.copy(tasks = tasks)
            val result  = new TaskListViewModelProvider().apply(answers)
            result.tasks.foreach(_.status mustBe TaskStatus.CannotStartYet)
        }
      }
    }

    "when pre task list, trader details and route details completed" - {
      "transport details must be 'not started'" in {
        val tasks = Map(
          PreTaskListTask.section   -> TaskStatus.Completed,
          TraderDetailsTask.section -> TaskStatus.Completed,
          RouteDetailsTask.section  -> TaskStatus.Completed
        )
        val answers = emptyUserAnswers.copy(tasks = tasks)
        val result  = new TaskListViewModelProvider().apply(answers)

        val transportTask = result.tasks(2)
        transportTask.name mustBe "Add transport details"
        transportTask.status mustBe TaskStatus.NotStarted
        transportTask.href(answers.lrn)(frontendAppConfig) must endWith(s"/transport-details/$lrn")
      }
    }

    "when pre task list, trader details, route details, transport details and documents completed" - {
      "items must be 'not started'" in {
        val tasks = Map(
          PreTaskListTask.section   -> TaskStatus.Completed,
          TraderDetailsTask.section -> TaskStatus.Completed,
          RouteDetailsTask.section  -> TaskStatus.Completed,
          TransportTask.section     -> TaskStatus.Completed,
          DocumentsTask.section     -> TaskStatus.Completed
        )
        val answers = emptyUserAnswers.copy(tasks = tasks)
        val result  = new TaskListViewModelProvider().apply(answers)

        val itemsTask = result.tasks(4: Int)

        itemsTask.name mustBe "Add items"
        itemsTask.status mustBe TaskStatus.NotStarted
        itemsTask.href(answers.lrn)(frontendAppConfig) must endWith(s"/items/$lrn")
      }
    }

    "when pre task list, trader details, route details and documents completed, transport details and items in progress" - {
      "items must be 'not started'" in {
        val tasks = Map(
          PreTaskListTask.section   -> TaskStatus.Completed,
          TraderDetailsTask.section -> TaskStatus.Completed,
          RouteDetailsTask.section  -> TaskStatus.Completed,
          TransportTask.section     -> TaskStatus.InProgress,
          DocumentsTask.section     -> TaskStatus.Completed,
          ItemsTask.section         -> TaskStatus.InProgress
        )
        val answers = emptyUserAnswers.copy(tasks = tasks)
        val result  = new TaskListViewModelProvider().apply(answers)

        val itemsTask = result.tasks(4: Int)

        itemsTask.name mustBe "Items"
        itemsTask.status mustBe TaskStatus.CannotContinue
        itemsTask.href(answers.lrn)(frontendAppConfig) must endWith(s"/items/$lrn")
      }
    }

    "when all tasks are in error state" - {
      "items must be 'not started'" in {
        val tasks = Map(
          PreTaskListTask.section      -> TaskStatus.Completed,
          TraderDetailsTask.section    -> TaskStatus.Error,
          RouteDetailsTask.section     -> TaskStatus.Error,
          TransportTask.section        -> TaskStatus.Error,
          DocumentsTask.section        -> TaskStatus.Error,
          ItemsTask.section            -> TaskStatus.Error,
          GuaranteeDetailsTask.section -> TaskStatus.Error
        )
        val answers = emptyUserAnswers.copy(tasks = tasks)
        val result  = new TaskListViewModelProvider().apply(answers)

        result.tasks.head.name mustBe "Amend trader details"
        result.tasks.head.status mustBe TaskStatus.Error

        result.tasks(1).name mustBe "Amend route details"
        result.tasks(1).status mustBe TaskStatus.Error

        result.tasks(2).name mustBe "Amend transport details"
        result.tasks(2).status mustBe TaskStatus.Error

        result.tasks(3).name mustBe "Amend documents"
        result.tasks(3).status mustBe TaskStatus.Error

        result.tasks(4: Int).name mustBe "Amend items"
        result.tasks(4: Int).status mustBe TaskStatus.Error

        result.tasks(5: Int).name mustBe "Amend guarantee details"
        result.tasks(5: Int).status mustBe TaskStatus.Error
      }
    }
  }

  "showSubmissionButton" - {

    "must be true if guarantee is complete and other sections are unavailable" in {
      val tasks = Map(
        PreTaskListTask.section      -> TaskStatus.Unavailable,
        TraderDetailsTask.section    -> TaskStatus.Unavailable,
        RouteDetailsTask.section     -> TaskStatus.Unavailable,
        TransportTask.section        -> TaskStatus.Unavailable,
        DocumentsTask.section        -> TaskStatus.Unavailable,
        ItemsTask.section            -> TaskStatus.Unavailable,
        GuaranteeDetailsTask.section -> TaskStatus.Completed
      )
      val answers = emptyUserAnswers.copy(tasks = tasks)
      val result  = new TaskListViewModelProvider().apply(answers)

      result.showSubmissionButton mustBe true
    }

    "must be true if everything is complete" in {
      val tasks = Map(
        PreTaskListTask.section      -> TaskStatus.Completed,
        TraderDetailsTask.section    -> TaskStatus.Completed,
        RouteDetailsTask.section     -> TaskStatus.Completed,
        TransportTask.section        -> TaskStatus.Completed,
        DocumentsTask.section        -> TaskStatus.Completed,
        ItemsTask.section            -> TaskStatus.Completed,
        GuaranteeDetailsTask.section -> TaskStatus.Completed
      )
      val answers = emptyUserAnswers.copy(tasks = tasks)
      val result  = new TaskListViewModelProvider().apply(answers)

      result.showSubmissionButton mustBe true
    }

    "must be true if everything is amended" in {
      val tasks = Map(
        PreTaskListTask.section      -> TaskStatus.Amended,
        TraderDetailsTask.section    -> TaskStatus.Amended,
        RouteDetailsTask.section     -> TaskStatus.Amended,
        TransportTask.section        -> TaskStatus.Amended,
        DocumentsTask.section        -> TaskStatus.Amended,
        ItemsTask.section            -> TaskStatus.Amended,
        GuaranteeDetailsTask.section -> TaskStatus.Amended
      )
      val answers = emptyUserAnswers.copy(tasks = tasks)
      val result  = new TaskListViewModelProvider().apply(answers)

      result.showSubmissionButton mustBe true
    }

    "must be false if everything is not complete" in {
      val tasks = Map(
        PreTaskListTask.section      -> TaskStatus.Completed,
        TraderDetailsTask.section    -> TaskStatus.NotStarted,
        RouteDetailsTask.section     -> TaskStatus.NotStarted,
        TransportTask.section        -> TaskStatus.NotStarted,
        DocumentsTask.section        -> TaskStatus.NotStarted,
        GuaranteeDetailsTask.section -> TaskStatus.Completed
      )
      val answers = emptyUserAnswers.copy(tasks = tasks)
      val result  = new TaskListViewModelProvider().apply(answers)

      result.showSubmissionButton mustBe false
    }
  }

  "showSubmissionButton on amendment journey" - {
    "must be false if all complete but on amendment" in {
      val tasks = Map(
        PreTaskListTask.section      -> TaskStatus.Completed,
        TraderDetailsTask.section    -> TaskStatus.Completed,
        RouteDetailsTask.section     -> TaskStatus.Completed,
        TransportTask.section        -> TaskStatus.Completed,
        DocumentsTask.section        -> TaskStatus.Completed,
        ItemsTask.section            -> TaskStatus.Completed,
        GuaranteeDetailsTask.section -> TaskStatus.Completed
      )
      val answers = emptyUserAnswers.copy(tasks = tasks, status = SubmissionState.Amendment)
      val result  = new TaskListViewModelProvider().apply(answers)

      result.showSubmissionButton mustBe false
    }

    "must be true if at least one amended" in {
      val tasks = Map(
        PreTaskListTask.section      -> TaskStatus.Completed,
        TraderDetailsTask.section    -> TaskStatus.Completed,
        RouteDetailsTask.section     -> TaskStatus.Completed,
        TransportTask.section        -> TaskStatus.Completed,
        DocumentsTask.section        -> TaskStatus.Completed,
        ItemsTask.section            -> TaskStatus.Amended,
        GuaranteeDetailsTask.section -> TaskStatus.Completed
      )
      val answers = emptyUserAnswers.copy(tasks = tasks, status = SubmissionState.Amendment)
      val result  = new TaskListViewModelProvider().apply(answers)

      result.showSubmissionButton mustBe true
    }
  }
}
