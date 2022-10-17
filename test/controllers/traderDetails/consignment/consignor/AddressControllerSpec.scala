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

package controllers.traderDetails.consignment.consignor

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.DynamicAddressFormProvider
import generators.Generators
import models.reference.Country
import models.{DynamicAddress, NormalMode}
import navigation.traderDetails.TraderDetailsNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.traderDetails.consignment.consignor.{AddressPage, CountryPage, NamePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CountriesService
import views.html.traderDetails.consignment.consignor.AddressView

import scala.concurrent.Future

class AddressControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val addressHolderName = Gen.alphaNumStr.sample.value

  private val testAddress = arbitrary[DynamicAddress].sample.value
  private val country     = arbitrary[Country].sample.value

  private val formProvider                        = new DynamicAddressFormProvider()
  private def form(isPostalCodeRequired: Boolean) = formProvider("traderDetails.consignment.consignor.address", isPostalCodeRequired, addressHolderName)

  private val mode              = NormalMode
  private lazy val addressRoute = routes.AddressController.onPageLoad(lrn, mode).url

  private lazy val mockCountriesService: CountriesService = mock[CountriesService]

  override def beforeEach(): Unit = {
    reset(mockCountriesService)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TraderDetailsNavigatorProvider]).toInstance(fakeTraderDetailsNavigatorProvider))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))

  "Consignment Consignor Address Controller" - {

    "must return OK and the correct view for a GET" - {
      "when postcode is required" in {

        val isPostalCodeRequired = true

        when(mockCountriesService.doesCountryRequireZip(any())(any())).thenReturn(Future.successful(isPostalCodeRequired))

        val userAnswers = emptyUserAnswers
          .setValue(NamePage, addressHolderName)
          .setValue(CountryPage, country)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addressRoute)
        val result  = route(app, request).value

        val view = injector.instanceOf[AddressView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(isPostalCodeRequired), lrn, mode, addressHolderName, isPostalCodeRequired)(request, messages).toString
      }
      "when postcode is not required" in {

        val isPostalCodeRequired = false

        when(mockCountriesService.doesCountryRequireZip(any())(any())).thenReturn(Future.successful(isPostalCodeRequired))

        val userAnswers = emptyUserAnswers
          .setValue(NamePage, addressHolderName)
          .setValue(CountryPage, country)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addressRoute)
        val result  = route(app, request).value

        val view = injector.instanceOf[AddressView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(isPostalCodeRequired), lrn, mode, addressHolderName, isPostalCodeRequired)(request, messages).toString
      }

    }

    "must populate the view correctly on a GET when the question has previously been answered" - {

      "when postcode is required" in {

        val isPostalCodeRequired = true
        val testAddress          = arbitrary[DynamicAddress](arbitraryDynamicAddressWithRequiredPostalCode).sample.value

        when(mockCountriesService.doesCountryRequireZip(any())(any())).thenReturn(Future.successful(isPostalCodeRequired))

        val userAnswers = emptyUserAnswers
          .setValue(NamePage, addressHolderName)
          .setValue(CountryPage, country)
          .setValue(AddressPage, testAddress)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addressRoute)

        val result = route(app, request).value

        val filledForm = form(isPostalCodeRequired).bind(
          Map(
            "numberAndStreet" -> testAddress.numberAndStreet,
            "city"            -> testAddress.city,
            "postalCode"      -> testAddress.postalCode.get
          )
        )

        val view = injector.instanceOf[AddressView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, addressHolderName, isPostalCodeRequired)(request, messages).toString

      }

      "when postcode is optional" in {

        val isPostalCodeRequired = false

        when(mockCountriesService.doesCountryRequireZip(any())(any())).thenReturn(Future.successful(isPostalCodeRequired))

        val userAnswers = emptyUserAnswers
          .setValue(NamePage, addressHolderName)
          .setValue(CountryPage, country)
          .setValue(AddressPage, testAddress)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addressRoute)

        val result = route(app, request).value

        val filledForm = form(isPostalCodeRequired).bind(
          Map(
            "numberAndStreet" -> testAddress.numberAndStreet,
            "city"            -> testAddress.city,
            "postalCode"      -> testAddress.postalCode.getOrElse("")
          )
        )

        val view = injector.instanceOf[AddressView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, addressHolderName, isPostalCodeRequired)(request, messages).toString

      }
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockCountriesService.doesCountryRequireZip(any())(any())).thenReturn(Future.successful(false))
      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers
        .setValue(NamePage, addressHolderName)
        .setValue(CountryPage, country)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, addressRoute)
        .withFormUrlEncodedBody(
          ("numberAndStreet", testAddress.numberAndStreet),
          ("city", testAddress.city),
          ("postalCode", testAddress.postalCode.getOrElse(""))
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" - {

      "when postcode is required" in {

        val isPostalCodeRequired = true

        when(mockCountriesService.doesCountryRequireZip(any())(any())).thenReturn(Future.successful(isPostalCodeRequired))

        val userAnswers = emptyUserAnswers
          .setValue(NamePage, addressHolderName)
          .setValue(CountryPage, country)
        setExistingUserAnswers(userAnswers)

        val request   = FakeRequest(POST, addressRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form(isPostalCodeRequired).bind(Map("value" -> ""))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST

        val view = injector.instanceOf[AddressView]

        contentAsString(result) mustEqual
          view(boundForm, lrn, mode, addressHolderName, isPostalCodeRequired)(request, messages).toString

      }

      "when postcode is optional" in {

        val isPostalCodeRequired = false

        when(mockCountriesService.doesCountryRequireZip(any())(any())).thenReturn(Future.successful(isPostalCodeRequired))

        val userAnswers = emptyUserAnswers
          .setValue(NamePage, addressHolderName)
          .setValue(CountryPage, country)
        setExistingUserAnswers(userAnswers)

        val request   = FakeRequest(POST, addressRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form(isPostalCodeRequired).bind(Map("value" -> ""))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST

        val view = injector.instanceOf[AddressView]

        contentAsString(result) mustEqual
          view(boundForm, lrn, mode, addressHolderName, isPostalCodeRequired)(request, messages).toString

      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addressRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addressRoute)
        .withFormUrlEncodedBody(
          ("numberAndStreet", testAddress.numberAndStreet),
          ("city", testAddress.city),
          ("postalCode", testAddress.postalCode.getOrElse(""))
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
