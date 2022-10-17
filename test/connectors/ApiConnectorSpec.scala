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
import com.github.tomakehurst.wiremock.client.WireMock._
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import helper.WireMockServerHandler
import models.journeyDomain.routeDetails.RouteDetailsDomain
import models.journeyDomain.routeDetails.loadingAndUnloading.LoadingAndUnloadingDomain
import models.journeyDomain.routeDetails.routing.RoutingDomain
import models.journeyDomain.traderDetails.TraderDetailsDomain
import models.journeyDomain.traderDetails.consignment.ConsignmentDomain
import models.journeyDomain.traderDetails.holderOfTransit.HolderOfTransitDomain.HolderOfTransitEori
import models.journeyDomain.{DepartureDomain, PreTaskListDomain}
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{Address, DeclarationType, LocalReferenceNumber, ProcedureType, SecurityDetailsType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.{BadRequestException, HttpResponse, UpstreamErrorResponse}

class ApiConnectorSpec extends SpecBase with AppWithDefaultMockFixtures with WireMockServerHandler with UserAnswersSpecHelper with Generators {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.common-transit-convention-traders.port" -> server.port())

  private lazy val connector: ApiConnector = app.injector.instanceOf[ApiConnector]

  val departureId: String = "someid"

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

  val customsOffice: CustomsOffice = CustomsOffice("foo", "bar", None)

  val preTaskListDomain: PreTaskListDomain = PreTaskListDomain(LocalReferenceNumber("refno").get,
                                                               customsOffice,
                                                               ProcedureType.Normal,
                                                               DeclarationType.Option1,
                                                               None,
                                                               SecurityDetailsType.NoSecurityDetails,
                                                               true
  )

  val holderOfTransitName    = Gen.alphaNumStr.sample.value
  val holderOfTransitAddress = arbitrary[Address].sample.value

  val traderDetailsDomain: TraderDetailsDomain = TraderDetailsDomain(
    holderOfTransit = HolderOfTransitEori(
      eori = None,
      name = holderOfTransitName,
      address = holderOfTransitAddress,
      additionalContact = None
    ),
    representative = None,
    consignment = ConsignmentDomain(
      consignor = None,
      consignee = None
    ),
    reducedDataSet = true
  )

  val routingDomain: RoutingDomain = RoutingDomain(
    Country(CountryCode("GB"), "My country"),
    customsOffice,
    false,
    Seq.empty
  )

  val routeDetailsDomain: RouteDetailsDomain = RouteDetailsDomain(
    routingDomain,
    None,
    None,
    None,
    LoadingAndUnloadingDomain(None, None)
  )

  val request = DepartureDomain(preTaskListDomain, traderDetailsDomain, routeDetailsDomain)

  "ApiConnector" - {

    "submitDeclaration is called" - {

      "for success" in {

        server.stubFor(post(urlEqualTo(uri)).willReturn(okJson(expected)))

        val res: HttpResponse = await(connector.submitDeclaration(request))
        res.status mustBe OK

      }

      "for bad request" in {

        server.stubFor(post(urlEqualTo(uri)).willReturn(badRequest()))

        intercept[BadRequestException] {
          await(connector.submitDeclaration(request))
        }

      }

      "for internal server error" in {

        server.stubFor(post(urlEqualTo(uri)).willReturn(serverError()))

        intercept[UpstreamErrorResponse] {
          await(connector.submitDeclaration(request))
        }

      }

    }

  }

}
