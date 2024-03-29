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

package viewModels

import viewModels.taskList.TaskStatus.Completed

package object taskList {

  implicit class RichDependentSections(dependentSections: Seq[String]) {

    def allCompleted(tasks: Map[String, TaskStatus]): Boolean =
      dependentSections.forall(_.isCompleted(tasks))
  }

  implicit class RichDependentSection(dependentSection: String) {

    def isCompleted(tasks: Map[String, TaskStatus]): Boolean =
      tasks.exists {
        case (`dependentSection`, Completed) => true
        case _                               => false
      }
  }
}
