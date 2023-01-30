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

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generated._
import generators.Generators
import models.UserAnswers
import models.domain.UserAnswersReader
import models.journeyDomain.DepartureDomain
import models.journeyDomain.DepartureDomain.userAnswersReader

class CustomsOfficesSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  val uA: UserAnswers = arbitraryDepartureAnswers(emptyUserAnswers).sample.value

  "CustomsOffices" - {

    "transformOfficeOfDeparture is called" - {

      "will convert to API format" in {

        UserAnswersReader[DepartureDomain](userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)).run(uA).map {
          case DepartureDomain(preTaskList, _, _, _, _) =>
            val expected: CustomsOfficeOfDepartureType03  = CustomsOfficeOfDepartureType03(preTaskList.officeOfDeparture.id)
            val converted: CustomsOfficeOfDepartureType03 = CustomsOffices.transformOfficeOfDeparture(preTaskList.officeOfDeparture)

            converted mustBe expected
        }

      }

    }

    "transformOfficeOfDestination is called" - {

      "will convert to API format" in {

        UserAnswersReader[DepartureDomain](userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)).run(uA).map {
          case DepartureDomain(_, _, routeDetails, _, _) =>
            val expected: CustomsOfficeOfDestinationDeclaredType01  = CustomsOfficeOfDestinationDeclaredType01(routeDetails.routing.officeOfDestination.id)
            val converted: CustomsOfficeOfDestinationDeclaredType01 = CustomsOffices.transformOfficeOfDestination(routeDetails.routing.officeOfDestination)

            converted mustBe expected
        }

      }

    }

    "transformOfficeOfTransit is called" - {

      "will convert to API format" in {

        UserAnswersReader[DepartureDomain](userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)).run(uA).map {
          case DepartureDomain(_, _, routeDetails, _, _) =>
            val expected: Seq[CustomsOfficeOfTransitDeclaredType03] = routeDetails.transit
              .map(
                transitDomain =>
                  transitDomain.officesOfTransit
                    .map(
                      officeOfTransitDomain =>
                        CustomsOfficeOfTransitDeclaredType03(
                          transitDomain.officesOfTransit.indexOf(officeOfTransitDomain.customsOffice).toString,
                          officeOfTransitDomain.customsOffice.id
                        )
                    )
              )
              .getOrElse(Seq.empty)

            val converted: Seq[CustomsOfficeOfTransitDeclaredType03] = CustomsOffices.transformOfficeOfTransit(routeDetails.transit)

            converted mustBe expected
        }

      }

    }

    "transformOfficeOfExit is called" - {

      "will convert to API format" in {

        UserAnswersReader[DepartureDomain](userAnswersReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)).run(uA).map {
          case DepartureDomain(_, _, routeDetails, _, _) =>
            val expected: Seq[CustomsOfficeOfExitForTransitDeclaredType02] = routeDetails.exit
              .map(
                transitDomain =>
                  transitDomain.officesOfExit
                    .map(
                      officeOfExitDomain =>
                        CustomsOfficeOfExitForTransitDeclaredType02(
                          transitDomain.officesOfExit.indexOf(officeOfExitDomain).toString,
                          officeOfExitDomain.customsOffice.id
                        )
                    )
              )
              .getOrElse(Seq.empty)

            val converted: Seq[CustomsOfficeOfExitForTransitDeclaredType02] = CustomsOffices.transformOfficeOfExit(routeDetails.exit)

            converted mustBe expected
        }

      }

    }

  }
}
