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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import config.FrontendAppConfig
import controllers.{routes => mainRoutes}
import forms.OfficeOfDepartureFormProvider
import matchers.JsonMatchers
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{CountryList, CustomsOfficeList, NormalMode}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.OfficeOfDeparturePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.{CountriesService, CustomsOfficesService}
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class OfficeOfDepartureControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  val customsOffice1: CustomsOffice          = CustomsOffice("officeId", "someName", CountryCode("GB"), None)
  val customsOffice2: CustomsOffice          = CustomsOffice("id", "name", CountryCode("GB"), None)
  val xiCustomsOffice1: CustomsOffice        = CustomsOffice("xi", "ni", CountryCode("XI"), None)
  val customsOffices: CustomsOfficeList      = CustomsOfficeList(Seq(customsOffice1, customsOffice2))
  val xiCustomsOffices: CustomsOfficeList    = CustomsOfficeList(Seq(xiCustomsOffice1))
  val nonEuTransitCountries: Seq[Country]    = Seq(Country(CountryCode("GB"), "description"))
  val nonEuTransitCountriesList: CountryList = CountryList(nonEuTransitCountries)
  val gbForm                                 = new OfficeOfDepartureFormProvider()(customsOffices)
  val xiForm                                 = new OfficeOfDepartureFormProvider()(xiCustomsOffices)

  private val mockFrontendAppConfig: FrontendAppConfig         = mock[FrontendAppConfig]
  private val mockCountriesService: CountriesService           = mock[CountriesService]
  private val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]
  lazy val officeOfDepartureRoute: String                      = routes.OfficeOfDepartureController.onPageLoad(lrn, NormalMode).url

  override def beforeEach(): Unit = {
    reset(mockFrontendAppConfig, mockCountriesService, mockCustomsOfficesService)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[PreTaskListDetails]).toInstance(fakeNavigator))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))
      .overrides(bind(classOf[CustomsOfficesService]).toInstance(mockCustomsOfficesService))

  "OfficeOfDeparture Controller" - {

    "must return OK and the correct view for a GET" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))

      val request                                = FakeRequest(GET, officeOfDepartureRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedCustomsOfficeJson = Seq(
        Json.obj("value" -> "", "text"         -> "Select"),
        Json.obj("value" -> "officeId", "text" -> "someName (officeId)", "selected" -> false),
        Json.obj("value" -> "id", "text"       -> "name (id)", "selected"           -> false)
      )

      val expectedJson = Json.obj(
        "form"           -> gbForm,
        "mode"           -> NormalMode,
        "lrn"            -> lrn,
        "customsOffices" -> expectedCustomsOfficeJson
      )

      templateCaptor.getValue mustEqual "officeOfDeparture.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers
        .set(OfficeOfDeparturePage, customsOffice1)
        .success
        .value
      setUserAnswers(Some(userAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))

      val request                                = FakeRequest(GET, officeOfDepartureRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = gbForm.bind(Map("value" -> "officeId"))

      val expectedCustomsOfficeJson = Seq(
        Json.obj("value" -> "", "text"         -> "Select"),
        Json.obj("value" -> "officeId", "text" -> "someName (officeId)", "selected" -> true),
        Json.obj("value" -> "id", "text"       -> "name (id)", "selected"           -> false)
      )

      val expectedJson = Json.obj(
        "form"           -> filledForm,
        "lrn"            -> lrn,
        "mode"           -> NormalMode,
        "customsOffices" -> expectedCustomsOfficeJson
      )

      templateCaptor.getValue mustEqual "officeOfDeparture.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to the next page when valid data is submitted" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))
      when(mockCountriesService.getNonEuTransitCountries()(any())).thenReturn(Future.successful(nonEuTransitCountriesList))

      val request =
        FakeRequest(POST, officeOfDepartureRoute)
          .withFormUrlEncodedBody(("value", "id"))

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setUserAnswers(Some(emptyUserAnswers))
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))

      val request                                = FakeRequest(POST, officeOfDepartureRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = gbForm.bind(Map("value" -> ""))
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form" -> boundForm,
        "lrn"  -> lrn,
        "mode" -> NormalMode
      )

      templateCaptor.getValue mustEqual "officeOfDeparture.njk"
      jsonCaptor.getValue must containJson(expectedJson)
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setUserAnswers(None)

      val request = FakeRequest(GET, officeOfDepartureRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setUserAnswers(None)

      val request =
        FakeRequest(POST, officeOfDepartureRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
