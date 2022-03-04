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
import controllers.{routes => mainRoutes}
import forms.MovementDestinationCountryFormProvider
import matchers.JsonMatchers
import models.DeclarationType._
import models.reference.{Country, CountryCode, CustomsOffice}
import models.userAnswerScenarios.Scenario1.UserAnswersSpecHelperOps
import models.{CountryList, NormalMode}
import navigation.annotations.RouteDetails
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{times, verify, when}
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import pages.routeDetails.MovementDestinationCountryPage
import pages.{DeclarationTypePage, OfficeOfDeparturePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.CountriesService
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class MovementDestinationCountryControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with NunjucksSupport with JsonMatchers {

  val mockCountriesService: CountriesService = mock[CountriesService]

  private val formProvider         = new MovementDestinationCountryFormProvider()
  private val countriesExcludingSM = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom")))
  private val countriesIncludingSM = CountryList(Seq(Country(CountryCode("GB"), "United Kingdom"), Country(CountryCode("SM"), "San Marino")))

  private def form(countries: CountryList) = formProvider(countries)
  private val template                     = "movementDestinationCountry.njk"

  private lazy val movementDestinationCountryRoute = routes.MovementDestinationCountryController.onPageLoad(lrn, NormalMode).url

  def jsonCountryListWithoutSM(preSelected: Boolean): Seq[JsObject] = Seq(
    Json.obj("text" -> "Select", "value"         -> ""),
    Json.obj("text" -> "United Kingdom", "value" -> "GB", "selected" -> preSelected)
  )

  def jsonCountryListWithSM(preSelected: Boolean): Seq[JsObject] = Seq(
    Json.obj("text" -> "Select", "value"         -> ""),
    Json.obj("text" -> "United Kingdom", "value" -> "GB", "selected" -> preSelected),
    Json.obj("text" -> "San Marino", "value"     -> "SM", "selected" -> preSelected)
  )

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[RouteDetails]).toInstance(new FakeNavigator(onwardRoute)))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))

  "MovementDestinationCountry Controller" - {

    "must return OK and the correct view for a GET for NI departure with Declaration Type of Option1 or Option4" in {

      val declarationType = Gen.oneOf(Option1, Option4).sample.value
      val userAnswers = emptyUserAnswers
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))
        .unsafeSetVal(DeclarationTypePage)(declarationType)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getDestinationCountries(any(), any())(any())).thenReturn(Future.successful(countriesExcludingSM))

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, movementDestinationCountryRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      verify(mockCountriesService, times(1))
        .getDestinationCountries(any(), eqTo(alwaysExcludedTransitCountries :+ CountryCode("SM")))(any())

      val expectedJson = Json.obj(
        "form"        -> form(countriesExcludingSM),
        "mode"        -> NormalMode,
        "lrn"         -> lrn,
        "countries"   -> jsonCountryListWithoutSM(preSelected = false),
        "onSubmitUrl" -> routes.MovementDestinationCountryController.onSubmit(lrn, NormalMode).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must return OK and the correct view for a GET for NI departure with Declaration Type of Option2 or Option3" in {

      val declarationType = Gen.oneOf(Option2, Option3).sample.value
      val userAnswers = emptyUserAnswers
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("XI"), None))
        .unsafeSetVal(DeclarationTypePage)(declarationType)

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getDestinationCountries(any(), any())(any())).thenReturn(Future.successful(countriesIncludingSM))

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, movementDestinationCountryRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      verify(mockCountriesService, times(1))
        .getDestinationCountries(any(), eqTo(alwaysExcludedTransitCountries))(any())

      val expectedJson = Json.obj(
        "form"        -> form(countriesIncludingSM),
        "mode"        -> NormalMode,
        "lrn"         -> lrn,
        "countries"   -> jsonCountryListWithSM(preSelected = false),
        "onSubmitUrl" -> routes.MovementDestinationCountryController.onSubmit(lrn, NormalMode).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must return OK and the correct view for a GET for GB departure" in {

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getDestinationCountries(any(), any())(any())).thenReturn(Future.successful(countriesExcludingSM))

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, movementDestinationCountryRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())
      verify(mockCountriesService, times(1))
        .getDestinationCountries(any(), eqTo(alwaysExcludedTransitCountries ++ gbExcludedCountries))(any())

      val expectedJson = Json.obj(
        "form"        -> form(countriesExcludingSM),
        "mode"        -> NormalMode,
        "lrn"         -> lrn,
        "countries"   -> jsonCountryListWithoutSM(preSelected = false),
        "onSubmitUrl" -> routes.MovementDestinationCountryController.onSubmit(lrn, NormalMode).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))
        .unsafeSetVal(MovementDestinationCountryPage)(CountryCode("GB"))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getDestinationCountries(any(), any())(any())).thenReturn(Future.successful(countriesExcludingSM))

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(GET, movementDestinationCountryRoute)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form(countriesExcludingSM).bind(Map("value" -> "GB"))

      val expectedJson = Json.obj(
        "form"        -> filledForm,
        "lrn"         -> lrn,
        "mode"        -> NormalMode,
        "countries"   -> jsonCountryListWithoutSM(true),
        "onSubmitUrl" -> routes.MovementDestinationCountryController.onSubmit(lrn, NormalMode).url
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig mustBe expectedJson

    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      when(mockCountriesService.getDestinationCountries(any(), any())(any())).thenReturn(Future.successful(countriesExcludingSM))

      setUserAnswers(Some(userAnswers))

      val request =
        FakeRequest(POST, movementDestinationCountryRoute)
          .withFormUrlEncodedBody(("value", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(OfficeOfDeparturePage)(CustomsOffice("id", "name", CountryCode("GB"), None))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockCountriesService.getDestinationCountries(any(), eqTo(Seq(CountryCode("JE"))))(any())).thenReturn(Future.successful(countriesExcludingSM))

      setUserAnswers(Some(userAnswers))

      val request                                = FakeRequest(POST, movementDestinationCountryRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm                              = form(countriesExcludingSM).bind(Map("value" -> ""))
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

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, movementDestinationCountryRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, movementDestinationCountryRoute)
          .withFormUrlEncodedBody(("value", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }
  }
}
