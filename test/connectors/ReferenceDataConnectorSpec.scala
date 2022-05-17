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

package connectors

import base.{AppWithDefaultMockFixtures, SpecBase}
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, okJson, urlEqualTo}
import helper.WireMockServerHandler
import models.CustomsOfficeList
import models.reference._
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder
import play.mvc.Http.HeaderNames.CONTENT_TYPE
import play.mvc.Http.MimeTypes.JSON
import play.mvc.Http.Status._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends SpecBase with AppWithDefaultMockFixtures with WireMockServerHandler with ScalaCheckPropertyChecks {

  private val baseUrl = "transit-movements-trader-reference-data"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      conf = "microservice.services.referenceData.port" -> server.port()
    )

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  private val customsOfficeResponseJson: String =
    """
      |[
      | {
      |   "id" : "testId1",
      |   "name" : "testName1",
      |   "roles" : ["role1", "role2"],
      |   "countryId" : "GB",
      |   "phoneNumber" : "testPhoneNumber"
      | },
      | {
      |   "id" : "testId2",
      |   "name" : "testName2",
      |   "countryId" : "GB",
      |   "roles" : ["role1", "role2"]
      | }
      |]
      |""".stripMargin

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

  private val customsOfficeJson: String =
    """
      |  {
      |    "id": "1",
      |    "name": "Data1",
      |    "roles" : ["role1", "role2"],
      |    "countryId" : "GB",
      |    "phoneNumber" : "testPhoneNumber"
      |  }
      |""".stripMargin

  "Reference Data" - {

    "getCustomsOffices" - {
      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/customs-offices"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult = Seq(
          CustomsOffice("testId1", "testName1", CountryCode("GB"), Some("testPhoneNumber")),
          CustomsOffice("testId2", "testName2", CountryCode("GB"), None)
        )

        connector.getCustomsOffices().futureValue mustBe expectedResult
      }

      "must return a successful future response with roles with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/customs-offices?role=NPM"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult = Seq(
          CustomsOffice("testId1", "testName1", CountryCode("GB"), Some("testPhoneNumber")),
          CustomsOffice("testId2", "testName2", CountryCode("GB"), None)
        )

        connector.getCustomsOffices(Seq("NPM")).futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/customs-offices", connector.getCustomsOffices())
      }
    }

    "getCustomsOfficesOfTheCountry" - {
      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/customs-offices/GB"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult =
          CustomsOfficeList(
            Seq(
              CustomsOffice("testId1", "testName1", CountryCode("GB"), Some("testPhoneNumber")),
              CustomsOffice("testId2", "testName2", CountryCode("GB"), None)
            )
          )

        connector.getCustomsOfficesForCountry(CountryCode("GB")).futureValue mustBe expectedResult
      }

      "must return a successful future response when roles are defined with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/customs-offices/GB?role=TRA"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult =
          CustomsOfficeList(
            Seq(
              CustomsOffice("testId1", "testName1", CountryCode("GB"), Some("testPhoneNumber")),
              CustomsOffice("testId2", "testName2", CountryCode("GB"), None)
            )
          )

        connector.getCustomsOfficesForCountry(CountryCode("GB"), Seq("TRA")).futureValue mustBe expectedResult
      }

      "must return a successful future response when CustomsOffice is not found" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/customs-offices/AR?role=TRA")).willReturn(
            aResponse()
              .withStatus(NOT_FOUND)
              .withHeader(CONTENT_TYPE, JSON)
          )
        )

        val expectedResult =
          CustomsOfficeList(Nil)

        connector.getCustomsOfficesForCountry(CountryCode("AR"), Seq("TRA")).futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/customs-offices/GB", connector.getCustomsOfficesForCountry(CountryCode("GB")))
      }
    }

    "getCountries" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/countries?customsOfficeRole=ANY&exclude=IT&exclude=DE&membership=ctc"))
            .willReturn(okJson(countriesResponseJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        val queryParameters = Seq(
          "customsOfficeRole" -> "ANY",
          "exclude"           -> "IT",
          "exclude"           -> "DE",
          "membership"        -> "ctc"
        )

        connector.getCountries(queryParameters).futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$baseUrl/countries?customsOfficeRole=ANY", connector.getCountries(Nil))
      }
    }

    "getCustomsOffice" - {

      "must return a Customs Office when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$baseUrl/customs-office/1"))
            .willReturn(okJson(customsOfficeJson))
        )

        val expectedResult: CustomsOffice = CustomsOffice("1", "Data1", CountryCode("GB"), Some("testPhoneNumber"))

        connector.getCustomsOffice("1").futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {

        checkErrorResponse(s"/$baseUrl/customs-office/1", connector.getCustomsOffice("1"))
      }
    }
  }

  private def checkErrorResponse(url: String, result: => Future[_]): Assertion = {
    val errorResponses: Gen[Int] = Gen
      .chooseNum(400: Int, 599: Int)
      .suchThat(_ != 404)

    forAll(errorResponses) {
      errorResponse =>
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(errorResponse)
            )
        )

        whenReady(result.failed) {
          _ mustBe an[Exception]
        }
    }
  }

}
