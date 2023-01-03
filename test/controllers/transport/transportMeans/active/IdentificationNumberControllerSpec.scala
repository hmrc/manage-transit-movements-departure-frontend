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
import forms.transport.transportMeans.active.IdentificationNumberFormProvider
import generators.Generators
import models.NormalMode
import models.transport.transportMeans.BorderModeOfTransport
import models.transport.transportMeans.active.Identification
import navigation.transport.TransportMeansActiveNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.transportMeans.BorderModeOfTransportPage
import pages.transport.transportMeans.active.{IdentificationNumberPage, IdentificationPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.transportMeans.active.IdentificationNumberView

import scala.concurrent.Future

class IdentificationNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val prefix                                           = "transport.transportMeans.active.identificationNumber"
  private def dynamicTitle(identificationType: Identification) = messages(s"$prefix.$identificationType")

  private val formProvider                             = new IdentificationNumberFormProvider()
  private def form(identificationType: Identification) = formProvider(prefix, dynamicTitle(identificationType))
  private val mode                                     = NormalMode
  private lazy val identificationNumberRoute           = routes.IdentificationNumberController.onPageLoad(lrn, mode, index).url

  private val validAnswer = "testString"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportMeansActiveNavigatorProvider]).toInstance(fakeTransportMeansActiveNavigatorProvider))

  "IdentificationNumber Controller" - {

    "must return OK and the correct view for a GET" - {

      "when border mode is Rail" in {

        val identificationType = Identification.TrainNumber
        val userAnswers        = emptyUserAnswers.setValue(BorderModeOfTransportPage, BorderModeOfTransport.Rail)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, identificationNumberRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[IdentificationNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(identificationType), lrn, s"$prefix.$identificationType", mode, index)(request, messages).toString
      }

      "when border mode is Road" in {

        val identificationType = Identification.RegNumberRoadVehicle
        val userAnswers        = emptyUserAnswers.setValue(BorderModeOfTransportPage, BorderModeOfTransport.Road)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, identificationNumberRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[IdentificationNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(identificationType), lrn, s"$prefix.$identificationType", mode, index)(request, messages).toString
      }

      "when border mode is something else" in {

        val borderModeGen = Gen
          .oneOf(BorderModeOfTransport.values)
          .filterNot(_ == BorderModeOfTransport.Rail)
          .filterNot(_ == BorderModeOfTransport.Road)

        forAll(borderModeGen, arbitrary[Identification]) {
          (borderMode, identificationType) =>
            val userAnswers = emptyUserAnswers
              .setValue(BorderModeOfTransportPage, borderMode)
              .setValue(IdentificationPage(index), identificationType)
            setExistingUserAnswers(userAnswers)

            val request = FakeRequest(GET, identificationNumberRoute)

            val result = route(app, request).value

            val view = injector.instanceOf[IdentificationNumberView]

            status(result) mustEqual OK

            contentAsString(result) mustEqual
              view(form(identificationType), lrn, s"$prefix.$identificationType", mode, index)(request, messages).toString
        }
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val identificationType = Identification.ImoShipIdNumber
      val userAnswers = emptyUserAnswers
        .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Maritime)
        .setValue(IdentificationPage(index), identificationType)
        .setValue(IdentificationNumberPage(index), validAnswer)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, identificationNumberRoute)

      val result = route(app, request).value

      val filledForm = form(identificationType).bind(Map("value" -> validAnswer))

      val view = injector.instanceOf[IdentificationNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, s"$prefix.$identificationType", mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(BorderModeOfTransportPage, BorderModeOfTransport.Road)
      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, identificationNumberRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val identificationType = Identification.RegNumberRoadVehicle
      val userAnswers        = emptyUserAnswers.setValue(BorderModeOfTransportPage, BorderModeOfTransport.Road)
      setExistingUserAnswers(userAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, identificationNumberRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form(identificationType).bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[IdentificationNumberView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, s"$prefix.$identificationType", mode, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, identificationNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, identificationNumberRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
