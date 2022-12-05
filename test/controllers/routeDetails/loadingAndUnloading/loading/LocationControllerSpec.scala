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

package controllers.routeDetails.loadingAndUnloading.loading

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.LocationFormProvider
import generators.Generators
import models.NormalMode
import models.reference.Country
import navigation.routeDetails.LoadingAndUnloadingNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.loadingAndUnloading.loading.{CountryPage, LocationPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.routeDetails.loadingAndUnloading.loading.LocationView

import scala.concurrent.Future

class LocationControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val country            = arbitrary[Country].sample.value
  private val countryName        = country.description
  private val formProvider       = new LocationFormProvider()
  private val form               = formProvider("routeDetails.loadingAndUnloading.loading.location", countryName)
  private val mode               = NormalMode
  private lazy val locationRoute = routes.LocationController.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[LoadingAndUnloadingNavigatorProvider]).toInstance(fakeLoadingNavigatorProvider))

  "Location Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.setValue(CountryPage, country)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, locationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[LocationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, country.description, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(CountryPage, country)
        .setValue(LocationPage, "Test")

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, locationRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "Test"))

      val view = injector.instanceOf[LocationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, country.description, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(CountryPage, country)
      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, locationRoute)
        .withFormUrlEncodedBody(("value", "Test"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(CountryPage, country)
      setExistingUserAnswers(userAnswers)

      val invalidAnswer = ">"

      val request    = FakeRequest(POST, locationRoute).withFormUrlEncodedBody(("value", invalidAnswer))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[LocationView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, countryName, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, locationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, locationRoute)
        .withFormUrlEncodedBody(("value", "test"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
