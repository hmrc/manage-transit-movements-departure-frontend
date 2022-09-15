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
import models.SecurityDetailsType._
import models.domain.{EitherType, UserAnswersReader}
import models.journeyDomain.routeDetails.exit.{ExitDomain, OfficeOfExitDomain}
import models.journeyDomain.routeDetails.loading.{AdditionalInformationDomain, LoadingDomain}
import models.journeyDomain.routeDetails.locationOfGoods.LocationOfGoodsDomain.LocationOfGoodsV
import models.journeyDomain.routeDetails.routing.{CountryOfRoutingDomain, RoutingDomain}
import models.journeyDomain.routeDetails.transit.TransitDomain
import models.reference.{Country, CustomsOffice}
import models.{DeclarationType, LocationOfGoodsIdentification, LocationType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.preTaskList._
import pages.routeDetails.exit.index._
import pages.routeDetails.loading.{PlaceOfLoadingAddUnLocodeYesNoPage, PlaceOfLoadingCountryPage, PlaceOfLoadingLocationPage}
import pages.routeDetails.locationOfGoods._
import pages.routeDetails.routing._
import pages.routeDetails.routing.index._
import pages.routeDetails.transit._

class RouteDetailsDomainSpec extends SpecBase with Generators with RouteDetailsUserAnswersGenerator {

  "RouteDetailsDomain" - {

    "can be parsed from UserAnswers" - {

      "when TIR declaration type" in {
        val country       = arbitrary[Country].sample.value
        val loadingPlace  = Gen.alphaNumStr.sample.value.take(35)
        val customsOffice = arbitrary[CustomsOffice].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(DeclarationTypePage, Option4)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(CountryOfDestinationPage, country)
          .setValue(OfficeOfDestinationPage, customsOffice)
          .setValue(BindingItineraryPage, false)
          .setValue(AddCountryOfRoutingYesNoPage, false)
          .setValue(AddLocationOfGoodsPage, false)
          .setValue(PlaceOfLoadingAddUnLocodeYesNoPage, false)
          .setValue(PlaceOfLoadingCountryPage, country)
          .setValue(PlaceOfLoadingLocationPage, loadingPlace)

        val expectedResult = RouteDetailsDomain(
          routing = RoutingDomain(
            countryOfDestination = country,
            officeOfDestination = customsOffice,
            bindingItinerary = false,
            countriesOfRouting = Nil
          ),
          transit = None,
          exit = None,
          locationOfGoods = None,
          loading = Some(LoadingDomain(None, Some(AdditionalInformationDomain(country, loadingPlace))))
        )

        val result: EitherType[RouteDetailsDomain] = UserAnswersReader[RouteDetailsDomain](
          RouteDetailsDomain.userAnswersReader(Nil, Seq(customsOffice.countryCode))
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when security is in set {0, 1}" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val securityType    = Gen.oneOf(NoSecurityDetails, EntrySummaryDeclarationSecurityDetails).sample.value
        val country         = arbitrary[Country].sample.value
        val customsOffice   = arbitrary[CustomsOffice].sample.value
        val loadingPlace    = Gen.alphaNumStr.sample.value.take(35)

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(DeclarationTypePage, declarationType)
          .setValue(SecurityDetailsTypePage, securityType)
          .setValue(CountryOfDestinationPage, country)
          .setValue(OfficeOfDestinationPage, customsOffice)
          .setValue(BindingItineraryPage, true)
          .setValue(CountryOfRoutingPage(index), country)
          .setValue(AddOfficeOfTransitYesNoPage, false)
          .setValue(AddLocationOfGoodsPage, false)
          .setValue(PlaceOfLoadingAddUnLocodeYesNoPage, false)
          .setValue(PlaceOfLoadingCountryPage, country)
          .setValue(PlaceOfLoadingLocationPage, loadingPlace)

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
          exit = None,
          locationOfGoods = None,
          loading = Some(LoadingDomain(None, Some(AdditionalInformationDomain(country, loadingPlace))))
        )

        val result: EitherType[RouteDetailsDomain] = UserAnswersReader[RouteDetailsDomain](
          RouteDetailsDomain.userAnswersReader(Seq(customsOffice.countryCode), Seq(customsOffice.countryCode))
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when at least one country of routing is in set CL147" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val securityType    = Gen.oneOf(ExitSummaryDeclarationSecurityDetails, EntryAndExitSummaryDeclarationSecurityDetails).sample.value
        val country         = arbitrary[Country].sample.value
        val customsOffice   = arbitrary[CustomsOffice].sample.value
        val loadingPlace    = Gen.alphaNumStr.sample.value.take(35)

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(DeclarationTypePage, declarationType)
          .setValue(SecurityDetailsTypePage, securityType)
          .setValue(CountryOfDestinationPage, country)
          .setValue(OfficeOfDestinationPage, customsOffice)
          .setValue(BindingItineraryPage, true)
          .setValue(CountryOfRoutingPage(index), country)
          .setValue(AddOfficeOfTransitYesNoPage, false)
          .setValue(AddLocationOfGoodsPage, false)
          .setValue(PlaceOfLoadingAddUnLocodeYesNoPage, false)
          .setValue(PlaceOfLoadingCountryPage, country)
          .setValue(PlaceOfLoadingLocationPage, loadingPlace)

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
          exit = None,
          locationOfGoods = None,
          loading = Some(LoadingDomain(None, Some(AdditionalInformationDomain(country, loadingPlace))))
        )

        val result: EitherType[RouteDetailsDomain] = UserAnswersReader[RouteDetailsDomain](
          RouteDetailsDomain.userAnswersReader(Seq(customsOffice.countryCode), Seq(country.code.code, customsOffice.countryCode))
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when no countries of routing are in set CL147" in {
        val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
        val securityType    = Gen.oneOf(ExitSummaryDeclarationSecurityDetails, EntryAndExitSummaryDeclarationSecurityDetails).sample.value
        val country         = arbitrary[Country].sample.value
        val customsOffice   = arbitrary[CustomsOffice].sample.value
        val loadingPlace    = Gen.alphaNumStr.sample.value.take(35)

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
          .setValue(AddLocationOfGoodsPage, false)
          .setValue(PlaceOfLoadingAddUnLocodeYesNoPage, false)
          .setValue(PlaceOfLoadingCountryPage, country)
          .setValue(PlaceOfLoadingLocationPage, loadingPlace)

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
          ),
          locationOfGoods = None,
          loading = Some(LoadingDomain(None, Some(AdditionalInformationDomain(country, loadingPlace))))
        )

        val result: EitherType[RouteDetailsDomain] = UserAnswersReader[RouteDetailsDomain](
          RouteDetailsDomain.userAnswersReader(Seq(customsOffice.countryCode), Seq(customsOffice.countryCode))
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when pre-lodging (D)" ignore {}

      "when not pre-lodging" - {
        "and office of departure is not in set CL147" in {
          val country        = arbitrary[Country].sample.value
          val customsOffice  = arbitrary[CustomsOffice].sample.value
          val typeOfLocation = arbitrary[LocationType].sample.value
          val loadingPlace   = Gen.alphaNumStr.sample.value.take(35)

          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOffice)
            .setValue(DeclarationTypePage, Option4)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(CountryOfDestinationPage, country)
            .setValue(OfficeOfDestinationPage, customsOffice)
            .setValue(BindingItineraryPage, false)
            .setValue(AddCountryOfRoutingYesNoPage, false)
            .setValue(LocationOfGoodsTypePage, typeOfLocation)
            .setValue(LocationOfGoodsIdentificationPage, LocationOfGoodsIdentification.CustomsOfficeIdentifier)
            .setValue(LocationOfGoodsCustomsOfficeIdentifierPage, customsOffice)
            .setValue(PlaceOfLoadingAddUnLocodeYesNoPage, false)
            .setValue(PlaceOfLoadingCountryPage, country)
            .setValue(PlaceOfLoadingLocationPage, loadingPlace)

          val expectedResult = RouteDetailsDomain(
            routing = RoutingDomain(
              countryOfDestination = country,
              officeOfDestination = customsOffice,
              bindingItinerary = false,
              countriesOfRouting = Nil
            ),
            transit = None,
            exit = None,
            locationOfGoods = Some(
              LocationOfGoodsV(
                typeOfLocation = typeOfLocation,
                customsOffice = customsOffice
              )
            ),
            loading = Some(LoadingDomain(None, Some(AdditionalInformationDomain(country, loadingPlace))))
          )

          val result: EitherType[RouteDetailsDomain] = UserAnswersReader[RouteDetailsDomain](
            RouteDetailsDomain.userAnswersReader(Nil, Nil)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }
      }
    }
  }
}
