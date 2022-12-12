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
import models.domain.{EitherType, UserAnswersReader}
import models.reference.Nationality
import models.transport.transportMeans.departure.{Identification, InlandMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.transportMeans.departure._

class TransportMeansDepartureDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "TransportMeansDepartureDomain" - {

    val identification: Identification = arbitrary[Identification].sample.value
    val identificationNumber: String   = Gen.alphaNumStr.sample.value
    val nationality: Nationality       = arbitrary[Nationality].sample.value

    "can be parsed from user answers" - {

      "when the InlandMode is 'Unknown'" in {
        val userAnswers = emptyUserAnswers
          .setValue(InlandModePage, InlandMode.Unknown)
          .setValue(MeansIdentificationNumberPage, identificationNumber)
          .setValue(VehicleCountryPage, nationality)

        val expectedResult = TransportMeansDomainWithUnknownInlandMode(identificationNumber, nationality)

        val result: EitherType[TransportMeansDepartureDomain] = UserAnswersReader[TransportMeansDepartureDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when the InlandMode is not 'Mail' or 'Unknown'" in {
        forAll(arbitrary[InlandMode](arbitraryNonMailOrUnknownInlandMode)) {
          inlandMode =>
            val userAnswers = emptyUserAnswers
              .setValue(InlandModePage, inlandMode)
              .setValue(IdentificationPage, identification)
              .setValue(MeansIdentificationNumberPage, identificationNumber)
              .setValue(VehicleCountryPage, nationality)

            val expectedResult = TransportMeansDomainWithAnyOtherInlandMode(inlandMode, identification, identificationNumber, nationality)

            val result: EitherType[TransportMeansDepartureDomain] = UserAnswersReader[TransportMeansDepartureDomain].run(userAnswers)

            result.value mustBe expectedResult
        }
      }
    }

    "can not be parsed from user answers" - {
      "when answers are empty" in {
        val result: EitherType[TransportMeansDepartureDomain] = UserAnswersReader[TransportMeansDepartureDomain].run(emptyUserAnswers)

        result.left.value.page mustBe InlandModePage
      }

      "when the InlandMode is 'Mail'" in {
        val userAnswers = emptyUserAnswers
          .setValue(InlandModePage, InlandMode.Mail)

        val result: EitherType[TransportMeansDepartureDomain] = UserAnswersReader[TransportMeansDepartureDomain].run(userAnswers)

        result.left.value.page mustBe InlandModePage
      }

      "when inland mode is 'Unknown'" - {
        "and identification number page is missing" in {
          val userAnswers = emptyUserAnswers
            .setValue(InlandModePage, InlandMode.Unknown)

          val result: EitherType[TransportMeansDepartureDomain] = UserAnswersReader[TransportMeansDepartureDomain].run(userAnswers)

          result.left.value.page mustBe MeansIdentificationNumberPage
        }

        "and vehicle country page is missing" in {
          val userAnswers = emptyUserAnswers
            .setValue(InlandModePage, InlandMode.Unknown)
            .setValue(MeansIdentificationNumberPage, identificationNumber)

          val result: EitherType[TransportMeansDepartureDomain] = UserAnswersReader[TransportMeansDepartureDomain].run(userAnswers)

          result.left.value.page mustBe VehicleCountryPage
        }
      }

      "when inland mode is neither 'Mail' nor 'Unknown'" - {
        "and identification page is missing" in {
          forAll(arbitrary[InlandMode](arbitraryNonMailOrUnknownInlandMode)) {
            inlandMode =>
              val userAnswers = emptyUserAnswers
                .setValue(InlandModePage, inlandMode)

              val result: EitherType[TransportMeansDepartureDomain] = UserAnswersReader[TransportMeansDepartureDomain].run(userAnswers)

              result.left.value.page mustBe IdentificationPage
          }
        }

        "and identification number page is missing" in {
          forAll(arbitrary[InlandMode](arbitraryNonMailOrUnknownInlandMode)) {
            inlandMode =>
              val userAnswers = emptyUserAnswers
                .setValue(InlandModePage, inlandMode)
                .setValue(IdentificationPage, identification)

              val result: EitherType[TransportMeansDepartureDomain] = UserAnswersReader[TransportMeansDepartureDomain].run(userAnswers)

              result.left.value.page mustBe MeansIdentificationNumberPage
          }
        }

        "and vehicle country page is missing" in {
          forAll(arbitrary[InlandMode](arbitraryNonMailOrUnknownInlandMode)) {
            inlandMode =>
              val userAnswers = emptyUserAnswers
                .setValue(InlandModePage, inlandMode)
                .setValue(IdentificationPage, identification)
                .setValue(MeansIdentificationNumberPage, identificationNumber)

              val result: EitherType[TransportMeansDepartureDomain] = UserAnswersReader[TransportMeansDepartureDomain].run(userAnswers)

              result.left.value.page mustBe VehicleCountryPage
          }
        }
      }
    }
  }
}
