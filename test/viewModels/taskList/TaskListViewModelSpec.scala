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

class TaskListViewModelSpec extends SpecBase {

  "apply" - {
    "must create tasks" in {
      val answers = emptyUserAnswers

      val tasks = new TaskListViewModel().apply(answers)

      tasks.size mustBe 4

      tasks.head.name mustBe "Add trader details"
      tasks.head.status mustBe TaskStatus.NotStarted
      tasks.head.href(answers.lrn)(frontendAppConfig) must endWith(s"/$lrn/trader-details")

      tasks(1).name mustBe "Add route details"
      tasks(1).status mustBe TaskStatus.NotStarted
      tasks(1).href(answers.lrn)(frontendAppConfig) must endWith(s"/$lrn/route-details")

      tasks(2).name mustBe "Transport details"
      tasks(2).status mustBe TaskStatus.CannotStartYet
      tasks(2).href(answers.lrn)(frontendAppConfig) must endWith(s"/$lrn/transport-details")

      tasks(3).name mustBe "Add guarantee details"
      tasks(3).status mustBe TaskStatus.NotStarted
      tasks(3).href(answers.lrn)(frontendAppConfig) must endWith(s"/$lrn/guarantee-details")
    }
  }
}
