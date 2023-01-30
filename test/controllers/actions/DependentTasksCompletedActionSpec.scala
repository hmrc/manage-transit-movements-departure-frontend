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

package controllers.actions

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.requests.DataRequest
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.mvc.{Result, Results}
import play.api.test.Helpers._
import viewModels.taskList.{PreTaskListTask, TaskStatus}

import scala.concurrent.Future

class DependentTasksCompletedActionSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersSpecHelper {

  def harness(tasks: Map[String, TaskStatus], dependentTasks: String*): Future[Result] = {

    lazy val actionProvider = app.injector.instanceOf[DependentTasksCompletedActionProviderImpl]

    actionProvider
      .apply(dependentTasks: _*)
      .invokeBlock(
        DataRequest(fakeRequest, eoriNumber, emptyUserAnswers.copy(tasks = tasks)),
        {
          _: DataRequest[_] =>
            Future.successful(Results.Ok)
        }
      )
  }

  "DependentTasksCompletedAction" - {

    "return None if dependent section is completed" in {
      val tasks                  = Map(PreTaskListTask.section -> TaskStatus.Completed)
      val result: Future[Result] = harness(tasks)
      status(result) mustBe OK
      redirectLocation(result) mustBe None
    }

    "return to LRN page if pre- task list is incomplete" in {
      forAll(arbitrary[TaskStatus](arbitraryIncompleteTaskStatus)) {
        taskStatus =>
          val tasks  = Map(PreTaskListTask.section -> taskStatus)
          val result = harness(tasks, PreTaskListTask.section)
          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe
            controllers.preTaskList.routes.LocalReferenceNumberController.onPageLoad().url
      }
    }

    "return to task list if any other dependent section is incomplete" in {
      forAll(nonEmptyString, arbitrary[TaskStatus](arbitraryIncompleteTaskStatus)) {
        (section, taskStatus) =>
          val tasks  = Map(PreTaskListTask.section -> TaskStatus.Completed, section -> taskStatus)
          val result = harness(tasks, section)
          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe
            controllers.routes.TaskListController.onPageLoad(emptyUserAnswers.lrn).url
      }
    }
  }
}
