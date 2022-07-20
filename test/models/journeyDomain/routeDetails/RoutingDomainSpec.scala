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
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.SecurityDetailsType._
import models.domain.{EitherType, UserAnswersReader}
import models.reference.{Country, CustomsOffice}
import models.{Index, SecurityDetailsType}
import org.scalacheck.Arbitrary.arbitrary
import pages.preTaskList._
import pages.routeDetails.routing.{AddCountryOfRoutingYesNoPage, BindingItineraryPage, CountryOfRoutingPage, OfficeOfDestinationPage}

class RoutingDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "RoutingDomain" - {

    val officeOfDestination = arbitrary[CustomsOffice].sample.value
    val country             = arbitrary[Country].sample.value

    "can be parsed from UserAnswers" - {

      "when no security" - {

        val securityType = NoSecurityDetails

        "and following binding itinerary" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(SecurityDetailsTypePage)(securityType)
            .unsafeSetVal(OfficeOfDestinationPage)(officeOfDestination)
            .unsafeSetVal(BindingItineraryPage)(true)
            .unsafeSetVal(CountryOfRoutingPage(index))(country)

          val expectedResult = RoutingDomain(
            officeOfDestination = officeOfDestination,
            bindingItinerary = true,
            countriesOfRouting = Seq(
              CountryOfRoutingDomain(country)(index)
            )
          )

          val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

          result.value mustBe expectedResult
        }

        "and not following binding itinerary" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(SecurityDetailsTypePage)(securityType)
            .unsafeSetVal(OfficeOfDestinationPage)(officeOfDestination)
            .unsafeSetVal(BindingItineraryPage)(false)
            .unsafeSetVal(AddCountryOfRoutingYesNoPage)(false)

          val expectedResult = RoutingDomain(
            officeOfDestination = officeOfDestination,
            bindingItinerary = false,
            countriesOfRouting = Nil
          )

          val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

          result.value mustBe expectedResult
        }
      }

      "when there is security" - {

        val securityType = arbitrary[SecurityDetailsType](arbitrarySomeSecurityDetailsType).sample.value

        "and following binding itinerary" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(SecurityDetailsTypePage)(securityType)
            .unsafeSetVal(OfficeOfDestinationPage)(officeOfDestination)
            .unsafeSetVal(BindingItineraryPage)(true)
            .unsafeSetVal(CountryOfRoutingPage(index))(country)

          val expectedResult = RoutingDomain(
            officeOfDestination = officeOfDestination,
            bindingItinerary = true,
            countriesOfRouting = Seq(
              CountryOfRoutingDomain(country)(index)
            )
          )

          val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

          result.value mustBe expectedResult
        }

        "and not following binding itinerary" in {

          val userAnswers = emptyUserAnswers
            .unsafeSetVal(SecurityDetailsTypePage)(securityType)
            .unsafeSetVal(OfficeOfDestinationPage)(officeOfDestination)
            .unsafeSetVal(BindingItineraryPage)(false)
            .unsafeSetVal(CountryOfRoutingPage(index))(country)

          val expectedResult = RoutingDomain(
            officeOfDestination = officeOfDestination,
            bindingItinerary = false,
            countriesOfRouting = Seq(
              CountryOfRoutingDomain(country)(index)
            )
          )

          val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

          result.value mustBe expectedResult
        }
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when office of destination page is missing" in {

        val securityType = arbitrary[SecurityDetailsType].sample.value
        val userAnswers  = emptyUserAnswers.unsafeSetVal(SecurityDetailsTypePage)(securityType)

        val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

        result.left.value.page mustBe OfficeOfDestinationPage
      }

      "when binding itinerary page is missing" in {

        val securityType = arbitrary[SecurityDetailsType].sample.value
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(SecurityDetailsTypePage)(securityType)
          .unsafeSetVal(OfficeOfDestinationPage)(officeOfDestination)

        val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

        result.left.value.page mustBe BindingItineraryPage
      }

      "when add country page is missing" - {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(SecurityDetailsTypePage)(NoSecurityDetails)
          .unsafeSetVal(OfficeOfDestinationPage)(officeOfDestination)
          .unsafeSetVal(BindingItineraryPage)(false)

        val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

        result.left.value.page mustBe AddCountryOfRoutingYesNoPage
      }

      "when binding itinerary is true and no countries added" in {

        val securityType = arbitrary[SecurityDetailsType].sample.value
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(SecurityDetailsTypePage)(securityType)
          .unsafeSetVal(OfficeOfDestinationPage)(officeOfDestination)
          .unsafeSetVal(BindingItineraryPage)(true)

        val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

        result.left.value.page mustBe CountryOfRoutingPage(Index(0))
      }

      "when there's security and no countries added" in {

        val securityType     = arbitrary[SecurityDetailsType](arbitrarySomeSecurityDetailsType).sample.value
        val bindingItinerary = arbitrary[Boolean].sample.value
        val userAnswers = emptyUserAnswers
          .unsafeSetVal(SecurityDetailsTypePage)(securityType)
          .unsafeSetVal(OfficeOfDestinationPage)(officeOfDestination)
          .unsafeSetVal(BindingItineraryPage)(bindingItinerary)

        val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

        result.left.value.page mustBe CountryOfRoutingPage(Index(0))
      }

      "when add country is true and no countries added" in {

        val userAnswers = emptyUserAnswers
          .unsafeSetVal(SecurityDetailsTypePage)(NoSecurityDetails)
          .unsafeSetVal(OfficeOfDestinationPage)(officeOfDestination)
          .unsafeSetVal(BindingItineraryPage)(false)
          .unsafeSetVal(AddCountryOfRoutingYesNoPage)(true)

        val result: EitherType[RoutingDomain] = UserAnswersReader[RoutingDomain].run(userAnswers)

        result.left.value.page mustBe CountryOfRoutingPage(Index(0))
      }
    }
  }
}
