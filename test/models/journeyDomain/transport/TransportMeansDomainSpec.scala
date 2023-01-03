/*
 * Copyright 2023 HM Revenue & Customs
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
import models.SecurityDetailsType
import models.SecurityDetailsType._
import models.domain.{EitherType, UserAnswersReader}
import models.transport.transportMeans.departure.InlandMode
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.SecurityDetailsTypePage
import pages.transport.transportMeans.AnotherVehicleCrossingYesNoPage
import pages.transport.transportMeans.departure.InlandModePage

class TransportMeansDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "TransportMeansDomain" - {
    "can be parsed from user answers" - {
      "when inland mode is 5 (mail" in {
        val inlandMode = InlandMode.Mail

        val answers = emptyUserAnswers
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(InlandModePage, inlandMode)

        val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain](
          TransportMeansDomain.userAnswersReader
        ).run(answers)

        result.value.inlandMode mustBe inlandMode
      }

      "when inland mode is not 5 (mail)" - {
        val inlandMode = Gen.oneOf(InlandMode.values.filterNot(_ == InlandMode.Mail)).sample.value

        "and security type is in Set{0}" - {
          "and another vehicle crossing border is true" in {
            val initialAnswers = emptyUserAnswers
              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
              .setValue(InlandModePage, inlandMode)
              .setValue(AnotherVehicleCrossingYesNoPage, true)

            forAll(arbitraryTransportMeansAnswers(initialAnswers)) {
              answers =>
                val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain](
                  TransportMeansDomain.userAnswersReader
                ).run(answers)

                result.value.inlandMode mustBe inlandMode
                result.value.asInstanceOf[TransportMeansDomainWithOtherInlandMode].transportMeansActive mustBe defined
            }
          }

          "and another vehicle crossing border is false" in {
            val initialAnswers = emptyUserAnswers
              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
              .setValue(InlandModePage, inlandMode)
              .setValue(AnotherVehicleCrossingYesNoPage, false)

            forAll(arbitraryTransportMeansAnswers(initialAnswers)) {
              answers =>
                val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain](
                  TransportMeansDomain.userAnswersReader
                ).run(answers)

                result.value.inlandMode mustBe inlandMode
                result.value.asInstanceOf[TransportMeansDomainWithOtherInlandMode].transportMeansActive must not be defined
            }
          }
        }

        "and security type is in Set{1, 2, 3}" in {
          val securityType = arbitrary[SecurityDetailsType](arbitrarySomeSecurityDetailsType).sample.value

          val initialAnswers = emptyUserAnswers
            .setValue(SecurityDetailsTypePage, securityType)
            .setValue(InlandModePage, inlandMode)

          forAll(arbitraryTransportMeansAnswers(initialAnswers)) {
            answers =>
              val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain](
                TransportMeansDomain.userAnswersReader
              ).run(answers)

              result.value.inlandMode mustBe inlandMode
              result.value.asInstanceOf[TransportMeansDomainWithOtherInlandMode].transportMeansActive mustBe defined
          }
        }
      }
    }

    "cannot be parsed from user answers" - {
      "when inland mode is unanswered" in {
        val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain](
          TransportMeansDomain.userAnswersReader
        ).run(emptyUserAnswers)

        result.left.value.page mustBe InlandModePage
      }
    }
  }
}
