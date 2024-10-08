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

package controllers.actions

import base.SpecBase
import generators.Generators
import models.requests.DataRequest
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.mvc.Result
import play.api.test.Helpers._
import viewModels.taskList.{PreTaskListTask, TaskStatus}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PreTaskListCompletedActionSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private class Harness() extends PreTaskListCompletedActionImpl() {

    def callFilter(tasks: Map[String, TaskStatus]): Future[Option[Result]] = {
      val request = DataRequest(fakeRequest, eoriNumber, emptyUserAnswers.copy(tasks = tasks))
      filter(request)
    }
  }

  "CheckTaskAlreadyCompletedAction" - {

    "return None if dependent section is incomplete" in {
      forAll(arbitrary[TaskStatus](arbitraryIncompleteTaskStatus)) {
        taskStatus =>
          val action = new Harness()
          val tasks  = Map(PreTaskListTask.section -> taskStatus)
          val result = action.callFilter(tasks).futureValue
          result mustBe None
      }
    }

    "return to task list page if section has already been completed" in {
      val action = new Harness()
      val tasks  = Map(PreTaskListTask.section -> TaskStatus.Completed)
      val result = action.callFilter(tasks).map(_.value)
      redirectLocation(result).value mustBe controllers.routes.TaskListController.onPageLoad(emptyUserAnswers.lrn).url
    }
  }
}
