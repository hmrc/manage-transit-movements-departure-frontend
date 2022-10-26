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
import models.journeyDomain.routeDetails.exit.ExitDomain
import models.journeyDomain.routeDetails.locationOfGoods.LocationOfGoodsDomain
import models.journeyDomain.routeDetails.transit.TransitDomain
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{DeclarationType, Index, SecurityDetailsType}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList._
import pages.routeDetails.locationOfGoods.AddLocationOfGoodsPage
import pages.routeDetails.routing._
import pages.routeDetails.routing.index._

class RouteDetailsDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RouteDetailsUserAnswersGenerator {

  "RouteDetailsDomain" - {

    "transitReader" - {
      "can be parsed from UserAnswers" - {
        "when TIR declaration type" in {
          val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, Option4)

          val result: EitherType[Option[TransitDomain]] = UserAnswersReader[Option[TransitDomain]](
            RouteDetailsDomain.transitReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
          ).run(userAnswers)

          result.value must not be defined
        }

        "when not a TIR declaration type" in {
          val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
          val initialAnswers  = emptyUserAnswers.setValue(DeclarationTypePage, declarationType)

          forAll(arbitraryTransitAnswers(initialAnswers)) {
            answers =>
              val result: EitherType[Option[TransitDomain]] = UserAnswersReader[Option[TransitDomain]](
                RouteDetailsDomain.transitReader(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
              ).run(answers)

              result.value mustBe defined
          }
        }
      }
    }

    "exitReader" - {
      "can be parsed from UserAnswers" - {
        "when TIR declaration type" in {

          val security = Gen.oneOf(SecurityDetailsType.securityValues).sample.value

          val userAnswers = emptyUserAnswers
            .setValue(DeclarationTypePage, Option4)
            .setValue(SecurityDetailsTypePage, security)

          val result: EitherType[Option[ExitDomain]] = UserAnswersReader[Option[ExitDomain]](
            RouteDetailsDomain.exitReader(None)
          ).run(userAnswers)

          result.value must not be defined
        }

        "when not a TIR declaration type" - {
          val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value

          "and security is in set {0,1}" in {
            val security = Gen.oneOf(NoSecurityDetails, EntrySummaryDeclarationSecurityDetails).sample.value

            val userAnswers = emptyUserAnswers
              .setValue(DeclarationTypePage, declarationType)
              .setValue(SecurityDetailsTypePage, security)

            val result: EitherType[Option[ExitDomain]] = UserAnswersReader[Option[ExitDomain]](
              RouteDetailsDomain.exitReader(None)
            ).run(userAnswers)

            result.value must not be defined
          }

          "and security is not in set {0,1}" - {
            val security = Gen.oneOf(ExitSummaryDeclarationSecurityDetails, EntryAndExitSummaryDeclarationSecurityDetails).sample.value

            "at least one of the countries of routing is not in set CL147 and office of transit is populated" - {
              "and office of transit answers have been provided" in {
                val answers = emptyUserAnswers
                  .setValue(DeclarationTypePage, declarationType)
                  .setValue(SecurityDetailsTypePage, security)
                  .setValue(BindingItineraryPage, true)
                  .setValue(CountriesOfRoutingInSecurityAgreement, false)

                forAll(arbitrary[Option[TransitDomain]](arbitraryPopulatedTransitDomain)) {
                  transit =>
                    val result: EitherType[Option[ExitDomain]] = UserAnswersReader[Option[ExitDomain]](
                      RouteDetailsDomain.exitReader(transit)
                    ).run(answers)

                    result.value must not be defined
                }
              }

              "and office of transit answers have not been provided" in {
                val initialAnswers = emptyUserAnswers
                  .setValue(DeclarationTypePage, declarationType)
                  .setValue(SecurityDetailsTypePage, security)
                  .setValue(BindingItineraryPage, true)
                  .setValue(CountriesOfRoutingInSecurityAgreement, false)

                forAll(
                  arbitraryOfficeOfExitAnswers(initialAnswers, index),
                  arbitrary[Option[TransitDomain]](arbitraryEmptyTransitDomain)
                ) {
                  (answers, transit) =>
                    val result: EitherType[Option[ExitDomain]] = UserAnswersReader[Option[ExitDomain]](
                      RouteDetailsDomain.exitReader(transit)
                    ).run(answers)

                    result.value mustBe defined
                }
              }
            }

            "and all of the countries of routing are in set CL147" in {
              val initialAnswers = emptyUserAnswers
                .setValue(DeclarationTypePage, declarationType)
                .setValue(SecurityDetailsTypePage, security)
                .setValue(BindingItineraryPage, true)
                .setValue(CountriesOfRoutingInSecurityAgreement, true)

              forAll(arbitraryOfficeOfExitAnswers(initialAnswers, index)) {
                answers =>
                  val result: EitherType[Option[ExitDomain]] = UserAnswersReader[Option[ExitDomain]](
                    RouteDetailsDomain.exitReader(None)
                  ).run(answers)

                  result.value mustBe defined
              }
            }
          }
        }
      }
    }

    "locationOfGoodsReader" - {
      "can be parsed from UserAnswers" - {
        "when office of departure is in set CL147" - {
          val customsOfficeInCL147 = arbitrary[CustomsOffice]
            .map(_.copy(id = customsSecurityAgreementAreaCountryCodes.head))
            .sample
            .value

          "and not adding a location of goods type" in {
            val userAnswers = emptyUserAnswers
              .setValue(OfficeOfDeparturePage, customsOfficeInCL147)
              .setValue(AddLocationOfGoodsPage, false)

            val result: EitherType[Option[LocationOfGoodsDomain]] = UserAnswersReader[Option[LocationOfGoodsDomain]](
              RouteDetailsDomain.locationOfGoodsReader(customsSecurityAgreementAreaCountryCodes)
            ).run(userAnswers)

            result.value must not be defined
          }

          "and adding a location of goods type" in {
            val initialAnswers = emptyUserAnswers
              .setValue(OfficeOfDeparturePage, customsOfficeInCL147)
              .setValue(AddLocationOfGoodsPage, true)

            forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
              answers =>
                val result: EitherType[Option[LocationOfGoodsDomain]] = UserAnswersReader[Option[LocationOfGoodsDomain]](
                  RouteDetailsDomain.locationOfGoodsReader(customsSecurityAgreementAreaCountryCodes)
                ).run(answers)

                result.value mustBe defined
            }
          }
        }

        "when office of departure is not in set CL147" in {
          val customsOfficeNotInCL147 = arbitrary[CustomsOffice]
            .retryUntil {
              x =>
                !customsSecurityAgreementAreaCountryCodes.contains(x.countryCode)
            }
            .sample
            .value

          val initialAnswers = emptyUserAnswers
            .setValue(OfficeOfDeparturePage, customsOfficeNotInCL147)

          forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            answers =>
              val result: EitherType[Option[LocationOfGoodsDomain]] = UserAnswersReader[Option[LocationOfGoodsDomain]](
                RouteDetailsDomain.locationOfGoodsReader(customsSecurityAgreementAreaCountryCodes)
              ).run(answers)

              result.value mustBe defined
          }
        }
      }
    }
  }
}
