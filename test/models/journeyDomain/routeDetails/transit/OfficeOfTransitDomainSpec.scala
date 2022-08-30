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
import config.Constants._
import generators.Generators
import models.SecurityDetailsType._
import models.domain.{EitherType, UserAnswersReader}
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{DateTime, Index}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.preTaskList.{OfficeOfDeparturePage, SecurityDetailsTypePage}
import pages.routeDetails.routing.OfficeOfDestinationPage
import pages.routeDetails.transit.index.{AddOfficeOfTransitETAYesNoPage, OfficeOfTransitCountryPage, OfficeOfTransitETAPage, OfficeOfTransitPage}

class OfficeOfTransitDomainSpec extends SpecBase with Generators {

  "OfficeOfTransitDomain" - {

    val countryCode   = arbitrary[CountryCode].sample.value.code
    val countryCodes  = Seq(countryCode)
    def customsOffice = arbitrary[CustomsOffice].sample.value.copy(id = countryCode)

    val country = arbitrary[Country].sample.value

    val eta = arbitrary[DateTime].sample.value

    val securityType1Or3    = Gen.oneOf(EntrySummaryDeclarationSecurityDetails, EntryAndExitSummaryDeclarationSecurityDetails).sample.value
    val securityTypeNot1Or3 = Gen.oneOf(NoSecurityDetails, ExitSummaryDeclarationSecurityDetails).sample.value

    val officeOfTransit = customsOffice

    "can be parsed from UserAnswers" - {
      "when first in sequence" - {
        val index = Index(0)

        "and office of destination is in set CL112" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOffice)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(OfficeOfDestinationPage, customsOffice)
            .setValue(OfficeOfTransitPage(index), officeOfTransit)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

          val expectedResult = OfficeOfTransitDomain(
            country = None,
            customsOffice = officeOfTransit,
            officeOfTransitETA = None
          )(index)

          val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
            OfficeOfTransitDomain.userAnswersReader(index, countryCodes, Nil, Nil)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

        "and office of departure is in 'GB' and office of destination is in set CL010" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOffice.copy(id = GB))
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(OfficeOfDestinationPage, customsOffice)
            .setValue(OfficeOfTransitPage(index), officeOfTransit)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

          val expectedResult = OfficeOfTransitDomain(
            country = None,
            customsOffice = officeOfTransit,
            officeOfTransitETA = None
          )(index)

          val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
            OfficeOfTransitDomain.userAnswersReader(index, Nil, countryCodes, Nil)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

        "and office of destination is in 'AD'" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOffice)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(OfficeOfDestinationPage, customsOffice.copy(id = AD))
            .setValue(OfficeOfTransitPage(index), officeOfTransit)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

          val expectedResult = OfficeOfTransitDomain(
            country = None,
            customsOffice = officeOfTransit,
            officeOfTransitETA = None
          )(index)

