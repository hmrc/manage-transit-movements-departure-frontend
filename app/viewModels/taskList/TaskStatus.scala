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

sealed trait TaskStatus {
  val messageKey: String
  val tag: String
}

object TaskStatus {

  case object Completed extends TaskStatus {
    override val messageKey: String = "taskStatus.completed"
    override val tag: String        = "govuk-tag--green"
  }

  case object InProgress extends TaskStatus {
    override val messageKey: String = "taskStatus.inProgress"
    override val tag: String        = "govuk-tag--blue"
  }

  case object NotStarted extends TaskStatus {
    override val messageKey: String = "taskStatus.notStarted"
    override val tag: String        = "govuk-tag--grey"
  }

  case object CannotStartYet extends TaskStatus {
    override val messageKey: String = "taskStatus.cannotStartYet"
    override val tag: String        = "govuk-tag--red"
  }
}
