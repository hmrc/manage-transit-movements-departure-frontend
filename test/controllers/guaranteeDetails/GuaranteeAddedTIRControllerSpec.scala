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

package controllers.guaranteeDetails

import base.{AppWithDefaultMockFixtures, SpecBase}
import models.guaranteeDetails.GuaranteeType.TIRGuarantee
import models.{Index, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import pages.guaranteeDetails.guarantee.GuaranteeTypePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.guaranteeDetails.GuaranteeAddedTIRView

import scala.concurrent.Future

class GuaranteeAddedTIRControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private lazy val guaranteeAddedTIRRoute = routes.GuaranteeAddedTIRController.onPageLoad(lrn).url

  "GuaranteeAddedTIR Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, guaranteeAddedTIRRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[GuaranteeAddedTIRView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn)(request, messages).toString
    }

    "must redirect to check your answers" in {
      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      val request = FakeRequest(POST, routes.GuaranteeAddedTIRController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.TaskListController.onPageLoad(lrn).url

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
      userAnswersCaptor.getValue.get(GuaranteeTypePage(Index(0))).get mustBe TIRGuarantee
    }
  }
}
