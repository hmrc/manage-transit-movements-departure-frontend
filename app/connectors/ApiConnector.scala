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

import config.FrontendAppConfig
import models.journeyDomain.DepartureDomain
import play.api.Logging
import play.api.http.HeaderNames
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpErrorFunctions, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApiConnector @Inject() (httpClient: HttpClient, appConfig: FrontendAppConfig)(implicit ec: ExecutionContext) extends HttpErrorFunctions with Logging {

  private val requestHeaders = Seq(
    HeaderNames.ACCEPT       -> "application/vnd.hmrc.2.0+json",
    HeaderNames.CONTENT_TYPE -> "application/json"
  )

  // TODO - replace payload with data from user answers

  // TODO - reduced data set indicator is asked but never stored
  // TODO - bindingItinerary is asked but never stored
  def payload(request: DepartureDomain): String =
    s"""
       |{
       |  "n1:CC015C": {
       |    "preparationDateAndTime": "2022-01-22T07:43:36",
       |    "TransitOperation": {
       |      "LRN": "${request.preTaskList.localReferenceNumber.value}",
       |      "declarationType": "${request.preTaskList.declarationType.toString}",
       |      "additionalDeclarationType": "A",
       |      "security": "${request.preTaskList.securityDetailsType.securityContentType}",
       |      "reducedDatasetIndicator": "1",
       |      "bindingItinerary": "0"
       |    },
       |    "CustomsOfficeOfDeparture": {
       |      "referenceNumber": "${request.preTaskList.officeOfDeparture.id}"
       |    },
       |    "CustomsOfficeOfDestinationDeclared": {
       |      "referenceNumber": "${request.routeDetails.routing.officeOfDestination.id}"
       |    },
       |    "messageType": "CC015C",
       |    "@PhaseID": "NCTS5.0",
       |    "messageRecipient": "FdOcminxBxSLGm1rRUn0q96S1",
       |    "HolderOfTheTransitProcedure": {
       |      "identificationNumber": "SFzsisksA"
       |    },
       |    "Consignment": {
       |      "grossMass": 6430669292.48125,
       |      "HouseConsignment": [
       |        {
       |          "sequenceNumber": "48711",
       |          "grossMass": 6430669292.48125,
       |          "AdditionalSupplyChainActor": [],
       |          "DepartureTransportMeans": [],
       |          "PreviousDocument": [],
       |          "SupportingDocument": [],
       |          "TransportDocument": [],
       |          "AdditionalReference": [],
       |          "AdditionalInformation": [],
       |          "ConsignmentItem": [
       |            {
       |              "goodsItemNumber": "18914",
       |              "declarationGoodsItemNumber": 1458,
       |              "AdditionalSupplyChainActor": [],
       |              "Commodity": {
       |                "descriptionOfGoods": "ZMyM5HTSTnLqT5FT9aHXwScqXKC1VitlWeO5gs91cVXBXOB8xBdXG5aGhG9VFjjDGiraIETFfbQWeA7VUokO7ngDOrKZ23ccKKMA6C3GpXciUTt9nS2pzCFFFeg4BXdkIe",
       |                "DangerousGoods": []
       |              },
       |              "Packaging": [
       |                {
       |                  "sequenceNumber": "48711",
       |                  "typeOfPackages": "Oi"
       |                }
       |              ],
       |              "PreviousDocument": [],
       |              "SupportingDocument": [],
       |              "TransportDocument": [],
       |              "AdditionalReference": [],
       |              "AdditionalInformation": []
       |            }
       |          ]
       |        }
       |      ]
       |    },
       |    "messageIdentification": "6Onxa3En",
       |    "Guarantee": [
       |      {
       |        "sequenceNumber": "48711",
       |        "guaranteeType": "1",
       |        "otherGuaranteeReference": "1qJMA6MbhnnrOJJjHBHX",
       |        "GuaranteeReference": []
       |      }
       |    ]
       |  }
       |}
       |
       |""".stripMargin

  def submitDeclaration(request: DepartureDomain)(implicit hc: HeaderCarrier): Future[HttpResponse] = {

    val declarationUrl = s"${appConfig.apiUrl}/movements/departures"

    // TODO - do via domain models with reads and writes?
    // httpClient.POST[DepartureDomain, HttpResponse](declarationUrl, request, requestHeaders)
    httpClient.POSTString(declarationUrl, payload(request), requestHeaders)

  }

}
