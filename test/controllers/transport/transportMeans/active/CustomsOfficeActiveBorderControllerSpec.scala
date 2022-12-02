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
import forms.CustomsOfficeFormProvider
import generators.Generators
import models.reference.CustomsOffice
import models.{CustomsOfficeList, Index, NormalMode}
import navigation.transport.TransportMeansNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.exit.index.OfficeOfExitPage
import pages.routeDetails.routing.OfficeOfDestinationPage
import pages.routeDetails.transit.index.OfficeOfTransitPage
import pages.transport.transportMeans.active.CustomsOfficeActiveBorderPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.transportMeans.active.CustomsOfficeActiveBorderView

import scala.concurrent.Future

class CustomsOfficeActiveBorderControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val exitOffice        = arbitrary[CustomsOffice].sample.value
  private val transitOffice     = arbitrary[CustomsOffice].sample.value
  private val destinationOffice = arbitrary[CustomsOffice].sample.value

  private val allCustomOfficesList = CustomsOfficeList(List(exitOffice, transitOffice, destinationOffice))

  private val formProvider = new CustomsOfficeFormProvider()
  private val form         = formProvider("transport.transportMeans.active.customsOfficeActiveBorder", allCustomOfficesList)
  private val mode         = NormalMode

  private lazy val customsOfficeActiveBorderRoute = routes.CustomsOfficeActiveBorderController.onPageLoad(lrn, mode, activeIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportMeansNavigatorProvider]).toInstance(fakeTransportMeansNavigatorProvider))

  "ActiveBorderOfficeTransit Controller" - {

    "must return OK and the correct view for a GET" - {
      "when only destination office defined" in {

        val updatedAnswers = emptyUserAnswers
          .setValue(OfficeOfDestinationPage, destinationOffice)

        setExistingUserAnswers(updatedAnswers)

        val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[CustomsOfficeActiveBorderView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, lrn, CustomsOfficeList(List(destinationOffice)), mode, index)(request, messages).toString
      }
      "when one office of transit is defined" in {

        val updatedAnswers = emptyUserAnswers
          .setValue(OfficeOfTransitPage(Index(0)), transitOffice)
          .setValue(OfficeOfDestinationPage, destinationOffice)

        setExistingUserAnswers(updatedAnswers)

        val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[CustomsOfficeActiveBorderView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, lrn, CustomsOfficeList(List(transitOffice, destinationOffice)), mode, index)(request, messages).toString
      }
      "when one office of exit is defined" in {

        val updatedAnswers = emptyUserAnswers
          .setValue(OfficeOfExitPage(Index(0)), exitOffice)
          .setValue(OfficeOfDestinationPage, destinationOffice)

        setExistingUserAnswers(updatedAnswers)

        val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[CustomsOfficeActiveBorderView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, lrn, CustomsOfficeList(List(exitOffice, destinationOffice)), mode, index)(request, messages).toString
      }
      "when one of each office is defined" in {
        val updatedAnswers = emptyUserAnswers
          .setValue(OfficeOfExitPage(Index(0)), exitOffice)
          .setValue(OfficeOfTransitPage(Index(0)), transitOffice)
          .setValue(OfficeOfDestinationPage, destinationOffice)

        setExistingUserAnswers(updatedAnswers)

        val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[CustomsOfficeActiveBorderView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, lrn, allCustomOfficesList, mode, index)(request, messages).toString
      }
      "when multiple office of transit and exit are defined" in {

        val exitOffice2          = arbitrary[CustomsOffice].sample.value
        val transitOffice2       = arbitrary[CustomsOffice].sample.value
        val allCustomOfficesList = CustomsOfficeList(List(exitOffice, exitOffice2, transitOffice, transitOffice2, destinationOffice))

        val updatedAnswers = emptyUserAnswers
          .setValue(OfficeOfExitPage(Index(0)), exitOffice)
          .setValue(OfficeOfExitPage(Index(1)), exitOffice2)
          .setValue(OfficeOfTransitPage(Index(0)), transitOffice)
          .setValue(OfficeOfTransitPage(Index(1)), transitOffice2)
          .setValue(OfficeOfDestinationPage, destinationOffice)

        setExistingUserAnswers(updatedAnswers)

        val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[CustomsOfficeActiveBorderView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, lrn, allCustomOfficesList, mode, index)(request, messages).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(OfficeOfDestinationPage, destinationOffice)
        .setValue(OfficeOfTransitPage(index), transitOffice)
        .setValue(OfficeOfExitPage(index), exitOffice)
        .setValue(CustomsOfficeActiveBorderPage(index), destinationOffice)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> destinationOffice.id))

      val view = injector.instanceOf[CustomsOfficeActiveBorderView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, allCustomOfficesList, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      val updatedAnswers = emptyUserAnswers
        .setValue(OfficeOfDestinationPage, destinationOffice)
        .setValue(OfficeOfTransitPage(index), transitOffice)
        .setValue(OfficeOfExitPage(index), exitOffice)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(updatedAnswers)

      val request = FakeRequest(POST, customsOfficeActiveBorderRoute)
        .withFormUrlEncodedBody(("value", destinationOffice.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val updatedAnswers = emptyUserAnswers
        .setValue(OfficeOfDestinationPage, destinationOffice)
        .setValue(OfficeOfTransitPage(index), transitOffice)
        .setValue(OfficeOfExitPage(index), exitOffice)

      setExistingUserAnswers(updatedAnswers)

      val request   = FakeRequest(POST, customsOfficeActiveBorderRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[CustomsOfficeActiveBorderView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, allCustomOfficesList, mode, index)(request, messages).toString
    }
  }

  "must redirect to Session Expired for a GET if no existing data is found" in {

    setNoExistingUserAnswers()

    val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

    val result = route(app, request).value

    status(result) mustEqual SEE_OTHER
    redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
  }

  "must redirect to Session Expired for a POST if no existing data is found" in {

    setNoExistingUserAnswers()

    val request = FakeRequest(POST, customsOfficeActiveBorderRoute)
      .withFormUrlEncodedBody(("value", destinationOffice.id))

    val result = route(app, request).value

    status(result) mustEqual SEE_OTHER

    redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
  }
}
