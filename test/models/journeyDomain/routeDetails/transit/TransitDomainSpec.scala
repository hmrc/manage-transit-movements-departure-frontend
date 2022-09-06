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

package models.journeyDomain.routeDetails.transit

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generators.Generators
import models.DeclarationType._
import models.Index
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.{EitherType, UserAnswersReader}
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.preTaskList.{DeclarationTypePage, OfficeOfDeparturePage, SecurityDetailsTypePage}
import pages.routeDetails.routing.index.CountryOfRoutingPage
import pages.routeDetails.routing.{BindingItineraryPage, OfficeOfDestinationPage}
import pages.routeDetails.transit._
import pages.routeDetails.transit.index._

class TransitDomainSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "TransitDomain" - {

    val country      = arbitrary[Country].sample.value
    val countryCodes = Seq(country.code.code)

    def customsOffice                    = arbitrary[CustomsOffice].sample.value
    def customsOfficeFromListedCountry   = arbitrary[CustomsOffice].sample.value.copy(id = country.code.code)
    def customsOfficeFromUnlistedCountry = arbitrary[CustomsOffice].retryUntil(_.countryCode != country.code.code).sample.value

    val officeOfTransit = customsOffice

    "can be parsed from UserAnswers" - {

      "when offices of departure and destination country codes are in set CL112 and both have same country code" in {
        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOfficeFromListedCountry)
          .setValue(OfficeOfDestinationPage, customsOfficeFromListedCountry)
          .setValue(AddOfficeOfTransitYesNoPage, false)

        val expectedResult = TransitDomain(
          isT2DeclarationType = None,
          officesOfTransit = Nil
        )

        val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when T2 declaration type" in {
        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(DeclarationTypePage, Option2)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(OfficeOfDestinationPage, customsOffice)
          .setValue(OfficeOfTransitCountryPage(index), country)
          .setValue(OfficeOfTransitPage(index), officeOfTransit)
          .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

        val expectedResult = TransitDomain(
          isT2DeclarationType = None,
          officesOfTransit = Seq(OfficeOfTransitDomain(Some(country), officeOfTransit, None)(index))
        )

        val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(Nil, Nil)).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when T declaration type" - {

