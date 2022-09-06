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

package controllers.routeDetails.locationOfGoods

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.LocationOfGoodsAddressFormProvider
import generators.Generators
import models.{Address, CountryList, NormalMode, UserAnswers}
import navigation.routeDetails.LocationOfGoodsNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.locationOfGoods.LocationOfGoodsAddressPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CountriesService
import views.html.routeDetails.locationOfGoods.LocationOfGoodsAddressView

import scala.concurrent.Future

class LocationOfGoodsAddressControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val testAddress = arbitrary[Address].sample.value
  private val countryList = CountryList(Seq(testAddress.country))

  private val formProvider = new LocationOfGoodsAddressFormProvider()
  private val form         = formProvider("routeDetails.locationOfGoods.locationOfGoodsAddress", countryList)

  private val mode                             = NormalMode
  private lazy val locationOfGoodsAddressRoute = routes.LocationOfGoodsAddressController.onPageLoad(lrn, mode).url

  private lazy val mockCountriesService: CountriesService = mock[CountriesService]

  override def beforeEach(): Unit = {
    reset(mockCountriesService)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))
      .overrides(bind(classOf[LocationOfGoodsNavigatorProvider]).toInstance(fakeLocationOfGoodsNavigatorProvider))

  "LocationOfGoodsAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countryList))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, locationOfGoodsAddressRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[LocationOfGoodsAddressView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, countryList.countries)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countryList))

      val userAnswers = UserAnswers(lrn, eoriNumber)
        .setValue(LocationOfGoodsAddressPage, testAddress)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, locationOfGoodsAddressRoute)

      val result = route(app, request).value

      val filledForm = form.bind(
        Map(
          "addressLine1" -> testAddress.line1,
          "addressLine2" -> testAddress.line2,
          "postalCode"   -> testAddress.postalCode,
          "country"      -> testAddress.country.code.code
        )
      )

      val view = injector.instanceOf[LocationOfGoodsAddressView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, countryList.countries)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countryList))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, locationOfGoodsAddressRoute)
        .withFormUrlEncodedBody(
          ("addressLine1", testAddress.line1),
          ("addressLine2", testAddress.line2),
          ("postalCode", testAddress.postalCode),
          ("country", testAddress.country.code.code)
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countryList))

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, locationOfGoodsAddressRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[LocationOfGoodsAddressView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, countryList.countries)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, locationOfGoodsAddressRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, locationOfGoodsAddressRoute)
        .withFormUrlEncodedBody(
          ("addressLine1", testAddress.line1),
          ("addressLine2", testAddress.line2),
          ("postalCode", testAddress.postalCode),
          ("country", testAddress.country.code.code)
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
