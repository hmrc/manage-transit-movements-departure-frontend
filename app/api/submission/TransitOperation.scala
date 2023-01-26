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

package api.submission

import api.ApiXmlHelper
import generated.TransitOperationType06
import models.journeyDomain.PreTaskListDomain
import models.journeyDomain.routeDetails.routing.RoutingDomain
import models.journeyDomain.transport.TransportDomain

object TransitOperation {

  def transform(lrn: String,
                preTaskListDomain: PreTaskListDomain,
                reducedDatasetIndicator: Boolean,
                routingDomain: RoutingDomain,
                transportDomain: TransportDomain
  ): TransitOperationType06 =
    TransitOperationType06(
      LRN = lrn,
      declarationType = preTaskListDomain.declarationType.toString,
      additionalDeclarationType = "A",
      TIRCarnetNumber = preTaskListDomain.tirCarnetReference,
      presentationOfTheGoodsDateAndTime = None, // TODO - what is this? Needed?
      security = preTaskListDomain.securityDetailsType.securityContentType.toString,
      reducedDatasetIndicator = ApiXmlHelper.boolToFlag(reducedDatasetIndicator),
      specificCircumstanceIndicator = None, // TODO - what is this? Needed?
      communicationLanguageAtDeparture = None, // TODO - what is this? Needed?
      bindingItinerary = ApiXmlHelper.boolToFlag(routingDomain.bindingItinerary),
      limitDate = transportDomain.authorisationsAndLimit.flatMap(
        a =>
          a.limitDomain
            .map(
              l => ApiXmlHelper.toDate(l.limitDate.toString)
            )
      )
    )
}
