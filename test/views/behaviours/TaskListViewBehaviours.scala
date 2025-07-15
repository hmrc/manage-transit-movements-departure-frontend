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

package views.behaviours

import generators.Generators
import models.{LocalReferenceNumber, SubmissionState}
import org.scalacheck.Arbitrary.arbitrary
import viewModels.taskList.TaskListTask
import viewModels.taskList.TaskStatus._

import scala.jdk.CollectionConverters._

trait TaskListViewBehaviours extends ViewBehaviours with Generators {

  lazy val tasks: Seq[TaskListTask] = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryTask)).sample.value

  val submissionState: SubmissionState.Value = SubmissionState.NotSubmitted

  def pageWithTaskList(lrn: LocalReferenceNumber): Unit =
    "page with task list" - {

      val taskList = getElementByClass(doc, "govuk-task-list")

      val renderedTasks = taskList.getElementsByClass("govuk-task-list__item").asScala

      tasks.zipWithIndex.foreach {
        case (task, taskIndex) =>
          val renderedTask = renderedTasks(taskIndex)

          s"task ${taskIndex + 1}" - {
            val name = renderedTask.getElementsByClass("govuk-task-list__name-and-hint").first()

            task.status match {
              case CannotStartYet | Unavailable =>
                "must contain a name" in {
                  name.text() mustEqual task.name
                }
              case _ =>
                "must contain a name with a link" in {
                  val link = name.getElementsByClass("govuk-link govuk-task-list__link").first()
                  getElementHref(link) mustEqual task.href(lrn)(frontendAppConfig)
                  link.attr("aria-describedby") mustEqual s"task-list-${taskIndex + 1}-status"
                  link.text() mustEqual task.name
                }
            }

            "must contain a tag" in {
              val status = renderedTask.getElementsByClass("govuk-task-list__status").first()
              status.id() mustEqual s"task-list-${taskIndex + 1}-status"
              val tag = status.getElementsByClass("govuk-tag").first()
              tag.text() mustEqual messages(task.status.messageKey)
              tag.id() mustEqual s"${task.id}-status"
              assert(tag.hasClass(task.status.tag))
            }
          }
      }
    }
}
