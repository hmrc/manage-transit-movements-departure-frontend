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

package services

import base.SpecBase
import connectors.ApiConnector
import models.journeyDomain.routeDetails.RouteDetailsDomain
import models.journeyDomain.routeDetails.loadingAndUnloading.LoadingAndUnloadingDomain
import models.journeyDomain.routeDetails.routing.RoutingDomain
import models.journeyDomain.{DepartureDomain, PreTaskListDomain}
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{DeclarationType, LocalReferenceNumber, ProcedureType, SecurityDetailsType}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class ApiServiceSpec extends SpecBase with BeforeAndAfterEach {

  val mockApiConnector: ApiConnector = mock[ApiConnector]
  val service: ApiService            = new ApiService(mockApiConnector)
  val response: HttpResponse         = mock[HttpResponse]

  override def beforeEach(): Unit = {
    reset(mockApiConnector)
    super.beforeEach()
  }

  val customsOffice: CustomsOffice = CustomsOffice("foo", "bar", None)

  val preTaskListDomain: PreTaskListDomain = PreTaskListDomain(LocalReferenceNumber("refno").get,
                                                               customsOffice,
                                                               ProcedureType.Normal,
                                                               DeclarationType.Option1,
                                                               None,
                                                               SecurityDetailsType.NoSecurityDetails,
                                                               true
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

  val request = DepartureDomain(preTaskListDomain, routeDetailsDomain)

  "ApiService" - {

    "submitDeclaration" - {

      "must return a success response with valid request" in {

        when(response.status).thenReturn(OK)
        when(mockApiConnector.submitDeclaration(eqTo(request))(any())).thenReturn(Future.successful(response))

        service.submitDeclaration(request).futureValue.status mustBe OK

        verify(mockApiConnector).submitDeclaration(eqTo(request))(any())
      }

      "must return a bad request response with invalid request" in {

        when(response.status).thenReturn(BAD_REQUEST)
        when(mockApiConnector.submitDeclaration(eqTo(request))(any())).thenReturn(Future.successful(response))

        service.submitDeclaration(request).futureValue.status mustBe BAD_REQUEST

        verify(mockApiConnector).submitDeclaration(eqTo(request))(any())
      }

      "must return a server error when something goes wrong" in {

        when(response.status).thenReturn(INTERNAL_SERVER_ERROR)
        when(mockApiConnector.submitDeclaration(eqTo(request))(any())).thenReturn(Future.successful(response))

        service.submitDeclaration(request).futureValue.status mustBe INTERNAL_SERVER_ERROR

        verify(mockApiConnector).submitDeclaration(eqTo(request))(any())
      }

    }

  }
}