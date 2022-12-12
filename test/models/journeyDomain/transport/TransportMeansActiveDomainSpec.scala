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

package models.journeyDomain.transport

import base.SpecBase
import generators.Generators
import models.SecurityDetailsType.{EntrySummaryDeclarationSecurityDetails, NoSecurityDetails}
import models.{Index, SecurityDetailsType}
import models.domain.{EitherType, UserAnswersReader}
import models.reference.{CustomsOffice, Nationality}
import models.transport.transportMeans.BorderModeOfTransport
import models.transport.transportMeans.BorderModeOfTransport._
import models.transport.transportMeans.active.Identification
import models.transport.transportMeans.active.Identification._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.SecurityDetailsTypePage
import pages.transport.transportMeans.BorderModeOfTransportPage
import pages.transport.transportMeans.active._

class TransportMeansActiveDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "TransportMeansActiveDomain" - {

    val identification: Identification = arbitrary[Identification].sample.value
    val identificationNumber: String   = Gen.alphaNumStr.sample.value
    val nationality: Nationality       = arbitrary[Nationality].sample.value
    val customsOffice: CustomsOffice   = arbitrary[CustomsOffice].sample.value
    val conveyanceNumber: String       = Gen.alphaNumStr.sample.value

    "can be parsed from user answers" - {

      "when the add nationality is answered yes" - {
        "and security detail type is 0 and inland mode is Maritime and add conveyance number is yes" in {
          val userAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(BorderModeOfTransportPage, Maritime)
            .setValue(IdentificationPage(index), identification)
            .setValue(IdentificationNumberPage(index), identificationNumber)
            .setValue(AddNationalityYesNoPage(index), true)
            .setValue(NationalityPage(index), nationality)
            .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)
            .setValue(ConveyanceReferenceNumberYesNoPage(index), true)
            .setValue(ConveyanceReferenceNumberPage(index), conveyanceNumber)

          val expectedResult = TransportMeansActiveDomain(
            identification = identification,
            identificationNumber = identificationNumber,
            nationality = Option(nationality),
            customsOffice = customsOffice,
            conveyanceReferenceNumber = Some(conveyanceNumber)
          )

          val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
            TransportMeansActiveDomain.userAnswersReader(index)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }

        "and security detail type is 1 and inland mode is Air" in {
          val userAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, EntrySummaryDeclarationSecurityDetails)
            .setValue(BorderModeOfTransportPage, Air)
            .setValue(IdentificationPage(index), identification)
            .setValue(IdentificationNumberPage(index), identificationNumber)
            .setValue(AddNationalityYesNoPage(index), true)
            .setValue(NationalityPage(index), nationality)
            .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)
            .setValue(ConveyanceReferenceNumberPage(index), conveyanceNumber)

          val expectedResult = TransportMeansActiveDomain(
            identification = identification,
            identificationNumber = identificationNumber,
            nationality = Option(nationality),
            customsOffice = customsOffice,
            conveyanceReferenceNumber = Some(conveyanceNumber)
          )

