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

  private def applyView(tasks: Seq[TaskListTask]): HtmlFormat.Appendable =
    injector.instanceOf[TaskListView].apply(lrn, tasks)(fakeRequest, messages)

  override val prefix: String = "taskList"

  behave like pageWithTitle()

  behave like pageWithoutBackLink()

  behave like pageWithCaption(lrn.toString)

  behave like pageWithHeading()

  behave like pageWithContent("h2", "Departure details")

  behave like pageWithTaskList(lrn)

  behave like pageWithLink(
    "transit-movements",
    "Back to transit movements",
    frontendAppConfig.serviceUrl
  )

  "when all tasks completed" - {
    val tasks = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryCompletedTask)).sample.value
    val doc   = parseView(applyView(tasks))

    behave like pageWithContent(doc, "h2", "Now send your departure declaration")

    behave like pageWithContent(doc, "p", "By sending this you are confirming that the details you are providing are correct, to the best of your knowledge.")

    behave like pageWithSubmitButton(doc, "Confirm and send")
  }
}
