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

import org.scalacheck.Arbitrary.arbitrary
import play.twirl.api.HtmlFormat
import viewModels.taskList.TaskListTask
import views.behaviours.TaskListViewBehaviours
import views.html.TaskListView

class TaskListViewSpec extends TaskListViewBehaviours {

  override def view: HtmlFormat.Appendable = applyView(tasks)
  private val expiryInDays: Option[Int]    = Some(30)

  private def applyView(tasks: Seq[TaskListTask]): HtmlFormat.Appendable =
    injector.instanceOf[TaskListView].apply(lrn, tasks, showErrorContent = false, expiryInDays)(fakeRequest, messages)

  override val prefix: String = "taskList"

  behave like pageWithTitle()

  behave like pageWithoutBackLink()

  behave like pageWithCaption("LRN:" + lrn.toString)

  behave like pageWithHeading()

  behave like pageWithContent("h2", "Departure details")
  behave like pageWithContent("p", "You must complete each section before you can send your declaration.")

  if (expiryInDays.isDefined) {
    behave like pageWithContent("p", "You can save your declaration and come back later. You have 30 days left to complete it before your answers are deleted.")
  } else {
    behave like pageWithContent("p", "You can save your declaration and come back later.")
  }

  behave like pageWithTaskList(lrn)

  behave like pageWithLink(
    "transit-movements",
    "Back to transit movements",
    frontendAppConfig.serviceUrl
  )

  "when there are errors" - {
    "when all tasks completed" - {
      val tasks = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryCompletedTask)).sample.value

      def applyViewWithErrors(tasks: Seq[TaskListTask]): HtmlFormat.Appendable =
        injector.instanceOf[TaskListView].apply(lrn, tasks, showErrorContent = true, None)(fakeRequest, messages)

      val doc = parseView(applyViewWithErrors(tasks))

      behave like pageWithContent(doc, "p", "There is a problem with this declaration.")
      behave like pageWithContent(doc, "p", "Amend the errors in the relevant sections and resend the declaration.")
      behave like pageWithContent(doc, "p", "You must complete each section before you can send your declaration.")

      behave like pageWithSubmitButton(doc, "Confirm and resend")
    }

    "when not all tasks completed" - {
      val tasks = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryErrorTask)).sample.value

      def applyViewWithErrors(tasks: Seq[TaskListTask]): HtmlFormat.Appendable =
        injector.instanceOf[TaskListView].apply(lrn, tasks, showErrorContent = true, None)(fakeRequest, messages)

      val doc = parseView(applyViewWithErrors(tasks))

      behave like pageWithContent(doc, "p", "There is a problem with this declaration.")
      behave like pageWithContent(doc, "p", "Amend the errors in the relevant sections and resend the declaration.")
      behave like pageWithContent(doc, "p", "You must complete each section before you can send your declaration.")

      behave like pageWithoutSubmitButton(doc)
    }
  }

  "when there are not errors" - {
    "when all tasks completed" - {
      val tasks = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryCompletedTask)).sample.value
      val doc   = parseView(applyView(tasks))

      behave like pageWithContent(doc, "h2", "Send your departure declaration")

      behave like pageWithContent(doc, "p", "By sending this, you are confirming that these details are correct to the best of your knowledge.")

      behave like pageWithSubmitButton(doc, "Confirm and send")
    }

    "when not all tasks completed" - {
      val tasks = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryIncompleteTask)).sample.value
      val doc   = parseView(applyView(tasks))

      behave like pageWithoutContent(doc, "h2", "Send your departure declaration")

      behave like pageWithoutContent(doc, "p", "By sending this, you are confirming that these details are correct to the best of your knowledge.")

      behave like pageWithoutSubmitButton(doc)
    }
  }
}