          val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
            OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, Nil)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

        "and office of destination is not in 'AD'" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOffice)
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(OfficeOfDestinationPage, customsOffice)
            .setValue(OfficeOfTransitCountryPage(index), country)
            .setValue(OfficeOfTransitPage(index), officeOfTransit)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

          val expectedResult = OfficeOfTransitDomain(
            country = Some(country),
            customsOffice = officeOfTransit,
            officeOfTransitETA = None
          )(index)

          val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
            OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, Nil)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }
      }

      "when not first in sequence" in {
        val index = Index(1)

        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(OfficeOfDestinationPage, customsOffice)
          .setValue(OfficeOfTransitCountryPage(Index(0)), country)
          .setValue(OfficeOfTransitCountryPage(index), country)
          .setValue(OfficeOfTransitPage(index), officeOfTransit)
          .setValue(AddOfficeOfTransitETAYesNoPage(index), true)
          .setValue(OfficeOfTransitETAPage(index), eta)

        val expectedResult = OfficeOfTransitDomain(
          country = Some(country),
          customsOffice = officeOfTransit,
          officeOfTransitETA = Some(eta)
        )(index)

        val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
          OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, Nil)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when security type is one of 'entrySummaryDeclaration' or 'entryAndExitSummaryDeclaration'" - {
        "and office of transit is in set CL147" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOffice)
            .setValue(SecurityDetailsTypePage, securityType1Or3)
            .setValue(OfficeOfDestinationPage, customsOffice)
            .setValue(OfficeOfTransitCountryPage(index), country)
            .setValue(OfficeOfTransitPage(index), officeOfTransit)
            .setValue(OfficeOfTransitETAPage(index), eta)

          val expectedResult = OfficeOfTransitDomain(
            country = Some(country),
            customsOffice = officeOfTransit,
            officeOfTransitETA = Some(eta)
          )(index)

          val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
            OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, countryCodes)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

        "and office of transit is not in CL147" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOffice)
            .setValue(SecurityDetailsTypePage, securityType1Or3)
            .setValue(OfficeOfDestinationPage, customsOffice)
            .setValue(OfficeOfTransitCountryPage(index), country)
            .setValue(OfficeOfTransitPage(index), officeOfTransit)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

          val expectedResult = OfficeOfTransitDomain(
            country = Some(country),
            customsOffice = officeOfTransit,
            officeOfTransitETA = None
          )(index)

          val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
            OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, Nil)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }
      }

      "when security type is not one of 'entrySummaryDeclaration' or 'entryAndExitSummaryDeclaration'" in {
        val userAnswers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(SecurityDetailsTypePage, securityTypeNot1Or3)
          .setValue(OfficeOfDestinationPage, customsOffice)
          .setValue(OfficeOfTransitCountryPage(index), country)
          .setValue(OfficeOfTransitPage(index), officeOfTransit)
          .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

        val expectedResult = OfficeOfTransitDomain(
          country = Some(country),
          customsOffice = officeOfTransit,
          officeOfTransitETA = None
        )(index)

        val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
          OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, Nil)
        ).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from user answers" - {
      "when first in sequence" in {}

      "when not first in sequence" - {
        val index = Index(1)

        "when country missing" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfTransitCountryPage(Index(0)), country)

          val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
            OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, Nil)
          ).run(userAnswers)

          result.left.value.page mustBe OfficeOfTransitCountryPage(index)
        }

        "when office missing" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfTransitCountryPage(Index(0)), country)
            .setValue(OfficeOfTransitCountryPage(index), country)

          val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
            OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, Nil)
          ).run(userAnswers)

          result.left.value.page mustBe OfficeOfTransitPage(index)
        }

        "when security type is one of 'entrySummaryDeclaration' or 'entryAndExitSummaryDeclaration'" - {
          "and office of transit is in set CL147" - {
            "and eta missing" in {
              val userAnswers = emptyUserAnswers
                .setValue(SecurityDetailsTypePage, securityType1Or3)
                .setValue(OfficeOfTransitCountryPage(Index(0)), country)
                .setValue(OfficeOfTransitCountryPage(index), country)
                .setValue(OfficeOfTransitPage(index), officeOfTransit)

              val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
                OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, countryCodes)
              ).run(userAnswers)

              result.left.value.page mustBe OfficeOfTransitETAPage(index)
            }
          }

          "and office of transit is not in set CL147" - {
            "and eta yes/no missing" in {
              val userAnswers = emptyUserAnswers
                .setValue(SecurityDetailsTypePage, securityType1Or3)
                .setValue(OfficeOfTransitCountryPage(Index(0)), country)
                .setValue(OfficeOfTransitCountryPage(index), country)
                .setValue(OfficeOfTransitPage(index), officeOfTransit)

              val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
                OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, Nil)
              ).run(userAnswers)

              result.left.value.page mustBe AddOfficeOfTransitETAYesNoPage(index)
            }
          }
        }

        "when security type is not one of 'entrySummaryDeclaration' or 'entryAndExitSummaryDeclaration'" - {
          "and eta yes/no missing" in {
            val userAnswers = emptyUserAnswers
              .setValue(SecurityDetailsTypePage, securityTypeNot1Or3)
              .setValue(OfficeOfTransitCountryPage(Index(0)), country)
              .setValue(OfficeOfTransitCountryPage(index), country)
              .setValue(OfficeOfTransitPage(index), officeOfTransit)

            val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
              OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, countryCodes)
            ).run(userAnswers)

            result.left.value.page mustBe AddOfficeOfTransitETAYesNoPage(index)
          }
        }

        "when eta yes/no is true and eta missing" in {
          val userAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, securityTypeNot1Or3)
            .setValue(OfficeOfTransitCountryPage(Index(0)), country)
            .setValue(OfficeOfTransitCountryPage(index), country)
            .setValue(OfficeOfTransitPage(index), officeOfTransit)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), true)

          val result: EitherType[OfficeOfTransitDomain] = UserAnswersReader[OfficeOfTransitDomain](
            OfficeOfTransitDomain.userAnswersReader(index, Nil, Nil, Nil)
          ).run(userAnswers)

          result.left.value.page mustBe OfficeOfTransitETAPage(index)
        }
      }
    }
  }
}