          val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
            TransportMeansActiveDomain.userAnswersReader(index)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }
      }

      "when the add nationality is answered no" - {
        "and security detail type is 0 and inland mode is Maritime and add conveyance number is no" in {
          val userAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(BorderModeOfTransportPage, Maritime)
            .setValue(IdentificationPage(index), identification)
            .setValue(IdentificationNumberPage(index), identificationNumber)
            .setValue(AddNationalityYesNoPage(index), false)
            .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)
            .setValue(ConveyanceReferenceNumberYesNoPage(index), false)

          val expectedResult = TransportMeansActiveDomain(
            identification = identification,
            identificationNumber = identificationNumber,
            nationality = None,
            customsOffice = customsOffice,
            conveyanceReferenceNumber = None
          )

          val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
            TransportMeansActiveDomain.userAnswersReader(index)
          ).run(userAnswers)

          result.value mustBe expectedResult
        }
      }

    }

    "can not be parsed from user answers" - {

      "when index is 0" - {
        val index = Index(0)

        "and border mode is 2 (Rail)" - {
          "must bypass identification type and go to identification number" in {
            val userAnswers = emptyUserAnswers
              .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Rail)

            val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
              TransportMeansActiveDomain.userAnswersReader(index)
            ).run(userAnswers)

            result.left.value.page mustBe IdentificationNumberPage(index)
          }
        }

        "and border mode is 3 (Road)" - {
          "must bypass identification type and go to identification number" in {
            val userAnswers = emptyUserAnswers
              .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Road)

            val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
              TransportMeansActiveDomain.userAnswersReader(index)
            ).run(userAnswers)

            result.left.value.page mustBe IdentificationNumberPage(index)
          }
        }

        "and border mode is 1, 4, 5, 7, 8" - {
          val gen = Gen.oneOf(Maritime, Air, Mail, Fixed, Waterway)
          "must ask identification type" in {
            forAll(gen) {
              borderMode =>
                val userAnswers = emptyUserAnswers.setValue(BorderModeOfTransportPage, borderMode)

                val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
                  TransportMeansActiveDomain.userAnswersReader(index)
                ).run(userAnswers)

                result.left.value.page mustBe IdentificationPage(index)
            }
          }
        }
      }

      "when index is not 0" - {
        val index = Index(1)
        "and border mode is anything" - {
          val gen = arbitrary[BorderModeOfTransport]
          "must ask identification type" in {
            forAll(gen) {
              borderMode =>
                val userAnswers = emptyUserAnswers.setValue(BorderModeOfTransportPage, borderMode)

                val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
                  TransportMeansActiveDomain.userAnswersReader(index)
                ).run(userAnswers)

                result.left.value.page mustBe IdentificationPage(index)
            }
          }
        }

      }

      "when add nationality is unanswered" in {
        val userAnswers = emptyUserAnswers
          .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Road)
          .setValue(IdentificationNumberPage(index), identificationNumber)

        val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
          TransportMeansActiveDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.left.value.page mustBe AddNationalityYesNoPage(index)
      }

      "when nationality is unanswered" in {
        val userAnswers = emptyUserAnswers
          .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Road)
          .setValue(IdentificationNumberPage(index), identificationNumber)
          .setValue(AddNationalityYesNoPage(index), true)

        val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
          TransportMeansActiveDomain.userAnswersReader(index)
        ).run(userAnswers)

        result.left.value.page mustBe NationalityPage(index)
      }

      "when customs office ref. number is unanswered" - {
        "and add nationality is true" in {
          val userAnswers = emptyUserAnswers
            .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Road)
            .setValue(IdentificationNumberPage(index), identificationNumber)
            .setValue(AddNationalityYesNoPage(index), true)
            .setValue(NationalityPage(index), nationality)

          val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
            TransportMeansActiveDomain.userAnswersReader(index)
          ).run(userAnswers)

          result.left.value.page mustBe CustomsOfficeActiveBorderPage(index)
        }

        "and add nationality is false" in {
          val userAnswers = emptyUserAnswers
            .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Road)
            .setValue(IdentificationNumberPage(index), identificationNumber)
            .setValue(AddNationalityYesNoPage(index), false)

          val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
            TransportMeansActiveDomain.userAnswersReader(index)
          ).run(userAnswers)

          result.left.value.page mustBe CustomsOfficeActiveBorderPage(index)
        }
      }

      "when security is in set {1,2,3}" - {
        "and border mode of transport is 4 (Air)" - {
          val securityGen       = arbitrary[SecurityDetailsType](arbitrarySomeSecurityDetailsType)
          val identificationGen = Gen.oneOf(IataFlightNumber, RegNumberAircraft)
          forAll(securityGen, identificationGen) {
            (securityType, identification) =>
              val userAnswers = emptyUserAnswers
                .setValue(SecurityDetailsTypePage, securityType)
                .setValue(BorderModeOfTransportPage, BorderModeOfTransport.Air)
                .setValue(IdentificationPage(index), identification)
                .setValue(IdentificationNumberPage(index), identificationNumber)
                .setValue(AddNationalityYesNoPage(index), false)
                .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)

              val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
                TransportMeansActiveDomain.userAnswersReader(index)
              ).run(userAnswers)

              result.left.value.page mustBe ConveyanceReferenceNumberPage(index)
          }
        }

        "and border mode of transport is not 4 (Air)" in {
          val securityGen       = arbitrary[SecurityDetailsType](arbitrarySomeSecurityDetailsType)
          val borderModeGen     = Gen.oneOf(BorderModeOfTransport.values.filterNot(_ == Air))
          val identificationGen = Gen.oneOf(IataFlightNumber, RegNumberAircraft)
          forAll(securityGen, borderModeGen, identificationGen) {
            (securityType, borderMode, identification) =>
              val userAnswers = emptyUserAnswers
                .setValue(SecurityDetailsTypePage, securityType)
                .setValue(BorderModeOfTransportPage, borderMode)
                .setValue(IdentificationPage(index), identification)
                .setValue(IdentificationNumberPage(index), identificationNumber)
                .setValue(AddNationalityYesNoPage(index), false)
                .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)

              val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
                TransportMeansActiveDomain.userAnswersReader(index)
              ).run(userAnswers)

              result.left.value.page mustBe ConveyanceReferenceNumberYesNoPage(index)
          }
        }
      }

      "when security is 0 (No security)" - {
        val borderModeGen     = arbitrary[BorderModeOfTransport]
        val identificationGen = arbitrary[Identification]
        forAll(borderModeGen, identificationGen) {
          (borderMode, identification) =>
            val userAnswers = emptyUserAnswers
              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
              .setValue(BorderModeOfTransportPage, borderMode)
              .setValue(IdentificationPage(index), identification)
              .setValue(IdentificationNumberPage(index), identificationNumber)
              .setValue(AddNationalityYesNoPage(index), false)
              .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)

            val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
              TransportMeansActiveDomain.userAnswersReader(index)
            ).run(userAnswers)

            result.left.value.page mustBe ConveyanceReferenceNumberYesNoPage(index)
        }
      }

      "when conveyance reference number needs to be answered" in {
        val borderModeGen     = arbitrary[BorderModeOfTransport]
        val identificationGen = arbitrary[Identification]
        forAll(borderModeGen, identificationGen) {
          (borderMode, identification) =>
            val userAnswers = emptyUserAnswers
              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
              .setValue(BorderModeOfTransportPage, borderMode)
              .setValue(IdentificationPage(index), identification)
              .setValue(IdentificationNumberPage(index), identificationNumber)
              .setValue(AddNationalityYesNoPage(index), false)
              .setValue(CustomsOfficeActiveBorderPage(index), customsOffice)
              .setValue(ConveyanceReferenceNumberYesNoPage(index), true)

            val result: EitherType[TransportMeansActiveDomain] = UserAnswersReader[TransportMeansActiveDomain](
              TransportMeansActiveDomain.userAnswersReader(index)
            ).run(userAnswers)

            result.left.value.page mustBe ConveyanceReferenceNumberPage(index)
        }
      }
    }
  }
}
