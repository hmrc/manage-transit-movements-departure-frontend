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
import models.DeclarationType
import models.DeclarationType.Option4
import models.SecurityDetailsType._
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.routeDetails.exit.{ExitDomain, OfficeOfExitDomain}
import models.journeyDomain.routeDetails.routing.{CountryOfRoutingDomain, RoutingDomain}
import models.journeyDomain.routeDetails.transit.TransitDomain
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.preTaskList._
import pages.routeDetails.exit.index._
import pages.routeDetails.routing._
import pages.routeDetails.routing.index.CountryOfRoutingPage
import pages.routeDetails.transit.AddOfficeOfTransitYesNoPage

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
          transit = None,
          exit = None
        )

        val result: EitherType[RouteDetailsDomain] = UserAnswersReader[RouteDetailsDomain](
          RouteDetailsDomain.userAnswersReader(Nil, Nil, Nil)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when security is in set {0, 1}" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val securityType    = Gen.oneOf(NoSecurityDetails, EntrySummaryDeclarationSecurityDetails).sample.value
        val country         = arbitrary[Country].sample.value
        val customsOffice   = arbitrary[CustomsOffice].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(DeclarationTypePage, declarationType)
          .setValue(SecurityDetailsTypePage, securityType)
          .setValue(CountryOfDestinationPage, country)
          .setValue(OfficeOfDestinationPage, customsOffice)
          .setValue(BindingItineraryPage, true)
          .setValue(CountryOfRoutingPage(index), country)
          .setValue(AddOfficeOfTransitYesNoPage, false)

        val expectedResult = RouteDetailsDomain(
          routing = RoutingDomain(
            countryOfDestination = country,
            officeOfDestination = customsOffice,
            bindingItinerary = true,
            countriesOfRouting = Seq(
              CountryOfRoutingDomain(country)(index)
            )
          ),
          transit = Some(
            TransitDomain(
              isT2DeclarationType = None,
              officesOfTransit = Nil
            )
          ),
          exit = None
        )

        val result: EitherType[RouteDetailsDomain] = UserAnswersReader[RouteDetailsDomain](
          RouteDetailsDomain.userAnswersReader(Seq(customsOffice.countryCode), Nil, Nil)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when at least one country of routing is in set CL147" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val securityType    = Gen.oneOf(ExitSummaryDeclarationSecurityDetails, EntryAndExitSummaryDeclarationSecurityDetails).sample.value
        val country         = arbitrary[Country].sample.value
        val customsOffice   = arbitrary[CustomsOffice].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(DeclarationTypePage, declarationType)
          .setValue(SecurityDetailsTypePage, securityType)
          .setValue(CountryOfDestinationPage, country)
          .setValue(OfficeOfDestinationPage, customsOffice)
          .setValue(BindingItineraryPage, true)
          .setValue(CountryOfRoutingPage(index), country)
          .setValue(AddOfficeOfTransitYesNoPage, false)

        val expectedResult = RouteDetailsDomain(
          routing = RoutingDomain(
            countryOfDestination = country,
            officeOfDestination = customsOffice,
            bindingItinerary = true,
            countriesOfRouting = Seq(
              CountryOfRoutingDomain(country)(index)
            )
          ),
          transit = Some(
            TransitDomain(
              isT2DeclarationType = None,
              officesOfTransit = Nil
            )
          ),
          exit = None
        )

        val result: EitherType[RouteDetailsDomain] = UserAnswersReader[RouteDetailsDomain](
          RouteDetailsDomain.userAnswersReader(Seq(customsOffice.countryCode), Nil, Seq(country.code.code))
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when no countries of routing are in set CL147" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val securityType    = Gen.oneOf(ExitSummaryDeclarationSecurityDetails, EntryAndExitSummaryDeclarationSecurityDetails).sample.value
        val country         = arbitrary[Country].sample.value
        val customsOffice   = arbitrary[CustomsOffice].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(DeclarationTypePage, declarationType)
          .setValue(SecurityDetailsTypePage, securityType)
          .setValue(CountryOfDestinationPage, country)
          .setValue(OfficeOfDestinationPage, customsOffice)
          .setValue(BindingItineraryPage, true)
          .setValue(CountryOfRoutingPage(index), country)
          .setValue(AddOfficeOfTransitYesNoPage, false)
          .setValue(OfficeOfExitCountryPage(index), country)
          .setValue(OfficeOfExitPage(index), customsOffice)

        val expectedResult = RouteDetailsDomain(
          routing = RoutingDomain(
            countryOfDestination = country,
            officeOfDestination = customsOffice,
            bindingItinerary = true,
            countriesOfRouting = Seq(
              CountryOfRoutingDomain(country)(index)
            )
          ),
          transit = Some(
            TransitDomain(
              isT2DeclarationType = None,
              officesOfTransit = Nil
            )
          ),
          exit = Some(
            ExitDomain(
              Seq(
                OfficeOfExitDomain(country, customsOffice)(index)
              )
            )
          )
        )

        val result: EitherType[RouteDetailsDomain] = UserAnswersReader[RouteDetailsDomain](
          RouteDetailsDomain.userAnswersReader(Seq(customsOffice.countryCode), Nil, Nil)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }
    }
  }
}
