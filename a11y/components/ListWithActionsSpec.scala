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

package components

import a11ySpecBase.A11ySpecBase
import models.LocalReferenceNumber
import org.scalacheck.Arbitrary.arbitrary
import viewModels.taskList.Task
import views.html.components.TaskList
import views.html.templates.MainTemplate

class ListWithActionsSpec extends A11ySpecBase {

  "the 'list with actions' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[TaskList]

    val title      = nonEmptyString.sample.value
    val sectionKey = nonEmptyString.sample.value
    val tasks      = arbitrary[List[Task]](arbitraryTasks(arbitraryTask)).sample.value
    val lrn        = arbitrary[LocalReferenceNumber].sample.value

    val content = template.apply(title) {
      component.apply(sectionKey, tasks, lrn).withHeading(title)
    }

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks
    }
  }
}
