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
import config.FrontendAppConfig
import generators.Generators
import models.LocalReferenceNumber
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class TaskListViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "apply" - {
    "when pre task list is completed" - {
      "must create tasks" in {
        val answers = emptyUserAnswers.copy(tasks = Map(PreTaskListTask.section -> TaskStatus.Completed))

        val tasks = new TaskListViewModel().apply(answers)

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
            val result  = new TaskListViewModel().apply(answers)
            result.foreach(_.status mustBe TaskStatus.CannotStartYet)
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
        val result  = new TaskListViewModel().apply(answers)

        result(2).name mustBe "Add transport details"
        result(2).status mustBe TaskStatus.NotStarted
        result(2).href(answers.lrn)(frontendAppConfig) must endWith(s"/transport-details/$lrn")
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
        val result  = new TaskListViewModel().apply(answers)

        result(4).name mustBe "Add items"
        result(4).status mustBe TaskStatus.NotStarted
        result(4).href(answers.lrn)(frontendAppConfig) must endWith(s"/items/$lrn")
      }
    }
  }

  "showSubmissionButton" - {

    val guaranteeCompleteTask = new TaskListTask {
      override val status: TaskStatus = TaskStatus.Completed
      override val messageKey: String = ""
      override val id: String         = ""

      override def href(lrn: LocalReferenceNumber)(implicit config: FrontendAppConfig): String = ""

      override val section: String = GuaranteeDetailsTask.section
    }

    val preTaskComplete = new TaskListTask {
      override val status: TaskStatus = TaskStatus.Completed
      override val messageKey: String = ""
      override val id: String         = ""

      override def href(lrn: LocalReferenceNumber)(implicit config: FrontendAppConfig): String = ""

      override val section: String = PreTaskListTask.section
    }

    val preTaskNotStarted = new TaskListTask {
      override val status: TaskStatus = TaskStatus.NotStarted
      override val messageKey: String = ""
      override val id: String         = ""

      override def href(lrn: LocalReferenceNumber)(implicit config: FrontendAppConfig): String = ""

      override val section: String = PreTaskListTask.section
    }

    val preTaskUnavailable = new TaskListTask {
      override val status: TaskStatus = TaskStatus.Unavailable
      override val messageKey: String = ""
      override val id: String         = ""

      override def href(lrn: LocalReferenceNumber)(implicit config: FrontendAppConfig): String = ""

      override val section: String = PreTaskListTask.section
    }

    val traderDetailsUnavailable = new TaskListTask {
      override val status: TaskStatus = TaskStatus.Unavailable
      override val messageKey: String = ""
      override val id: String         = ""

      override def href(lrn: LocalReferenceNumber)(implicit config: FrontendAppConfig): String = ""

      override val section: String = TraderDetailsTask.section
    }

    "must be true is guarantee is complete and other sections are unavailable" in {

      val tasksLists = Seq(guaranteeCompleteTask, preTaskUnavailable, traderDetailsUnavailable)

      TaskListViewModel.showSubmissionButton(tasksLists) mustBe true
    }

    "must be true everything is complete" in {

      val tasksLists = Seq(preTaskComplete, guaranteeCompleteTask)

      TaskListViewModel.showSubmissionButton(tasksLists) mustBe true
    }

    "must be false if everything is not complete" in {

      val tasksLists = Seq(preTaskNotStarted, guaranteeCompleteTask)

      TaskListViewModel.showSubmissionButton(tasksLists) mustBe false
    }
  }
}
