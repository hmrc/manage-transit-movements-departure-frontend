/*
 * Copyright 2024 HM Revenue & Customs
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

import cats.data.NonEmptySet
import com.github.tomakehurst.wiremock.client.WireMock.*
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import helpers.{ItSpecBase, WireMockServerHandler}
import models.reference.*
import org.scalacheck.Gen
import org.scalatest.{Assertion, EitherValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends ItSpecBase with WireMockServerHandler with ScalaCheckPropertyChecks with EitherValues {

  private val baseUrl = "customs-reference-data/test-only"

  private val phase6App: GuiceApplicationBuilder => GuiceApplicationBuilder = _ => guiceApplicationBuilder().configure("feature-flags.phase-6-enabled" -> true)
  private val phase5App: GuiceApplicationBuilder => GuiceApplicationBuilder = _ => guiceApplicationBuilder().configure("feature-flags.phase-6-enabled" -> false)

  override def guiceApplicationBuilder(): GuiceApplicationBuilder = super
    .guiceApplicationBuilder()
    .configure(
      conf = "microservice.services.customs-reference-data.port" -> server.port()
    )

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  private val phase5emptyResponseJson: String =
    """
      |{
      |  "data": []
      |}
      |""".stripMargin

  private val phase6emptyResponseJson: String =
    """
      |
      | []
      |
      |""".stripMargin

  "Reference Data" - {

    "getCustomsOfficesOfDepartureForCountry" - {
      "when phase 5" - {

        val json: String =
          """
            |{
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
            |      "languageCode": "EN",
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
            |      "languageCode": "ES",
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

        "must return a successful future response with a sequence of CustomsOffices" in {
          val countryIds = Seq("GB", "XI")
          val url        = s"/$baseUrl/lists/CustomsOffices?data.countryId=GB&data.countryId=XI&data.roles.role=DEP"

          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(json))
              )

              val expectedResult = NonEmptySet.of(
                CustomsOffice("GB1", "testName1", None, "GB", "EN"),
                CustomsOffice("GB2", "testName2", None, "GB", "EN")
              )

              connector.getCustomsOfficesOfDepartureForCountry(countryIds*).futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val countryId = "AR"
              val url       = s"/$baseUrl/lists/CustomsOffices?data.countryId=AR&data.roles.role=DEP"
              checkNoReferenceDataFoundResponse(url, phase5emptyResponseJson, connector.getCustomsOfficesOfDepartureForCountry(countryId))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val countryId = "GB"
              val url       = s"/$baseUrl/lists/CustomsOffices?data.countryId=GB&data.roles.role=DEP"
              checkErrorResponse(url, connector.getCustomsOfficesOfDepartureForCountry(countryId))
          }
        }
      }

      "when phase 6" - {

        val json: String =
          """
            |[
            |  {
            |    "customsOfficeLsd": {
            |      "languageCode": "EN",
            |      "customsOfficeUsualName": "Glasgow Airport"
            |    },
            |    "phoneNumber": "+44(0)300 106 3520",
            |    "referenceNumber": "GB000054",
            |    "countryCode": "GB"
            |  },
            |  {
            |    "customsOfficeLsd": {
            |      "languageCode": "EN",
            |      "customsOfficeUsualName": "Belfast International Airport"
            |    },
            |    "phoneNumber": "+44 (0)3000 575 988",
            |    "referenceNumber": "XI000014",
            |    "countryCode": "XI"
            |  }
            |]
            |""".stripMargin

        "must return a successful future response with a sequence of CustomsOffices" in {
          val countryIds = Seq("GB", "XI")
          val url        = s"/$baseUrl/lists/CustomsOffices?countryCodes=GB&countryCodes=XI&roles=DEP"

          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(json))
              )

              val expectedResult = NonEmptySet.of(
                CustomsOffice("GB000054", "Glasgow Airport", Some("+44(0)300 106 3520"), "GB", "EN"),
                CustomsOffice("XI000014", "Belfast International Airport", Some("+44 (0)3000 575 988"), "XI", "EN")
              )

              connector.getCustomsOfficesOfDepartureForCountry(countryIds*).futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val countryId = "AR"
              val url       = s"/$baseUrl/lists/CustomsOffices?countryCodes=AR&roles=DEP"
              checkNoReferenceDataFoundResponse(url, phase6emptyResponseJson, connector.getCustomsOfficesOfDepartureForCountry(countryId))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val countryId = "GB"
              val url       = s"/$baseUrl/lists/CustomsOffices?countryCodes=GB&roles=DEP"
              checkErrorResponse(url, connector.getCustomsOfficesOfDepartureForCountry(countryId))
          }
        }
      }
    }

    "getCountryCodesCTCCountry" - {
      "when phase-6" - {
        def phase6Url(countryId: String) = s"/$baseUrl/lists/CountryCodesCTC?keys=$countryId"

        def countriesResponseCTJson: String =
          """
            |[
            |    {
            |      "key": "GB",
            |      "value": "United Kingdom"
            |    }
            |  ]
            |""".stripMargin
        "must return Seq of Country when successful" in {
          val countryId = "GB"
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(phase6Url(countryId)))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(countriesResponseCTJson))
              )

              val expectedResult = Country(countryId)

              connector.getCountryCodesCTCCountry(countryId).futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          val countryId = "AD"
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(phase6Url(countryId), phase6emptyResponseJson, connector.getCountryCodesCTCCountry(countryId))
          }

        }

        "must return an exception when an error response is returned" in {
          val countryId = "AD"
          checkErrorResponse(phase6Url(countryId), connector.getCountryCodesCTCCountry(countryId))
        }
      }
      "when phase-5" - {
        def phase5Url(countryId: String) = s"/$baseUrl/lists/CountryCodesCTC?data.code=$countryId"

        def countriesResponseCTJson: String =
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
            |    }
            |  ]
            |}
            |""".stripMargin
        "must return Seq of Country when successful" in {
          val countryId = "GB"
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(phase5Url(countryId)))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(countriesResponseCTJson))
              )

              val expectedResult = Country(countryId)

              connector.getCountryCodesCTCCountry(countryId).futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          val countryId = "AD"
          checkNoReferenceDataFoundResponse(phase5Url(countryId), phase5emptyResponseJson, connector.getCountryCodesCTCCountry(countryId))
        }

        "must return an exception when an error response is returned" in {
          val countryId = "AD"
          checkErrorResponse(phase5Url(countryId), connector.getCountryCodesCTCCountry(countryId))
        }
      }

    }

    "getCountryCustomsSecurityAgreementAreaCountry" - {
      "when phase-6" - {
        def phase6Url(countryId: String) = s"/$baseUrl/lists/CountryCustomsSecurityAgreementArea?keys=$countryId"
        def countriesResponseAAJson: String =
          """
            |[
            |    {
            |      "key": "GB",
            |      "value": "United Kingdom"
            |    }
            |  ]
            |""".stripMargin
        "must return Seq of Country when successful" in {
          val countryId = "GB"
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(phase6Url(countryId)))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(countriesResponseAAJson))
              )

              val expectedResult = Country(countryId)

              connector.getCountryCustomsSecurityAgreementAreaCountry(countryId).futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          val countryId = "AD"
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(phase6Url(countryId),
                                                phase6emptyResponseJson,
                                                connector.getCountryCustomsSecurityAgreementAreaCountry(countryId)
              )
          }

        }

        "must return an exception when an error response is returned" in {
          val countryId = "AD"
          checkErrorResponse(phase6Url(countryId), connector.getCountryCustomsSecurityAgreementAreaCountry(countryId))
        }
      }
      "when phase-5" - {
        def phase5Url(countryId: String) = s"/$baseUrl/lists/CountryCustomsSecurityAgreementArea?data.code=$countryId"

        def countriesResponseAAJson: String =
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
            |    }
            |  ]
            |}
            |""".stripMargin
        "must return Seq of Country when successful" in {
          val countryId = "GB"
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(phase5Url(countryId)))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(countriesResponseAAJson))
              )

              val expectedResult = Country(countryId)

              connector.getCountryCustomsSecurityAgreementAreaCountry(countryId).futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          val countryId = "AD"
          checkNoReferenceDataFoundResponse(phase5Url(countryId), phase5emptyResponseJson, connector.getCountryCustomsSecurityAgreementAreaCountry(countryId))
        }

        "must return an exception when an error response is returned" in {
          val countryId = "AD"
          checkErrorResponse(phase5Url(countryId), connector.getCountryCustomsSecurityAgreementAreaCountry(countryId))
        }
      }

    }

    "getCountryCodeCommunityCountry" - {
      "when phase -6" - {
        def phase6Url(countryId: String) = s"/$baseUrl/lists/CountryCodesCommunity?keys=$countryId"
        def countriesResponseCommunity: String =
          """
            |[
            |    {
            |      "key": "GB",
            |      "value": "United Kingdom"
            |    }
            |  ]
            |""".stripMargin
        "must return Seq of Country when successful" in {
          val countryId = "GB"
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(phase6Url(countryId)))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(countriesResponseCommunity))
              )

              val expectedResult = Country(countryId)

              connector.getCountryCodeCommunityCountry(countryId).futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          val countryId = "AD"
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(phase6Url(countryId), phase6emptyResponseJson, connector.getCountryCodeCommunityCountry(countryId))
          }

        }

        "must return an exception when an error response is returned" in {
          val countryId = "AD"
          checkErrorResponse(phase6Url(countryId), connector.getCountryCodeCommunityCountry(countryId))
        }
      }
      "when phase -5" - {
        def phase5Url(countryId: String) = s"/$baseUrl/lists/CountryCodesCommunity?data.code=$countryId"
        def countriesResponseCommunity: String =
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
            |    }
            |  ]
            |}
            |""".stripMargin
        "must return Seq of Country when successful" in {
          val countryId = "GB"
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(phase5Url(countryId)))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(countriesResponseCommunity))
              )

              val expectedResult = Country(countryId)

              connector.getCountryCodeCommunityCountry(countryId).futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          val countryId = "AD"
          checkNoReferenceDataFoundResponse(phase5Url(countryId), phase5emptyResponseJson, connector.getCountryCodeCommunityCountry(countryId))
        }

        "must return an exception when an error response is returned" in {
          val countryId = "AD"
          checkErrorResponse(phase5Url(countryId), connector.getCountryCodeCommunityCountry(countryId))
        }
      }

    }

    "getSecurityTypes" - {
      val url = s"/$baseUrl/lists/DeclarationTypeSecurity"

      "when phase-6" - {
        def securityTypesResponseJson: String =
          """
            |[
            |    {
            |      "key": "2",
            |      "value": "EXS"
            |    },
            |    {
            |      "key": "3",
            |      "value": "ENS &amp; EXS"
            |    }
            |]
            |""".stripMargin

        "must return Seq of security types when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(securityTypesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                SecurityType("2", "EXS"),
                SecurityType("3", "ENS &amp; EXS")
              )

              connector.getSecurityTypes().futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, phase6emptyResponseJson, connector.getSecurityTypes())
          }

        }

        "must return an exception when an error response is returned" in {
          checkErrorResponse(url, connector.getSecurityTypes())
        }
      }
      "when phase-5" - {
        def securityTypesResponseJson: String =
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

        "must return Seq of security types when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(securityTypesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                SecurityType("2", "EXS"),
                SecurityType("3", "ENS &amp; EXS")
              )

              connector.getSecurityTypes().futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          checkNoReferenceDataFoundResponse(url, phase5emptyResponseJson, connector.getSecurityTypes())
        }

        "must return an exception when an error response is returned" in {
          checkErrorResponse(url, connector.getSecurityTypes())
        }
      }

    }

    "getDeclarationTypes" - {
      val url = s"/$baseUrl/lists/DeclarationType"
      "when phase -6" - {
        def declarationTypesResponseJson: String =
          """
            |[
            |    {
            |      "key": "T2",
            |      "value": "Goods having the customs status of Union goods, which are placed under the common transit procedure"
            |    },
            |    {
            |      "key": "TIR",
            |      "value": "TIR carnet"
            |    }
            |  ]
            |""".stripMargin
        "must return Seq of declaration types when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(declarationTypesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                DeclarationType("T2", "Goods having the customs status of Union goods, which are placed under the common transit procedure"),
                DeclarationType("TIR", "TIR carnet")
              )

              connector.getDeclarationTypes().futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, phase6emptyResponseJson, connector.getDeclarationTypes())
          }

        }

        "must return an exception when an error response is returned" in {
          checkErrorResponse(url, connector.getDeclarationTypes())
        }
      }
      "when phase -5" - {
        def declarationTypesResponseJson: String =
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
        "must return Seq of declaration types when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(declarationTypesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                DeclarationType("T2", "Goods having the customs status of Union goods, which are placed under the common transit procedure"),
                DeclarationType("TIR", "TIR carnet")
              )

              connector.getDeclarationTypes().futureValue.value mustEqual expectedResult
          }

        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          checkNoReferenceDataFoundResponse(url, phase5emptyResponseJson, connector.getDeclarationTypes())
        }

        "must return an exception when an error response is returned" in {
          checkErrorResponse(url, connector.getDeclarationTypes())
        }
      }

    }

    "getAdditionalDeclarationTypes" - {
      val url = s"/$baseUrl/lists/DeclarationTypeAdditional"
      "when phase-6" - {

        def additionalDeclarationTypesResponseJson: String =
          """
            |[
            |    {
            |      "key": "A",
            |      "value": "for a standard customs declaration (under Article 162 of the Code)"
            |    },
            |    {
            |      "key": "D",
            |      "value": "For lodging a standard customs declaration (such as referred to under code A) in accordance with Article 171 of the Code."
            |    }
            |  ]
            |""".stripMargin
        "must return Seq of additional declaration types when successful" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(additionalDeclarationTypesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                AdditionalDeclarationType(
                  "A",
                  "for a standard customs declaration (under Article 162 of the Code)"
                ),
                AdditionalDeclarationType(
                  "D",
                  "For lodging a standard customs declaration (such as referred to under code A) in accordance with Article 171 of the Code."
                )
              )
              connector.getAdditionalDeclarationTypes().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, phase6emptyResponseJson, connector.getAdditionalDeclarationTypes())
          }
        }

        "must return an exception when an error response is returned" in {
          checkErrorResponse(url, connector.getAdditionalDeclarationTypes())
        }
      }
      "when phase-5" - {
        def additionalDeclarationTypesResponseJson: String =
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
        "must return Seq of additional declaration types when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(additionalDeclarationTypesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                AdditionalDeclarationType(
                  "A",
                  "for a standard customs declaration (under Article 162 of the Code)"
                ),
                AdditionalDeclarationType(
                  "D",
                  "For lodging a standard customs declaration (such as referred to under code A) in accordance with Article 171 of the Code."
                )
              )
              connector.getAdditionalDeclarationTypes().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          checkNoReferenceDataFoundResponse(url, phase5emptyResponseJson, connector.getAdditionalDeclarationTypes())

        }

        "must return an exception when an error response is returned" in {
          checkErrorResponse(url, connector.getAdditionalDeclarationTypes())
        }
      }
    }

  }

  private def checkNoReferenceDataFoundResponse(url: String, response: String, result: => Future[Either[Exception, ?]]): Assertion = {
    server.stubFor(
      get(urlEqualTo(url))
        .willReturn(okJson(response))
    )

    result.futureValue.left.value mustBe a[NoReferenceDataFoundException]
  }

  private def checkErrorResponse(url: String, result: => Future[Either[Exception, ?]]): Assertion = {
    val errorResponses: Gen[Int] = Gen.chooseNum(400: Int, 599: Int)

    forAll(errorResponses) {
      errorResponse =>
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
            .willReturn(
              aResponse()
                .withStatus(errorResponse)
            )
        )

        result.futureValue.left.value mustBe an[Exception]
    }
  }

}
