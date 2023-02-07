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
import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generated._
import generators.Generators
import models.domain.UserAnswersReader
import models.journeyDomain.DepartureDomain
import models.journeyDomain.DepartureDomain.userAnswersReader

class TransitOperationSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "TransitOperation" - {

    "transform is called" - {

      "will convert to API format" in {

        arbitraryDepartureAnswers(emptyUserAnswers).map(
          arbitraryDepartureUserAnswers =>
            UserAnswersReader[DepartureDomain](userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes))
              .run(arbitraryDepartureUserAnswers)
              .map {
                case DepartureDomain(preTaskList, _, routeDetails, _, transportDetails) =>
                  val expected: TransitOperationType06 = TransitOperationType06(
                    LRN = arbitraryDepartureUserAnswers.lrn.value,
                    declarationType = preTaskList.declarationType.toString,
                    additionalDeclarationType = "A",
                    TIRCarnetNumber = preTaskList.tirCarnetReference,
                    presentationOfTheGoodsDateAndTime = None,
                    security = preTaskList.securityDetailsType.securityContentType.toString,
                    reducedDatasetIndicator = ApiXmlHelper.boolToFlag(false),
                    specificCircumstanceIndicator = None,
                    communicationLanguageAtDeparture = None,
                    bindingItinerary = ApiXmlHelper.boolToFlag(routeDetails.routing.bindingItinerary),
                    limitDate = transportDetails.authorisationsAndLimit.flatMap(
                      a =>
                        a.limitDomain
                          .map(
                            l => ApiXmlHelper.toDate(l.limitDate.toString)
                          )
                    )
                  )

                  val converted = TransitOperation.transform(
                    lrn = arbitraryDepartureUserAnswers.lrn.value,
                    preTaskListDomain = preTaskList,
                    reducedDatasetIndicator = false,
                    routingDomain = routeDetails.routing,
                    transportDomain = transportDetails
                  )

                  converted mustBe expected
              }
        )
      }

    }

  }
}
