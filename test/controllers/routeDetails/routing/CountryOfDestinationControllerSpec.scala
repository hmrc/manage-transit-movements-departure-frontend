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

package controllers.routeDetails.routing

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.CountryFormProvider
import generators.Generators
import models.{CountryList, CustomsOfficeList, NormalMode}
import navigation.routeDetails.RoutingNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.routing.CountryOfDestinationPage
import play.api.data.FormError
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{CountriesService, CustomsOfficesService}
import views.html.routeDetails.routing.CountryOfDestinationView

import scala.concurrent.Future

class CountryOfDestinationControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val country1    = arbitraryCountry.arbitrary.sample.get
  private val country2    = arbitraryCountry.arbitrary.sample.get
  private val countryList = CountryList(Seq(country1, country2))

  private val formProvider = new CountryFormProvider()
  private val form         = formProvider("routeDetails.routing.countryOfDestination", countryList)
  private val mode         = NormalMode

  private val mockCountriesService: CountriesService           = mock[CountriesService]
  private val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]

  private lazy val countryOfDestinationRoute = routes.CountryOfDestinationController.onPageLoad(lrn, mode).url

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCountriesService, mockCustomsOfficesService)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[RoutingNavigatorProvider]).toInstance(fakeRoutingNavigatorProvider))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))
      .overrides(bind(classOf[CustomsOfficesService]).toInstance(mockCustomsOfficesService))

  "CountryOfDestination Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockCountriesService.getDestinationCountries(any(), any())(any()))
        .thenReturn(Future.successful(countryList))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, countryOfDestinationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CountryOfDestinationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, countryList.countries, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCountriesService.getDestinationCountries(any(), any())(any()))
        .thenReturn(Future.successful(countryList))

      val userAnswers = emptyUserAnswers.setValue(CountryOfDestinationPage, country1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, countryOfDestinationRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> country1.code.code))

      val view = injector.instanceOf[CountryOfDestinationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, countryList.countries, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      when(mockCountriesService.getDestinationCountries(any(), any())(any()))
        .thenReturn(Future.successful(countryList))
      when(mockCustomsOfficesService.getCustomsOfficesOfExitForCountry(any())(any()))
        .thenReturn(Future.successful(arbitrary[CustomsOfficeList].sample.value))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, countryOfDestinationRoute)
        .withFormUrlEncodedBody(("value", country1.code.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getDestinationCountries(any(), any())(any()))
        .thenReturn(Future.successful(countryList))

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, countryOfDestinationRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[CountryOfDestinationView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, countryList.countries, mode)(request, messages).toString
    }

    "must return a Bad Request and errors when submitted country has no corresponding customs offices" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      when(mockCountriesService.getDestinationCountries(any(), any())(any()))
        .thenReturn(Future.successful(countryList))
      when(mockCustomsOfficesService.getCustomsOfficesOfExitForCountry(any())(any()))
        .thenReturn(Future.successful(CustomsOfficeList(Nil)))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, countryOfDestinationRoute).withFormUrlEncodedBody(("value", country1.code.code))

      val boundForm = form
        .withError(FormError("value", "You cannot use this country as it does not have any offices of destination"))

      val result = route(app, request).value

      val view = injector.instanceOf[CountryOfDestinationView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, countryList.countries, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, countryOfDestinationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, countryOfDestinationRoute)
        .withFormUrlEncodedBody(("value", country1.code.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
