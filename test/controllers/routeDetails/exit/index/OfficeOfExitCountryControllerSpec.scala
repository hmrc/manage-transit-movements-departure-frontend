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

package controllers.routeDetails.exit.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.CountryFormProvider
import generators.Generators
import models.reference.{Country, CountryCode}
import models.{CountryList, CustomsOfficeList, Index, NormalMode}
import navigation.routeDetails.OfficeOfExitNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.exit.index.OfficeOfExitCountryPage
import pages.routeDetails.routing.CountryOfDestinationPage
import pages.routeDetails.routing.index.CountryOfRoutingPage
import play.api.data.FormError
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{CountriesService, CustomsOfficesService}
import views.html.routeDetails.exit.index.OfficeOfExitCountryView

import scala.concurrent.Future

class OfficeOfExitCountryControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val france                 = Country(CountryCode("FR"), "France")
  private val italy                  = Country(CountryCode("IT"), "Italy")
  private val countriesOfRoutingList = CountryList(Seq(france, italy))

  private val austria                = Country(CountryCode("AT"), "Austria")
  private val unitedKingdom          = Country(CountryCode("GB"), "United Kingdom")
  private val countriesReferenceData = CountryList(Seq(austria, unitedKingdom))

  private val secAgreementCountries = CountryList(Seq(austria, italy, france))

  private val formProvider                   = new CountryFormProvider()
  private def form(countryList: CountryList) = formProvider("routeDetails.exit.index.officeOfExitCountry", countryList)
  private val mode                           = NormalMode

  private val mockCountriesService: CountriesService           = mock[CountriesService]
  private val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]

  private lazy val officeOfExitCountryRoute = routes.OfficeOfExitCountryController.onPageLoad(lrn, index, mode).url

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCountriesService); reset(mockCustomsOfficesService)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[OfficeOfExitNavigatorProvider]).toInstance(fakeOfficeOfExitNavigatorProvider))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))
      .overrides(bind(classOf[CustomsOfficesService]).toInstance(mockCustomsOfficesService))

  "OfficeOfExitCountry Controller" - {

    "must return OK and the correct view for a GET when using CountriesOfRouting" in {

      when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(secAgreementCountries))

      val updatedUserAnswers = emptyUserAnswers
        .setValue(CountryOfDestinationPage, austria)
        .setValue(CountryOfRoutingPage(Index(0)), france)
        .setValue(CountryOfRoutingPage(Index(1)), italy)
        .setValue(CountryOfRoutingPage(Index(2)), austria)

      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(GET, officeOfExitCountryRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[OfficeOfExitCountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form(countriesOfRoutingList), lrn, countriesOfRoutingList.countries, index, mode)(request, messages).toString
    }

    "must return OK and the correct view for a GET when using reference data" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countriesReferenceData))

      val updatedUserAnswers = emptyUserAnswers
        .setValue(CountryOfDestinationPage, austria)

      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(GET, officeOfExitCountryRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[OfficeOfExitCountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form(countriesReferenceData), lrn, countriesReferenceData.countries, index, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered using countries of routing" in {

      when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(secAgreementCountries))

      val updatedUserAnswers = emptyUserAnswers
        .setValue(CountryOfDestinationPage, austria)
        .setValue(CountryOfRoutingPage(Index(0)), france)
        .setValue(CountryOfRoutingPage(Index(1)), italy)
        .setValue(OfficeOfExitCountryPage(Index(0)), france)

      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(GET, officeOfExitCountryRoute)

      val result = route(app, request).value

      val filledForm = form(countriesOfRoutingList).bind(Map("value" -> france.code.code))

      val view = injector.instanceOf[OfficeOfExitCountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, countriesOfRoutingList.countries, index, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered using reference data" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countriesReferenceData))

      val updatedUserAnswers = emptyUserAnswers
        .setValue(CountryOfDestinationPage, austria)
        .setValue(OfficeOfExitCountryPage(Index(0)), austria)

      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(GET, officeOfExitCountryRoute)

      val result = route(app, request).value

      val filledForm = form(countriesReferenceData).bind(Map("value" -> austria.code.code))

      val view = injector.instanceOf[OfficeOfExitCountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, countriesReferenceData.countries, index, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(secAgreementCountries))

      when(mockCustomsOfficesService.getCustomsOfficesOfExitForCountry(any())(any()))
        .thenReturn(Future.successful(arbitrary[CustomsOfficeList].sample.value))

      val updatedUserAnswers = emptyUserAnswers
        .setValue(CountryOfDestinationPage, austria)
        .setValue(CountryOfRoutingPage(Index(0)), france)
        .setValue(CountryOfRoutingPage(Index(1)), italy)

      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(POST, officeOfExitCountryRoute)
        .withFormUrlEncodedBody(("value", france.code.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(secAgreementCountries))

      val updatedUserAnswers = emptyUserAnswers
        .setValue(CountryOfDestinationPage, austria)
        .setValue(CountryOfRoutingPage(Index(0)), france)
        .setValue(CountryOfRoutingPage(Index(1)), italy)

      setExistingUserAnswers(updatedUserAnswers)

      val request   = FakeRequest(POST, officeOfExitCountryRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form(countriesOfRoutingList).bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[OfficeOfExitCountryView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, countriesOfRoutingList.countries, index, mode)(request, messages).toString
    }

    "must return a Bad Request and errors when submitted country has no corresponding customs offices" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(secAgreementCountries))

      when(mockCustomsOfficesService.getCustomsOfficesOfExitForCountry(any())(any()))
        .thenReturn(Future.successful(CustomsOfficeList(Nil)))

      val updatedUserAnswers = emptyUserAnswers
        .setValue(CountryOfDestinationPage, austria)
        .setValue(CountryOfRoutingPage(Index(0)), france)
        .setValue(CountryOfRoutingPage(Index(1)), italy)

      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(POST, officeOfExitCountryRoute).withFormUrlEncodedBody(("value", france.code.code))

      val boundForm = form(countriesOfRoutingList)
        .withError(FormError("value", "You cannot use this country as it does not have any offices of exit"))

      val result = route(app, request).value

      val view = injector.instanceOf[OfficeOfExitCountryView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, countriesOfRoutingList.countries, index, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, officeOfExitCountryRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, officeOfExitCountryRoute)
        .withFormUrlEncodedBody(("value", france.code.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
