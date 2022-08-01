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

package controllers.routeDetails.transit

import java.time.LocalDateTime

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.DateTimeFormProvider
import generators.Generators
import models.NormalMode
import navigation.routeDetails.OfficeOfTransitCountryNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.routeDetails.transit.{OfficeOfTransitCountryPage, OfficeOfTransitETAPage, OfficeOfTransitPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.routeDetails.transit.OfficeOfTransitETAView

import scala.concurrent.Future

class OfficeOfTransitETAControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider              = new DateTimeFormProvider()
  private val form                      = formProvider("routeDetails.transit.officeOfTransitETA")
  private val mode                      = NormalMode
  private lazy val arrivalDateTimeRoute = routes.OfficeOfTransitETAController.onPageLoad(lrn, mode, index).url
  private val date                      = LocalDateTime.now
  private val transitCountry            = arbitraryCountry.arbitrary.sample.get
  private val transitCustomsOffice      = arbitraryCustomsOffice.arbitrary.sample.get

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[OfficeOfTransitCountryNavigatorProvider]).toInstance(fakeOfficeOfTransitCountryNavigatorProvider))

  "ArrivalDateTime Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .setValue(OfficeOfTransitCountryPage(index), transitCountry)
        .setValue(OfficeOfTransitPage(index), transitCustomsOffice)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, arrivalDateTimeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[OfficeOfTransitETAView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, transitCountry.description, transitCustomsOffice.name, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(OfficeOfTransitETAPage(index), date)
        .setValue(OfficeOfTransitCountryPage(index), transitCountry)
        .setValue(OfficeOfTransitPage(index), transitCustomsOffice)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, arrivalDateTimeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(
        Map(
          "value.day"    -> date.getDayOfMonth.toString,
          "value.month"  -> date.getMonthValue.toString,
          "value.year"   -> date.getYear.toString,
          "value.hour"   -> date.getHour.toString,
          "value.minute" -> date.getMinute.toString
        )
      )

      val view = injector.instanceOf[OfficeOfTransitETAView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, transitCountry.description, transitCustomsOffice.name, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .setValue(OfficeOfTransitCountryPage(index), transitCountry)
        .setValue(OfficeOfTransitPage(index), transitCustomsOffice)
      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, arrivalDateTimeRoute)
        .withFormUrlEncodedBody(
          "value.day"    -> date.getDayOfMonth.toString,
          "value.month"  -> date.getMonthValue.toString,
          "value.year"   -> date.getYear.toString,
          "value.hour"   -> date.getHour.toString,
          "value.minute" -> date.getMinute.toString
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .setValue(OfficeOfTransitCountryPage(index), transitCountry)
        .setValue(OfficeOfTransitPage(index), transitCustomsOffice)
      setExistingUserAnswers(userAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, arrivalDateTimeRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[OfficeOfTransitETAView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, transitCountry.description, transitCustomsOffice.name, mode, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, arrivalDateTimeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, arrivalDateTimeRoute)
        .withFormUrlEncodedBody(
          "value.day"    -> date.getDayOfMonth.toString,
          "value.month"  -> date.getMonthValue.toString,
          "value.year"   -> date.getYear.toString,
          "value.hour"   -> date.getHour.toString,
          "value.minute" -> date.getMinute.toString
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
