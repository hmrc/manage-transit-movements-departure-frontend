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
import generators.Generators
import models.NormalMode
import models.transport.transportMeans.departure.{Identification, InlandMode}
import models.transport.transportMeans.departure.Identification._
import navigation.transport.TransportMeansNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.transport.transportMeans.departure.{IdentificationPage, InlandModePage, MeansIdentificationNumberPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.transportMeans.departure.MeansIdentificationNumberView

import scala.concurrent.Future

class MeansIdentificationNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider = new MeansIdentificationNumberProvider()

  private val identification: Identification = arbitrary[Identification].sample.value
  private val inlandMode: InlandMode         = arbitrary[InlandMode].retryUntil(_ != InlandMode.Unknown).sample.value

  private def form(identification: Identification) = formProvider("transport.transportMeans.departure.meansIdentificationNumber", identification.arg)

  private val mode                                = NormalMode
  private lazy val meansIdentificationNumberRoute = routes.MeansIdentificationNumberController.onPageLoad(lrn, mode).url

  private val validAnswer = "teststring"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportMeansNavigatorProvider]).toInstance(fakeTransportMeansNavigatorProvider))

  "MeansIdentificationNumber Controller" - {

    "must return OK and the correct view for a GET" - {

      "when inland mode is not unknown" in {

        val userAnswers = emptyUserAnswers
          .setValue(InlandModePage, inlandMode)
          .setValue(IdentificationPage, identification)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, meansIdentificationNumberRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[MeansIdentificationNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(identification), lrn, mode, identification)(request, messages).toString
      }

      "when inland mode is unknown" in {

        val userAnswers = emptyUserAnswers
          .setValue(InlandModePage, InlandMode.Unknown)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, meansIdentificationNumberRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[MeansIdentificationNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(Identification.Unknown), lrn, mode, Identification.Unknown)(request, messages).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" - {

      "when inland mode is not unknown" in {

        val userAnswers = emptyUserAnswers
          .setValue(InlandModePage, inlandMode)
          .setValue(IdentificationPage, identification)
          .setValue(MeansIdentificationNumberPage, validAnswer)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, meansIdentificationNumberRoute)

        val result = route(app, request).value

        val filledForm = form(identification).bind(Map("value" -> validAnswer))

        val view = injector.instanceOf[MeansIdentificationNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, identification)(request, messages).toString
      }

      "when inland mode is unknown" in {

        val userAnswers = emptyUserAnswers
          .setValue(InlandModePage, InlandMode.Unknown)
          .setValue(MeansIdentificationNumberPage, validAnswer)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, meansIdentificationNumberRoute)

        val result = route(app, request).value

        val filledForm = form(Identification.Unknown).bind(Map("value" -> validAnswer))

        val view = injector.instanceOf[MeansIdentificationNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, Identification.Unknown)(request, messages).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(
        emptyUserAnswers
          .setValue(InlandModePage, inlandMode)
          .setValue(IdentificationPage, identification)
      )

      val request = FakeRequest(POST, meansIdentificationNumberRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" - {

      "when identification is not unknown" in {

        val userAnswers = emptyUserAnswers
          .setValue(InlandModePage, inlandMode)
          .setValue(IdentificationPage, identification)

        setExistingUserAnswers(userAnswers)

        val invalidAnswer = ""

        val request    = FakeRequest(POST, meansIdentificationNumberRoute).withFormUrlEncodedBody(("value", ""))
        val filledForm = form(identification).bind(Map("value" -> invalidAnswer))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST

        val view = injector.instanceOf[MeansIdentificationNumberView]

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, identification)(request, messages).toString()
      }

      "when identification is unknown" in {

        val userAnswers = emptyUserAnswers
          .setValue(InlandModePage, InlandMode.Unknown)

        setExistingUserAnswers(userAnswers)

        val invalidAnswer = ""

        val request    = FakeRequest(POST, meansIdentificationNumberRoute).withFormUrlEncodedBody(("value", invalidAnswer))
        val filledForm = form(Identification.Unknown).bind(Map("value" -> invalidAnswer))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST

        val view = injector.instanceOf[MeansIdentificationNumberView]

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, Identification.Unknown)(request, messages).toString()
      }
    }

    "must redirect to Session Expired for a GET" - {

      "when no existing data is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(GET, meansIdentificationNumberRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
      }

      "when inland mode is not unknown but identification type is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(InlandModePage, inlandMode)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, meansIdentificationNumberRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
      }

    }

    "must redirect to Session Expired for a POST" - {

      "when no existing data is found" in {

        setNoExistingUserAnswers()

        val request = FakeRequest(POST, meansIdentificationNumberRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
      }

      "when inland mode is not unknown but identification type is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(InlandModePage, inlandMode)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, meansIdentificationNumberRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
      }
    }
  }
}
