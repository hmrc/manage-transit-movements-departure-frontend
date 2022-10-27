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

package controllers.transport.transportMeans.departure

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.MeansIdentificationNumberProvider
import models.InlandMode.Road
import models.reference.Country
import models.{InlandMode, NormalMode, WithName}
import navigation.transport.TransportMeansNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import pages.traderDetails.holderOfTransit.CountryPage
import pages.transport.transportMeans.departure.{InlandModePage, MeansIdentificationNumberPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.transportMeans.departure.MeansIdentificationNumberView

import scala.concurrent.Future

class MeansIdentificationNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider = new MeansIdentificationNumberProvider()

  val inlandMode: InlandMode = Road
  // TODO: Add  arbitrary identificationType
  private val identificationType                  = "IATA flight number" //TODO: Change to identification.toString
  private val form                                = formProvider("transport.transportMeans.departure.meansIdentificationNumber", identificationType)
  private val mode                                = NormalMode
  private lazy val meansIdentificationNumberRoute = routes.MeansIdentificationNumberController.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportMeansNavigatorProvider]).toInstance(fakeTransportMeansNavigatorProvider))

  "MeansIdentificationNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.setValue(InlandModePage, inlandMode) //TODO: Change to identification mean type
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, meansIdentificationNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[MeansIdentificationNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, inlandMode.toString)(request, messages).toString // TODO: Change to correct arg
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(InlandModePage, inlandMode) //TODO: Change to identification mean type
        .setValue(MeansIdentificationNumberPage, "test string") //TODO: Add previous page
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, meansIdentificationNumberRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "test string"))

      val view = injector.instanceOf[MeansIdentificationNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, inlandMode.toString)(request, messages).toString // TODO: Change to correct arg
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(InlandModePage, inlandMode) //TODO: Change to identification mean type
      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, meansIdentificationNumberRoute)
        .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(InlandModePage, inlandMode) //TODO: Change to identification mean type
      setExistingUserAnswers(userAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, meansIdentificationNumberRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[MeansIdentificationNumberView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, inlandMode.toString)(request, messages).toString() // TODO: Change to correct arg
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, meansIdentificationNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, meansIdentificationNumberRoute)
        .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
