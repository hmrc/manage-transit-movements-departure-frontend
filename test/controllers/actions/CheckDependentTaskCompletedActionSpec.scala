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
import models.domain.UserAnswersReader
import models.journeyDomain.PreTaskListDomain
import models.journeyDomain.traderDetails.TraderDetailsDomain
import models.reference.CustomsOffice
import models.requests.DataRequest
import models.{EoriNumber, UserAnswers}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.preTaskList._
import play.api.mvc.{AnyContent, Request, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future
import scala.reflect.runtime.universe.TypeTag

class CheckDependentTaskCompletedActionSpec extends SpecBase with GuiceOneAppPerSuite with Generators with UserAnswersSpecHelper {

  def harness[T: TypeTag](userAnswers: UserAnswers)(implicit userAnswersReader: UserAnswersReader[T]): Future[Result] = {

    lazy val actionProvider = app.injector.instanceOf[CheckDependentTaskCompletedActionProviderImpl]

    actionProvider
      .apply[T]
      .invokeBlock(
        DataRequest(FakeRequest(GET, "/").asInstanceOf[Request[AnyContent]], EoriNumber(""), userAnswers),
        {
          _: DataRequest[AnyContent] =>
            Future.successful(Results.Ok)
        }
      )
  }

  "CheckDependentTaskCompletedAction" - {

    "return None if dependent section is completed" in {

      val userAnswers = emptyUserAnswers
        .setValue(OfficeOfDeparturePage, CustomsOffice("GB1", "name", None))
        .setValue(ProcedureTypePage, Normal)
        .setValue(DeclarationTypePage, Option1)
        .setValue(SecurityDetailsTypePage, NoSecurityDetails)
        .setValue(DetailsConfirmedPage, true)

      val result: Future[Result] = harness[PreTaskListDomain](userAnswers)
      status(result) mustBe OK
      redirectLocation(result) mustBe None
    }

    "return to LRN page if pre- task list is incomplete" in {

      val result = harness[PreTaskListDomain](emptyUserAnswers)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.preTaskList.routes.LocalReferenceNumberController.onPageLoad().url)
    }

    "return to task list if any other dependent section is incomplete" in {

      implicit val userAnswersReader: UserAnswersReader[TraderDetailsDomain] = TraderDetailsDomain.userAnswersReader(Nil)
      val result                                                             = harness[TraderDetailsDomain](emptyUserAnswers)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.TaskListController.onPageLoad(emptyUserAnswers.lrn).url)
    }
  }
}
