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

package views.behaviours

import generators.Generators
import viewModels.taskList.Task

import scala.collection.JavaConverters._

trait TaskListViewBehaviours extends ViewBehaviours with Generators {

  lazy val tasks: Seq[Task] = listWithMaxLength[Task]()(arbitraryTask).sample.value

  override val urlContainsLrn: Boolean = true

  def pageWithTaskList(): Unit =
    "page with task list" - {

      val taskList = getElementByClass(doc, "app-task-list")

      val renderedTasks = taskList.getElementsByClass("app-task-list__item").asScala

      tasks.zipWithIndex.foreach {
        case (task, taskIndex) =>
          val renderedTask = renderedTasks(taskIndex)

          s"task ${taskIndex + 1}" - {
            val name = renderedTask.getElementsByClass("app-task-list__task-name").first()

            task.href match {
              case Some(href) =>
                "must contain a name with a link" in {
                  val link = name.getElementsByTag("a").first()
                  getElementHref(link) mustBe href
                  link.attr("aria-describedby") mustBe s"${task.id}-status"
                  link.text() mustBe task.name
                }
              case None =>
                "must contain a name" in {
                  name.text() mustBe task.name
                }
            }

            "must contain a tag" in {
              val tag = renderedTask.getElementsByClass("app-task-list__tag").first()
              tag.text() mustBe messages(task.status.messageKey)
              tag.id() mustBe s"${task.id}-status"
              assert(tag.hasClass(task.status.tag))
            }
          }
      }
    }
}
