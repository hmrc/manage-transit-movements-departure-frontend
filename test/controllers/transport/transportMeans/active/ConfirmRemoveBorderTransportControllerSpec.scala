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

package controllers.transport.transportMeans.active

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.Generators
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import pages.sections.transport.transportMeans
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.transportMeans.active.ConfirmRemoveBorderTransportView

import scala.concurrent.Future

class ConfirmRemoveBorderTransportControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider                           = new YesNoFormProvider()
  private val form                                   = formProvider("transport.transportMeans.active.confirmRemoveBorderTransport", activeIndex.display)
  private val mode                                   = NormalMode
  private lazy val confirmRemoveBorderTransportRoute = routes.ConfirmRemoveBorderTransportController.onPageLoad(lrn, mode, activeIndex).url

  "ConfirmRemoveBorderTransport Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, confirmRemoveBorderTransportRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[ConfirmRemoveBorderTransportView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, activeIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted and call to remove a transport active border" in {
      forAll(arbitraryTransportMeansActiveAnswers(emptyUserAnswers, activeIndex)) {
        userAnswers =>
          reset(mockSessionRepository)
          when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

          setExistingUserAnswers(userAnswers)

          val request = FakeRequest(POST, confirmRemoveBorderTransportRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            routes.AddAnotherBorderTransportController.onPageLoad(userAnswers.lrn, mode).url

          val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
          userAnswersCaptor.getValue.get(transportMeans.TransportMeansActiveSection(activeIndex)) mustNot be(defined)
      }
    }

    "must redirect to the next page when valid data is submitted and call to remove a transport active border is false" in {

      val userAnswers = emptyUserAnswers

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, confirmRemoveBorderTransportRoute)
        .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        routes.AddAnotherBorderTransportController.onPageLoad(userAnswers.lrn, mode).url

      verify(mockSessionRepository, never()).set(any())(any())
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, confirmRemoveBorderTransportRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val result    = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ConfirmRemoveBorderTransportView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, activeIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, confirmRemoveBorderTransportRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, confirmRemoveBorderTransportRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