        "and some items are T2 declaration type" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOffice)
            .setValue(DeclarationTypePage, Option5)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(OfficeOfDestinationPage, customsOffice)
            .setValue(T2DeclarationTypeYesNoPage, true)
            .setValue(OfficeOfTransitCountryPage(index), country)
            .setValue(OfficeOfTransitPage(index), officeOfTransit)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

          val expectedResult = TransitDomain(
            isT2DeclarationType = Some(true),
            officesOfTransit = Seq(OfficeOfTransitDomain(Some(country), officeOfTransit, None)(index))
          )

          val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(Nil, Nil)).run(userAnswers)

          result.value mustBe expectedResult
        }

        "and no items are T2 declaration type" - {
          "and country code for office of departure is in set CL112" in {
            val userAnswers = emptyUserAnswers
              .setValue(OfficeOfDeparturePage, customsOfficeFromListedCountry)
              .setValue(DeclarationTypePage, Option5)
              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
              .setValue(OfficeOfDestinationPage, customsOffice)
              .setValue(T2DeclarationTypeYesNoPage, false)
              .setValue(OfficeOfTransitCountryPage(index), country)
              .setValue(OfficeOfTransitPage(index), officeOfTransit)
              .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

            val expectedResult = TransitDomain(
              isT2DeclarationType = Some(false),
              officesOfTransit = Seq(OfficeOfTransitDomain(Some(country), officeOfTransit, None)(index))
            )

            val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

            result.value mustBe expectedResult
          }

          "and country code for office of destination is in set CL112" in {
            val userAnswers = emptyUserAnswers
              .setValue(OfficeOfDeparturePage, customsOffice)
              .setValue(DeclarationTypePage, Option5)
              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
              .setValue(OfficeOfDestinationPage, customsOfficeFromListedCountry)
              .setValue(T2DeclarationTypeYesNoPage, false)
              .setValue(OfficeOfTransitPage(index), officeOfTransit)
              .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

            val expectedResult = TransitDomain(
              isT2DeclarationType = Some(false),
              officesOfTransit = Seq(OfficeOfTransitDomain(None, officeOfTransit, None)(index))
            )

            val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

            result.value mustBe expectedResult
          }

          "and country code for neither office of departure nor office of destination is in set CL112" - {

            "and at least one country of routing is in set CL112" in {
              val userAnswers = emptyUserAnswers
                .setValue(OfficeOfDeparturePage, customsOfficeFromUnlistedCountry)
                .setValue(DeclarationTypePage, Option5)
                .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                .setValue(OfficeOfDestinationPage, customsOfficeFromUnlistedCountry)
                .setValue(BindingItineraryPage, true)
                .setValue(CountryOfRoutingPage(index), country)
                .setValue(T2DeclarationTypeYesNoPage, false)
                .setValue(OfficeOfTransitCountryPage(index), country)
                .setValue(OfficeOfTransitPage(index), officeOfTransit)
                .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

              val expectedResult = TransitDomain(
                isT2DeclarationType = Some(false),
                officesOfTransit = Seq(OfficeOfTransitDomain(Some(country), officeOfTransit, None)(index))
              )

              val result: EitherType[TransitDomain] =
                UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

              result.value mustBe expectedResult
            }

            "and no countries of routing are in set CL112" in {
              val country = arbitrary[Country]
                .retryUntil(
                  x => !countryCodes.contains(x.code.code)
                )
                .sample
                .value

              val userAnswers = emptyUserAnswers
                .setValue(OfficeOfDeparturePage, customsOfficeFromUnlistedCountry)
                .setValue(DeclarationTypePage, Option5)
                .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                .setValue(OfficeOfDestinationPage, customsOfficeFromUnlistedCountry)
                .setValue(BindingItineraryPage, true)
                .setValue(CountryOfRoutingPage(index), country)
                .setValue(T2DeclarationTypeYesNoPage, false)
                .setValue(AddOfficeOfTransitYesNoPage, false)

              val expectedResult = TransitDomain(
                isT2DeclarationType = Some(false),
                officesOfTransit = Nil
              )

              val result: EitherType[TransitDomain] =
                UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

              result.value mustBe expectedResult
            }
          }
        }
      }

      "when declaration type is neither T nor T2" - {

        val declarationType = Gen.oneOf(Option1, Option3).sample.value

        "and country code for office of departure is in set CL112" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOfficeFromListedCountry)
            .setValue(DeclarationTypePage, declarationType)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(OfficeOfDestinationPage, customsOffice)
            .setValue(OfficeOfTransitCountryPage(index), country)
            .setValue(OfficeOfTransitPage(index), officeOfTransit)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

          val expectedResult = TransitDomain(
            isT2DeclarationType = None,
            officesOfTransit = Seq(OfficeOfTransitDomain(Some(country), officeOfTransit, None)(index))
          )

          val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

          result.value mustBe expectedResult
        }

        "and country code for office of destination is in set CL112" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOffice)
            .setValue(DeclarationTypePage, declarationType)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(OfficeOfDestinationPage, customsOfficeFromListedCountry)
            .setValue(OfficeOfTransitPage(index), officeOfTransit)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

          val expectedResult = TransitDomain(
            isT2DeclarationType = None,
            officesOfTransit = Seq(OfficeOfTransitDomain(None, officeOfTransit, None)(index))
          )

          val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

          result.value mustBe expectedResult
        }

        "and country code for neither office of departure nor office of destination is in set CL112" - {

          "and at least one country of routing is in set CL112" in {
            val userAnswers = emptyUserAnswers
              .setValue(OfficeOfDeparturePage, customsOfficeFromUnlistedCountry)
              .setValue(DeclarationTypePage, declarationType)
              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
              .setValue(OfficeOfDestinationPage, customsOfficeFromUnlistedCountry)
              .setValue(BindingItineraryPage, true)
              .setValue(CountryOfRoutingPage(index), country)
              .setValue(OfficeOfTransitCountryPage(index), country)
              .setValue(OfficeOfTransitPage(index), officeOfTransit)
              .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

            val expectedResult = TransitDomain(
              isT2DeclarationType = None,
              officesOfTransit = Seq(OfficeOfTransitDomain(Some(country), officeOfTransit, None)(index))
            )

            val result: EitherType[TransitDomain] =
              UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

            result.value mustBe expectedResult
          }

          "and no countries of routing are in set CL112" in {
            val country = arbitrary[Country]
              .retryUntil(
                x => !countryCodes.contains(x.code.code)
              )
              .sample
              .value

            val userAnswers = emptyUserAnswers
              .setValue(OfficeOfDeparturePage, customsOfficeFromUnlistedCountry)
              .setValue(DeclarationTypePage, declarationType)
              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
              .setValue(OfficeOfDestinationPage, customsOfficeFromUnlistedCountry)
              .setValue(BindingItineraryPage, true)
              .setValue(CountryOfRoutingPage(index), country)
              .setValue(AddOfficeOfTransitYesNoPage, false)

            val expectedResult = TransitDomain(
              isT2DeclarationType = None,
              officesOfTransit = Nil
            )

            val result: EitherType[TransitDomain] =
              UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

            result.value mustBe expectedResult
          }
        }
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when offices of departure and destination are in CL112 and have same country code" - {
        "and add office of transit yes/no unanswered" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOfficeFromListedCountry)
            .setValue(OfficeOfDestinationPage, customsOfficeFromListedCountry)

          val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

          result.left.value.page mustBe AddOfficeOfTransitYesNoPage
        }
      }

      "when declaration type is T2" - {
        "and office of destination in set CL112" - {
          "and empty json at index 0" in {
            val userAnswers = emptyUserAnswers
              .setValue(OfficeOfDeparturePage, customsOfficeFromUnlistedCountry)
              .setValue(DeclarationTypePage, Option2)
              .setValue(OfficeOfDestinationPage, customsOfficeFromListedCountry)

            val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

            result.left.value.page mustBe OfficeOfTransitPage(Index(0))
          }
        }

        "and office of destination not in set CL112" - {
          "and empty json at index 0" in {
            val userAnswers = emptyUserAnswers
              .setValue(OfficeOfDeparturePage, customsOfficeFromUnlistedCountry)
              .setValue(DeclarationTypePage, Option2)
              .setValue(OfficeOfDestinationPage, customsOfficeFromUnlistedCountry)

            val result: EitherType[TransitDomain] = UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

            result.left.value.page mustBe OfficeOfTransitCountryPage(Index(0))
          }
        }
      }

      "when declaration type is neither T nor T2" - {

        val declarationType = Gen.oneOf(Option1, Option3).sample.value

        "and country code for neither office of departure nor office of destination is in set CL112" - {
          "and no countries of routing are in set CL112" - {
            "and add office of transit yes/no unanswered" in {
              val country = arbitrary[Country]
                .retryUntil(
                  x => !countryCodes.contains(x.code.code)
                )
                .sample
                .value

              val userAnswers = emptyUserAnswers
                .setValue(OfficeOfDeparturePage, customsOfficeFromUnlistedCountry)
                .setValue(DeclarationTypePage, declarationType)
                .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                .setValue(OfficeOfDestinationPage, customsOfficeFromUnlistedCountry)
                .setValue(BindingItineraryPage, true)
                .setValue(CountryOfRoutingPage(index), country)

              val result: EitherType[TransitDomain] =
                UserAnswersReader[TransitDomain](TransitDomain.userAnswersReader(countryCodes, Nil)).run(userAnswers)

              result.left.value.page mustBe AddOfficeOfTransitYesNoPage
            }
          }
        }
      }
    }
  }
}
