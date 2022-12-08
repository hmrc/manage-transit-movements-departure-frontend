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
import models.Index
import models.SecurityDetailsType._
import models.domain.{EitherType, UserAnswersReader}
import models.transport.transportMeans.BorderModeOfTransport
import org.scalacheck.Gen
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.SecurityDetailsTypePage
import pages.transport.transportMeans.{active, AnotherVehicleCrossingYesNoPage, BorderModeOfTransportPage}

class TransportMeansDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "TransportMeansDomain" - {
    "can be parsed from user answers" - {
      "when security type is in Set{0}" - {
        "and another vehicle crossing border is true" in {

          val initialAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(AnotherVehicleCrossingYesNoPage, true)

          forAll(arbitraryTransportMeansActiveAnswers(initialAnswers, index)) {
            answers =>
              val result: EitherType[Option[Seq[TransportMeansActiveDomain]]] = UserAnswersReader[Option[Seq[TransportMeansActiveDomain]]](
                TransportMeansDomain.transportMeansActiveReader
              ).run(answers)

              result.value mustBe defined
          }
        }

        "and another vehicle crossing border is false" in {

          val initialAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(AnotherVehicleCrossingYesNoPage, false)

          forAll(arbitraryTransportMeansActiveAnswers(initialAnswers, index)) {
            answers =>
              val result: EitherType[Option[Seq[TransportMeansActiveDomain]]] = UserAnswersReader[Option[Seq[TransportMeansActiveDomain]]](
                TransportMeansDomain.transportMeansActiveReader
              ).run(answers)

              result.value must not be defined
          }
        }
      }

      "when security type is in Set{1, 2, 3}" in {

        val securityType =
          Gen.oneOf(EntrySummaryDeclarationSecurityDetails, ExitSummaryDeclarationSecurityDetails, EntryAndExitSummaryDeclarationSecurityDetails).sample.value

        val initialAnswers = emptyUserAnswers
          .setValue(SecurityDetailsTypePage, securityType)

        forAll(arbitraryTransportMeansActiveAnswers(initialAnswers, index)) {
          answers =>
            val result: EitherType[Option[Seq[TransportMeansActiveDomain]]] = UserAnswersReader[Option[Seq[TransportMeansActiveDomain]]](
              TransportMeansDomain.transportMeansActiveReader
            ).run(answers)

            result.value mustBe defined
        }

      }
    }

    "cannot be parsed from user answers" - {
      "when add another vehicle crossing border is true" - {
        "and BorderModeOfTransport is unanswered" in {
          val userAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(AnotherVehicleCrossingYesNoPage, true)

          val result: EitherType[Option[Seq[TransportMeansActiveDomain]]] = UserAnswersReader[Option[Seq[TransportMeansActiveDomain]]](
            TransportMeansDomain.transportMeansActiveReader
          ).run(userAnswers)

          result.left.value.page mustBe BorderModeOfTransportPage
        }

        "and identification type crossing the border is unanswered" in {
          val userAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, NoSecurityDetails)
            .setValue(AnotherVehicleCrossingYesNoPage, true)
            .setValue(BorderModeOfTransportPage, arbitrary[BorderModeOfTransport].sample.value)

          val result: EitherType[Option[Seq[TransportMeansActiveDomain]]] = UserAnswersReader[Option[Seq[TransportMeansActiveDomain]]](
            TransportMeansDomain.transportMeansActiveReader
          ).run(userAnswers)

          result.left.value.page mustBe active.IdentificationPage(Index(0))
        }
      }
    }
  }
}
