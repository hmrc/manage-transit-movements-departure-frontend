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

package controllers.transport.transportMeans.active

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.transport.transportMeans.active.ConveyanceReferenceNumberFormProvider
import models.NormalMode
import navigation.transport.TransportMeansActiveNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.transport.transportMeans.active.ConveyanceReferenceNumberPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.transportMeans.active.ConveyanceReferenceNumberView

import scala.concurrent.Future

class ConveyanceReferenceNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                        = new ConveyanceReferenceNumberFormProvider()
  private val form                                = formProvider("transport.transportMeans.active.conveyanceReferenceNumber")
  private val mode                                = NormalMode
  private lazy val conveyanceReferenceNumberRoute = routes.ConveyanceReferenceNumberController.onPageLoad(lrn, mode, activeIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportMeansActiveNavigatorProvider]).toInstance(fakeTransportMeansActiveNavigatorProvider))

  "ConveyanceReferenceNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, conveyanceReferenceNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ConveyanceReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, activeIndex)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(ConveyanceReferenceNumberPage(activeIndex), "testString")
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, conveyanceReferenceNumberRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "testString"))

      val view = injector.instanceOf[ConveyanceReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, activeIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, conveyanceReferenceNumberRoute)
        .withFormUrlEncodedBody(("value", "testString"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, conveyanceReferenceNumberRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ConveyanceReferenceNumberView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, activeIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, conveyanceReferenceNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, conveyanceReferenceNumberRoute)
        .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
