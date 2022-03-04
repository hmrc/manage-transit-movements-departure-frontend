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

package controllers.transportDetails

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoutes}
import matchers.JsonMatchers
import models.reference.{Country, CountryCode, TransportMode}
import models.{CountryList, LocalReferenceNumber, TransportModeList}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.InlandModePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.{CountriesService, TransportModesService}

import scala.concurrent.Future

class TransportDetailsCheckYourAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with JsonMatchers {

  def onwardRoute(lrn: LocalReferenceNumber) = Call("GET", s"/manage-transit-movements-departures/$lrn/task-list")

  lazy val transportDetailsRoute: String               = routes.TransportDetailsCheckYourAnswersController.onPageLoad(lrn).url
  val mockTransportModesService: TransportModesService = mock[TransportModesService]
  private val mockCountriesService: CountriesService   = mock[CountriesService]
  val transportMode: TransportMode                     = TransportMode("1", "Sea transport")
  val transportModes: TransportModeList                = TransportModeList(Seq(transportMode))
  private val country                                  = Country(CountryCode("GB"), "United Kingdom")
  val countries: CountryList                           = CountryList(Seq(country))

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportModesService]).toInstance(mockTransportModesService))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))

  override def beforeEach(): Unit = {
    reset(mockTransportModesService, mockCountriesService)
    super.beforeEach()
  }

  "TransportDetailsCheckYourAnswers Controller" - {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(InlandModePage, "1").success.value
      setUserAnswers(Some(userAnswers))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockTransportModesService.getTransportModes()(any()))
        .thenReturn(Future.successful(transportModes))

      when(mockCountriesService.getCountries()(any()))
        .thenReturn(Future.successful(countries))

      val request                                = FakeRequest(GET, routes.TransportDetailsCheckYourAnswersController.onPageLoad(lrn).url)
      val templateCaptor: ArgumentCaptor[String] = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor: ArgumentCaptor[JsObject]   = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "lrn"         -> lrn,
        "nextPageUrl" -> mainRoutes.DeclarationSummaryController.onPageLoad(lrn).url
      )

      val jsonCaptorWithoutConfig: JsObject = jsonCaptor.getValue - configKey - "sections"

      templateCaptor.getValue mustEqual "transportDetailsCheckYourAnswers.njk"
      jsonCaptorWithoutConfig mustBe expectedJson
    }
  }
}
