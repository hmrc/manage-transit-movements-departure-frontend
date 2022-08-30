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

package models.journeyDomain.routeDetails

import base.SpecBase
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import models.DeclarationType.Option4
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.routeDetails.routing.RoutingDomain
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import pages.preTaskList._
import pages.routeDetails.routing._

class RouteDetailsDomainSpec extends SpecBase with Generators with RouteDetailsUserAnswersGenerator {

  "RouteDetailsDomain" - {

    "can be parsed from UserAnswers" - {

      "when TIR declaration type" in {
        val country       = arbitrary[Country].sample.value
        val customsOffice = arbitrary[CustomsOffice].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DeclarationTypePage, Option4)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(CountryOfDestinationPage, country)
          .setValue(OfficeOfDestinationPage, customsOffice)
          .setValue(BindingItineraryPage, false)
          .setValue(AddCountryOfRoutingYesNoPage, false)

        val expectedResult = RouteDetailsDomain(
          routing = RoutingDomain(
            countryOfDestination = country,
            officeOfDestination = customsOffice,
            bindingItinerary = false,
            countriesOfRouting = Nil
          ),
          transit = None
        )

        val result: EitherType[RouteDetailsDomain] = UserAnswersReader[RouteDetailsDomain](
          RouteDetailsDomain.userAnswersReader(Nil, Nil, Nil)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }
    }
  }
}
