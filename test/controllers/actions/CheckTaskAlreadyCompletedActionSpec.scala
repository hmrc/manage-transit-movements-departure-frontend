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

package controllers.actions

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.DeclarationType.Option1
import models.ProcedureType.Normal
import models.SecurityDetailsType.NoSecurityDetails
import models.journeyDomain.{PreTaskListDomain, TaskDomain}
import models.reference.{CountryCode, CustomsOffice}
import models.requests.DataRequest
import models.{EoriNumber, UserAnswers}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.preTaskList.{DeclarationTypePage, OfficeOfDeparturePage, ProcedureTypePage, SecurityDetailsTypePage}
import play.api.mvc.{AnyContent, Request, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class CheckTaskAlreadyCompletedActionSpec extends SpecBase with GuiceOneAppPerSuite with Generators with UserAnswersSpecHelper {

  def harness[T <: TaskDomain](userAnswers: UserAnswers): Future[Result] = {

    lazy val actionProvider = app.injector.instanceOf[CheckTaskAlreadyCompletedActionProviderImpl]

    actionProvider
      .apply[PreTaskListDomain]
      .invokeBlock(
        DataRequest(FakeRequest(GET, "/").asInstanceOf[Request[AnyContent]], EoriNumber(""), userAnswers),
        {
          _: DataRequest[AnyContent] =>
            Future.successful(Results.Ok)
        }
      )
  }

  "CheckTaskAlreadyCompletedAction" - {

    "return unit if dependent section is incomplete" in {

      val result: Future[Result] = harness(emptyUserAnswers)
      status(result) mustBe OK
      redirectLocation(result) mustBe None
    }

    "return to task list page if section has already been completed" in {

      val userAnswers = emptyUserAnswers
        .setValue(OfficeOfDeparturePage, CustomsOffice("id", "name", CountryCode("code"), None))
        .setValue(ProcedureTypePage, Normal)
        .setValue(DeclarationTypePage, Option1)
        .setValue(SecurityDetailsTypePage, NoSecurityDetails)

      val result = harness[PreTaskListDomain](userAnswers)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.TaskListController.onPageLoad(emptyUserAnswers.lrn).url)
    }
  }
}
