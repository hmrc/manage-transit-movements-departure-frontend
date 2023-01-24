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

package connectors

import base.{AppWithDefaultMockFixtures, SpecBase}
import com.github.tomakehurst.wiremock.client.WireMock._
import generators.Generators
import helper.WireMockServerHandler
import models.{CountryList, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import services.CountriesService
import play.api.inject.bind
import uk.gov.hmrc.http.{BadRequestException, HttpResponse, UpstreamErrorResponse}

import scala.concurrent.Future

class ApiConnectorSpec extends SpecBase with AppWithDefaultMockFixtures with WireMockServerHandler with Generators {

  val preTask: UserAnswers                   = arbitraryPreTaskListAnswers(emptyUserAnswers).sample.value
  val traderDetails: UserAnswers             = arbitraryTraderDetailsAnswers(preTask).sample.value
  val uA: UserAnswers                        = arbitraryRouteDetailsAnswers(traderDetails).sample.value
  val mockCountriesService: CountriesService = mock[CountriesService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[CountriesService].toInstance(mockCountriesService))
      .configure(conf = "microservice.services.common-transit-convention-traders.port" -> server.port())

  private lazy val connector: ApiConnector = app.injector.instanceOf[ApiConnector]

  val departureId: String = "someid"

  private val countriesResponseJson: String =
    """
      |[
      | {
      |   "code":"GB",
      |   "description":"United Kingdom"
      | },
      | {
      |   "code":"AD",
      |   "description":"Andorra"
      | }
      |]
      |""".stripMargin

  val expected: String = Json
    .obj(
      "_links" -> Json.obj(
        "self" -> Json.obj(
          "href" -> s"/customs/transits/movements/departures/$departureId"
        ),
        "messages" -> Json.obj(
          "href" -> s"/customs/transits/movements/departures/$departureId/messages"
        )
      )
    )
    .toString()
    .stripMargin

  val uri = "/movements/departures"

  "ApiConnector" - {

    "submitDeclaration is called" - {

      "for success" in {

        server.stubFor(post(urlEqualTo(uri)).willReturn(okJson(expected)))

        when(mockCountriesService.getCountryCodesCTC()(any()))
          .thenReturn(Future.successful(CountryList(ctcCountries)))
        when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any()))
          .thenReturn(Future.successful(CountryList(customsSecurityAgreementAreaCountries)))

        val res: HttpResponse = await(connector.submitDeclaration(uA))
        res.status mustBe OK

      }

      "for bad request" in {

        server.stubFor(post(urlEqualTo(uri)).willReturn(badRequest()))

        when(mockCountriesService.getCountryCodesCTC()(any()))
          .thenReturn(Future.successful(CountryList(ctcCountries)))
        when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any()))
          .thenReturn(Future.successful(CountryList(customsSecurityAgreementAreaCountries)))

        intercept[BadRequestException] {
          await(connector.submitDeclaration(uA))
        }

      }

      "for internal server error" in {

        server.stubFor(post(urlEqualTo(uri)).willReturn(serverError()))

        when(mockCountriesService.getCountryCodesCTC()(any()))
          .thenReturn(Future.successful(CountryList(ctcCountries)))
        when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any()))
          .thenReturn(Future.successful(CountryList(customsSecurityAgreementAreaCountries)))

        intercept[UpstreamErrorResponse] {
          await(connector.submitDeclaration(uA))
        }

      }

    }

  }

}
