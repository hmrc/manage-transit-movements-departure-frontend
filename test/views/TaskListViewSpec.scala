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

package views

import models.SubmissionState
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.twirl.api.HtmlFormat
import viewModels.taskList.{TaskListTask, TaskListViewModel}
import views.behaviours.TaskListViewBehaviours
import views.html.TaskListView

class TaskListViewSpec extends TaskListViewBehaviours {

  private val expiryInDays = Gen.choose(0: Int, 30: Int).sample.value

  override def view: HtmlFormat.Appendable = applyView(tasks, submissionState, expiryInDays)

  private def applyView(
    tasks: Seq[TaskListTask],
    submissionState: SubmissionState.Value,
    expiryInDays: Long
  ): HtmlFormat.Appendable = {
    val viewModel = TaskListViewModel(tasks, submissionState)
    injector.instanceOf[TaskListView].apply(lrn, viewModel, expiryInDays)(fakeRequest, messages)
  }

  override val prefix: String = "taskList"

  behave like pageWithTitle()

  behave like pageWithoutBackLink()

  behave like pageWithCaption(s"LRN: $lrn")

  behave like pageWithHeading()

  behave like pageWithContent("p", "You must complete each section before you can send your declaration.")

  behave like pageWithTaskList(lrn)

  behave like pageWithLink(
    "transit-movements",
    "Back to transit movements",
    frontendAppConfig.serviceUrl
  )

  behave like pageWithContent(
    "p",
    s"You can save your declaration and come back later. You have $expiryInDays days left to complete it before your answers are deleted."
  )

  "when there are errors" - {

    "when all tasks completed" - {
      val tasks = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryCompletedTask)).sample.value

      val view = applyView(tasks, SubmissionState.RejectedPendingChanges, expiryInDays)
      val doc  = parseView(view)

      behave like pageWithContent(doc, "p", "There is a problem with this declaration. Amend the errors in the relevant sections and resend the declaration.")
      behave like pageWithoutContent(doc, "p", "You must complete each section before you can send your declaration.")

      behave like pageWithSubmitButton(doc, "Confirm and resend")
    }

    "when not all tasks completed" - {
      val tasks = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryErrorTask)).sample.value

      val view = applyView(tasks, SubmissionState.RejectedPendingChanges, expiryInDays)
      val doc  = parseView(view)

      behave like pageWithContent(doc, "p", "There is a problem with this declaration. Amend the errors in the relevant sections and resend the declaration.")
      behave like pageWithoutContent(doc, "p", "You must complete each section before you can send your declaration.")

      behave like pageWithoutSubmitButton(doc)

      tasks.foreach {
        task =>
          val hiddenText = getElementById(doc, s"${task.id}-hidden")
          assertElementContainsText(hiddenText, "to amend the error")
      }
    }
  }

  "when there are not errors" - {

    "when all tasks completed" - {
      val tasks = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryCompletedTask)).sample.value

      val view = applyView(tasks, SubmissionState.NotSubmitted, expiryInDays)
      val doc  = parseView(view)

      behave like pageWithContent(doc, "h2", "Send your departure declaration")

      behave like pageWithContent(doc, "p", "By sending this, you are confirming that these details are correct to the best of your knowledge.")

      behave like pageWithSubmitButton(doc, "Confirm and send")
    }

    "when not all tasks completed" - {
      val tasks = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryIncompleteTask)).sample.value

      val view = applyView(tasks, SubmissionState.NotSubmitted, expiryInDays)
      val doc  = parseView(view)

      behave like pageWithoutContent(doc, "h2", "Send your departure declaration")

      behave like pageWithoutContent(doc, "p", "By sending this, you are confirming that these details are correct to the best of your knowledge.")

      behave like pageWithoutSubmitButton(doc)
    }
  }
}
