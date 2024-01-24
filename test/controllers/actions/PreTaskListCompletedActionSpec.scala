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
import generators.Generators
import models.UserAnswers
import models.requests.DataRequest
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.DetailsConfirmedPage
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PreTaskListCompletedActionSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private class Harness() extends PreTaskListCompletedActionImpl() {

    def callFilter(userAnswers: UserAnswers): Future[Option[Result]] = {
      val request = DataRequest(fakeRequest, eoriNumber, userAnswers)
      filter(request)
    }
  }

  "PreTaskListCompletedAction" - {

    "return None if details not yet confirmed" in {
      val userAnswersGen = Gen.oneOf(
        emptyUserAnswers,
        emptyUserAnswers.setValue(DetailsConfirmedPage, false)
      )
      forAll(userAnswersGen) {
        userAnswers =>
          val action = new Harness()
          val result = action.callFilter(userAnswers).futureValue
          result mustBe None
      }
    }

    "return to task list page if details have been confirmed" in {
      val action      = new Harness()
      val userAnswers = emptyUserAnswers.setValue(DetailsConfirmedPage, true)
      val result      = action.callFilter(userAnswers).map(_.value)
      redirectLocation(result).value mustBe controllers.routes.TaskListController.onPageLoad(emptyUserAnswers.lrn).url
    }
  }
}
