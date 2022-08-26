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

package controllers.routeDetails.transit.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.CustomsOfficeForCountryFormProvider
import generators.Generators
import models.reference.{Country, CustomsOffice}
import models.{reference, CustomsOfficeList, NormalMode}
import navigation.routeDetails.OfficeOfTransitNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.routing.CountryOfDestinationPage
import pages.routeDetails.transit.index.{OfficeOfTransitCountryPage, OfficeOfTransitPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CustomsOfficesService
import views.html.routeDetails.transit.index.OfficeOfTransitView

import scala.concurrent.Future

class OfficeOfTransitControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val customsOffice1    = arbitrary[reference.CustomsOffice].sample.value
  private val customsOffice2    = arbitrary[CustomsOffice].sample.value
  private val customsOfficeList = CustomsOfficeList(Seq(customsOffice1, customsOffice2))
  private val country           = arbitrary[Country].sample.value

  private val formProvider = new CustomsOfficeForCountryFormProvider()
  private val form         = formProvider("routeDetails.transit.officeOfTransit", customsOfficeList, country.description)
  private val mode         = NormalMode

  private val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]
  private lazy val officeOfTransitRoute                        = routes.OfficeOfTransitController.onPageLoad(lrn, mode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[OfficeOfTransitNavigatorProvider]).toInstance(fakeOfficeOfTransitNavigatorProvider))
      .overrides(bind(classOf[CustomsOfficesService]).toInstance(mockCustomsOfficesService))

  "OfficeOfTransit Controller" - {

    "must return OK and the correct view for a GET" - {
      "when country defined at index" in {
        val destinationCountry = arbitrary[Country].sample.value

        val updatedAnswers = emptyUserAnswers
          .setValue(CountryOfDestinationPage, destinationCountry)
          .setValue(OfficeOfTransitCountryPage(index), country)

        when(mockCustomsOfficesService.getCustomsOfficesForCountry(any(), any())(any())).thenReturn(Future.successful(customsOfficeList))
        setExistingUserAnswers(updatedAnswers)

        val request = FakeRequest(GET, officeOfTransitRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[OfficeOfTransitView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, lrn, customsOfficeList.customsOffices, country.description, mode, index)(request, messages).toString
      }

      "when only country of destination defined" in {
        val updatedAnswers = emptyUserAnswers.setValue(CountryOfDestinationPage, country)

        when(mockCustomsOfficesService.getCustomsOfficesForCountry(any(), any())(any())).thenReturn(Future.successful(customsOfficeList))
        setExistingUserAnswers(updatedAnswers)

        val request = FakeRequest(GET, officeOfTransitRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[OfficeOfTransitView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, lrn, customsOfficeList.customsOffices, country.description, mode, index)(request, messages).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" - {
      "when country defined at index" in {
        when(mockCustomsOfficesService.getCustomsOfficesForCountry(any(), any())(any())).thenReturn(Future.successful(customsOfficeList))

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfTransitCountryPage(index), country)
          .setValue(OfficeOfTransitPage(index), customsOffice1)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, officeOfTransitRoute)

        val result = route(app, request).value

        val filledForm = form.bind(Map("value" -> customsOffice1.id))

        val view = injector.instanceOf[OfficeOfTransitView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, customsOfficeList.customsOffices, country.description, mode, index)(request, messages).toString
      }

      "when only country of destination defined" in {
        when(mockCustomsOfficesService.getCustomsOfficesForCountry(any(), any())(any())).thenReturn(Future.successful(customsOfficeList))

        val userAnswers = emptyUserAnswers
          .setValue(CountryOfDestinationPage, country)
          .setValue(OfficeOfTransitPage(index), customsOffice1)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, officeOfTransitRoute)

        val result = route(app, request).value

        val filledForm = form.bind(Map("value" -> customsOffice1.id))

        val view = injector.instanceOf[OfficeOfTransitView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, customsOfficeList.customsOffices, country.description, mode, index)(request, messages).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val updatedAnswers = emptyUserAnswers.setValue(OfficeOfTransitCountryPage(index), country)

      when(mockCustomsOfficesService.getCustomsOfficesForCountry(any(), any())(any())).thenReturn(Future.successful(customsOfficeList))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(updatedAnswers)

      val request = FakeRequest(POST, officeOfTransitRoute)
        .withFormUrlEncodedBody(("value", customsOffice1.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" - {
      "when country defined at index" in {
        val destinationCountry = arbitrary[Country].sample.value

        val updatedAnswers = emptyUserAnswers
          .setValue(CountryOfDestinationPage, destinationCountry)
          .setValue(OfficeOfTransitCountryPage(index), country)

        when(mockCustomsOfficesService.getCustomsOfficesForCountry(any(), any())(any())).thenReturn(Future.successful(customsOfficeList))
        setExistingUserAnswers(updatedAnswers)

        val request   = FakeRequest(POST, officeOfTransitRoute).withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))

        val result = route(app, request).value

        val view = injector.instanceOf[OfficeOfTransitView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, lrn, customsOfficeList.customsOffices, country.description, mode, index)(request, messages).toString
      }

      "when only country of destination defined" in {
        val updatedAnswers = emptyUserAnswers.setValue(CountryOfDestinationPage, country)

        when(mockCustomsOfficesService.getCustomsOfficesForCountry(any(), any())(any())).thenReturn(Future.successful(customsOfficeList))
        setExistingUserAnswers(updatedAnswers)

        val request   = FakeRequest(POST, officeOfTransitRoute).withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))

        val result = route(app, request).value

        val view = injector.instanceOf[OfficeOfTransitView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, lrn, customsOfficeList.customsOffices, country.description, mode, index)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, officeOfTransitRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, officeOfTransitRoute)
        .withFormUrlEncodedBody(("value", customsOffice1.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
