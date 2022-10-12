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

package controllers.traderDetails.holderOfTransit

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.DynamicAddressFormProvider
import generators.Generators
import models.{CountryList, DynamicAddress, NormalMode}
import navigation.traderDetails.TraderDetailsNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.traderDetails.holderOfTransit.{AddressPage, NamePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CountriesService
import views.html.traderDetails.holderOfTransit.AddressView

import scala.concurrent.Future

class AddressControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val addressHolderName = Gen.alphaNumStr.sample.value

  private val testAddress = arbitrary[DynamicAddress].sample.value

  private val isPostalCodeRequired = false

  private val formProvider = new DynamicAddressFormProvider()
  private val form         = formProvider("traderDetails.holderOfTransit.address", addressHolderName, isPostalCodeRequired)

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

  "Address Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))

      val userAnswers = emptyUserAnswers.setValue(NamePage, addressHolderName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, addressRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[AddressView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, addressHolderName, isPostalCodeRequired)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))

      val userAnswers = emptyUserAnswers
        .setValue(NamePage, addressHolderName)
        .setValue(AddressPage, testAddress)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, addressRoute)

      val result = route(app, request).value

      val filledForm = form.bind(
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

    "must redirect to the next page when valid data is submitted" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))
      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers.setValue(NamePage, addressHolderName)
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

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))

      val userAnswers = emptyUserAnswers.setValue(NamePage, addressHolderName)
      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, addressRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[AddressView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, addressHolderName, isPostalCodeRequired)(request, messages).toString

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
