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
import helper.WireMockServerHandler
import models.reference._
import org.scalacheck.Gen
import org.scalatest.{Assertion, BeforeAndAfterEach}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder
import play.mvc.Http.HeaderNames.CONTENT_TYPE
import play.mvc.Http.MimeTypes.JSON
import play.mvc.Http.Status._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec
    extends SpecBase
    with AppWithDefaultMockFixtures
    with WireMockServerHandler
    with ScalaCheckPropertyChecks
    with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  private val baseUrl = "customs-reference-data/test-only"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      conf = "microservice.services.customsReferenceData.port" -> server.port()
    )

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  private val customsOfficeDestinationResponseJson: String =
    """
      {
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/CustomsOffices"
      |    }
      |  },
      |  "meta": {
      |    "version": "410157ad-bc37-4e71-af2a-404d1ddad94c",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "CustomsOffices",
      |  "data": [
      |    {
      |      "state": "valid",
      |      "activeFrom": "2019-01-01",
      |      "id": "GB1",
      |      "name": "testName1",
      |      "LanguageCode": "EN",
      |      "countryId": "GB",
      |      "eMailAddress": "foo@andorra.ad",
      |      "roles": [
      |        {
      |          "role": "DEP"
      |        }
      |      ]
      |    },
      |    {
      |      "state": "valid",
      |      "activeFrom": "2019-01-01",
      |      "id": "GB2",
      |      "name": "testName2",
      |      "LanguageCode": "ES",
      |      "countryId": "GB",
      |      "roles": [
      |        {
      |          "role": "DEP"
      |        }
      |      ]
      |    }
      |  ]
      |}
      |""".stripMargin

  private val countriesResponseCTJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/CountryCodesCTC"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "CountryCodesCTC",
      |  "data": [
      |    {
      |      "activeFrom": "2023-01-23",
      |      "code": "GB",
      |      "state": "valid",
      |      "description": "United Kingdom"
      |    },
      |    {
      |      "activeFrom": "2023-01-23",
      |      "code": "AD",
      |      "state": "valid",
      |      "description": "Andorra"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val countriesResponseAAJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/CountryCustomsSecurityAgreementArea"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "CountryCustomsSecurityAgreementArea",
      |  "data": [
      |    {
      |      "activeFrom": "2023-01-23",
      |      "code": "GB",
      |      "state": "valid",
      |      "description": "United Kingdom"
      |    },
      |    {
      |      "activeFrom": "2023-01-23",
      |      "code": "AD",
      |      "state": "valid",
      |      "description": "Andorra"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val countriesResponseCommunity: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/CountryCodesCommunity"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "CountryCodesCommunity",
      |  "data": [
      |    {
      |      "activeFrom": "2023-01-23",
      |      "code": "GB",
      |      "state": "valid",
      |      "description": "United Kingdom"
      |    },
      |    {
      |      "activeFrom": "2023-01-23",
      |      "code": "AD",
      |      "state": "valid",
      |      "description": "Andorra"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val securityTypesResponseJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/DeclarationTypeSecurity"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "DeclarationTypeSecurity",
      |  "data": [
      |    {
      |      "code": "2",
      |      "description": "EXS"
      |    },
      |    {
      |      "code": "3",
      |      "description": "ENS &amp; EXS"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val declarationTypesResponseJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/DeclarationType"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "DeclarationType",
      |  "data": [
      |    {
      |      "code": "T2",
      |      "description": "Goods having the customs status of Union goods, which are placed under the common transit procedure"
      |    },
      |    {
      |      "code": "TIR",
      |      "description": "TIR carnet"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val additionalDeclarationTypesResponseJson: String =
    """
      |{
      |  "_links": {
      |    "self": {
      |      "href": "/customs-reference-data/lists/DeclarationType"
      |    }
      |  },
      |  "meta": {
      |    "version": "fb16648c-ea06-431e-bbf6-483dc9ebed6e",
      |    "snapshotDate": "2023-01-01"
      |  },
      |  "id": "DeclarationType",
      |  "data": [
      |    {
      |      "code": "A",
      |      "description": "for a standard customs declaration (under Article 162 of the Code)"
      |    },
      |    {
      |      "code": "D",
      |      "description": "For lodging a standard customs declaration (such as referred to under code A) in accordance with Article 171 of the Code."
      |    }
      |  ]
      |}
      |""".stripMargin

  "Reference Data" - {

    "getCustomsOfficesOfDepartureForCountry" - {
      def url(countryId: String) = s"/$baseUrl/filtered-lists/CustomsOffices?data.countryId=$countryId&data.roles.role=DEP"

      "must return a successful future response with a sequence of CustomsOffices" in {
        val countryId = "GB"

        server.stubFor(
          get(urlEqualTo(url(countryId)))
            .willReturn(okJson(customsOfficeDestinationResponseJson))
        )

        val expectedResult = Seq(
          CustomsOffice("GB1", "testName1", None),
          CustomsOffice("GB2", "testName2", None)
        )

        connector.getCustomsOfficesOfDepartureForCountry(countryId).futureValue mustBe expectedResult
      }

      "must return a successful future response when CustomsOffice returns no data" in {
        val countryId = "AR"

        server.stubFor(
          get(urlEqualTo(url(countryId)))
            .willReturn(
              aResponse()
                .withStatus(NO_CONTENT)
                .withHeader(CONTENT_TYPE, JSON)
            )
        )

        val expectedResult = Nil

        connector.getCustomsOfficesOfDepartureForCountry(countryId).futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        val countryId = "GB"

        server.stubFor(
          get(urlEqualTo(url(countryId)))
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        checkErrorResponse(s"/$baseUrl/filtered-lists/CustomsOffices", connector.getCustomsOfficesOfDepartureForCountry(countryId))
      }

      "must return an exception when NOT_FOUND" in {
        val countryId = "GB"

        server.stubFor(
          get(urlEqualTo(url(countryId)))
            .willReturn(aResponse().withStatus(NOT_FOUND))
        )

        checkErrorResponse(s"/$baseUrl/filtered-lists/CustomsOffices", connector.getCustomsOfficesOfDepartureForCountry(countryId))
      }
    }

    "getCountryCodesCTC" - {
      val url = s"/$baseUrl/lists/CountryCodesCTC"

      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(countriesResponseCTJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country("GB"),
          Country("AD")
        )

        connector.getCountryCodesCTC().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCountryCodesCTC())
      }
    }

    "getCustomsSecurityAgreementAreaCountries" - {
      val url = s"/$baseUrl/lists/CountryCustomsSecurityAgreementArea"

      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(countriesResponseAAJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country("GB"),
          Country("AD")
        )

        connector.getCustomsSecurityAgreementAreaCountries().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCustomsSecurityAgreementAreaCountries())
      }
    }

    "getCountries" - {
      val url = s"/$baseUrl/lists/CountryCodesCommunity"

      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(countriesResponseCommunity))
        )

        val expectedResult: Seq[Country] = Seq(
          Country("GB"),
          Country("AD")
        )

        connector.getCountries().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCountries())
      }
    }

    "getSecurityTypes" - {
      val url = s"/$baseUrl/lists/DeclarationTypeSecurity"

      "must return Seq of security types when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(securityTypesResponseJson))
        )

        val expectedResult: Seq[SecurityType] = Seq(
          SecurityType("2", "EXS"),
          SecurityType("3", "ENS &amp; EXS")
        )

        connector.getSecurityTypes().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getSecurityTypes())
      }
    }

    "getDeclarationTypes" - {
      val url = s"/$baseUrl/lists/DeclarationType"

      "must return Seq of declaration types when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(declarationTypesResponseJson))
        )

        val expectedResult: Seq[DeclarationType] = Seq(
          DeclarationType("T2", "Goods having the customs status of Union goods, which are placed under the common transit procedure"),
          DeclarationType("TIR", "TIR carnet")
        )

        connector.getDeclarationTypes().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getDeclarationTypes())
      }
    }

    "getAdditionalDeclarationTypes" - {
      val url = s"/$baseUrl/lists/DeclarationTypeAdditional"

      "must return Seq of additional declaration types when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(additionalDeclarationTypesResponseJson))
        )

        val expectedResult: Seq[AdditionalDeclarationType] = Seq(
          AdditionalDeclarationType(
            "A",
            "for a standard customs declaration (under Article 162 of the Code)"
          ),
          AdditionalDeclarationType(
            "D",
            "For lodging a standard customs declaration (such as referred to under code A) in accordance with Article 171 of the Code."
          )
        )

        connector.getAdditionalDeclarationTypes().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getAdditionalDeclarationTypes())
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
