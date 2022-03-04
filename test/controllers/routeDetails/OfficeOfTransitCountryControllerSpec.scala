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

package controllers.routeDetails

import base.{AppWithDefaultMockFixtures, SpecBase}
import commonTestUtils.UserAnswersSpecHelper
import controllers.{routes => mainRoutes}
import forms.OfficeOfTransitCountryFormProvider
import matchers.JsonMatchers
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{CountryList, CustomsOfficeList, NormalMode}
import navigation.annotations.RouteDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.OfficeOfDeparturePage
import pages.routeDetails.OfficeOfTransitCountryPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.{CountriesService, CustomsOfficesService}
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class OfficeOfTransitCountryControllerSpec
    extends SpecBase
    with AppWithDefaultMockFixtures
    with MockitoSugar
    with NunjucksSupport
    with JsonMatchers
    with UserAnswersSpecHelper {

  val mockCountriesService: CountriesService           = mock[CountriesService]
  val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]

  private val countries                         = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  private val formProvider                      = new OfficeOfTransitCountryFormProvider()
  private val form                              = formProvider(countries)
  private val template                          = "officeOfTransitCountry.njk"
  private val customsOffice1: CustomsOffice     = CustomsOffice("officeId", "someName", CountryCode("GB"), None)
  private val customsOffice2: CustomsOffice     = CustomsOffice("id", "name", CountryCode("JE"), None)
  private val customsOffices: CustomsOfficeList = CustomsOfficeList(Seq(customsOffice1, customsOffice2))

  private lazy val officeOfTransitCountryRoute = routes.OfficeOfTransitCountryController.onPageLoad(lrn, index, NormalMode).url

  def jsonCountryList(preSelected: Boolean): Seq[JsObject] = Seq(
    Json.obj("text" -> "Select", "value"         -> ""),
    Json.obj("text" -> "United Kingdom", "value" -> "GB", "selected" -> preSelected)
  )

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[RouteDetails]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))
      .overrides(bind(classOf[CustomsOfficesService]).toInstance(mockCustomsOfficesService))

  "OfficeOfTransitCountry Controller" - {

    "must return OK and the correct view for a GET when office of departure is defined as a XI customs office" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getCountriesWithCustomsOffices(any(), any())(any())).thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers.unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, officeOfTransitCountryRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      verify(mockCountriesService, times(1))
        .getCountriesWithCustomsOffices(eqTo(alwaysExcludedTransitCountries), any())(any())

      val expectedJson = Json.obj(
        "form"        -> form,
        "index"       -> index.display,
        "mode"        -> NormalMode,
        "lrn"         -> lrn,
        "countries"   -> jsonCountryList(preSelected = false),
        "onSubmitUrl" -> routes.OfficeOfTransitCountryController.onSubmit(lrn, index, NormalMode).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must return OK and the correct view for a GET when office of departure is defined as a non XI customs office" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getCountriesWithCustomsOffices(any(), any())(any()))
        .thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers.unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, officeOfTransitCountryRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      verify(mockCountriesService, times(1))
        .getCountriesWithCustomsOffices(eqTo(alwaysExcludedTransitCountries ++ gbExcludedCountries), any())(any())

      val expectedJson = Json.obj(
        "form"        -> form,
        "index"       -> index.display,
        "mode"        -> NormalMode,
        "lrn"         -> lrn,
        "countries"   -> jsonCountryList(preSelected = false),
        "onSubmitUrl" -> routes.OfficeOfTransitCountryController.onSubmit(lrn, index, NormalMode).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to session expired for a GET when office of departure is not defined" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getCountriesWithCustomsOffices(any(), any())(any())).thenReturn(Future.successful(countries))

      setUserAnswers(Some(emptyUserAnswers))

      val request = FakeRequest(GET, officeOfTransitCountryRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must populate the view correctly on a GET when the question has previously been answered and office of departure is defined" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getCountriesWithCustomsOffices(any(), any())(any())).thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
        .unsafeSetVal(OfficeOfTransitCountryPage(index))(CountryCode("GB"))

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, officeOfTransitCountryRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "GB"))

      val expectedJson = Json.obj(
        "form"        -> filledForm,
        "index"       -> index.display,
        "lrn"         -> lrn,
        "mode"        -> NormalMode,
        "countries"   -> jsonCountryList(true),
        "onSubmitUrl" -> routes.OfficeOfTransitCountryController.onSubmit(lrn, index, NormalMode).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockCustomsOfficesService.getCustomsOfficesForCountry(any(), eqTo(Seq("TRA")))(any()))
        .thenReturn(Future.successful(customsOffices))
      when(mockCountriesService.getCountriesWithCustomsOffices(eqTo(Seq(CountryCode("JE"))), eqTo(None))(any())).thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers.unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))

      setUserAnswers(Some(userAnswers))

      val request =
        FakeRequest(POST, officeOfTransitCountryRoute)
          .withFormUrlEncodedBody(("value", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must redirect to session expired for a POST when office of departure is not defined " in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      when(mockCountriesService.getCountriesWithCustomsOffices(eqTo(Seq(CountryCode("JE"))), eqTo(None))(any())).thenReturn(Future.successful(countries))

      setUserAnswers(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, officeOfTransitCountryRoute)
          .withFormUrlEncodedBody(("value", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getCountriesWithCustomsOffices(eqTo(Seq(CountryCode("JE"))), eqTo(None))(any())).thenReturn(Future.successful(countries))

      val userAnswers = emptyUserAnswers.unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(POST, officeOfTransitCountryRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"  -> boundForm,
        "index" -> index.display,
        "lrn"   -> lrn,
        "mode"  -> NormalMode
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, officeOfTransitCountryRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, officeOfTransitCountryRoute)
          .withFormUrlEncodedBody(("value", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }
  }
}
