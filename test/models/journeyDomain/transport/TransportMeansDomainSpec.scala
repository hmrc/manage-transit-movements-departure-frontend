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
import models.InlandMode
import models.InlandMode._
import models.domain.{EitherType, UserAnswersReader}
import models.reference.Nationality
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.QuestionPage
import pages.transport.transportMeans.departure.{InlandModePage, VehicleCountryPage}

class TransportMeansDomainSpec extends SpecBase with Generators {

  "TransportMeansDomain" - {

    val inlandMode: InlandMode   = arbitrary[InlandMode].sample.value
    val nationality: Nationality = arbitrary[Nationality].sample.value

    "can be parsed from user answers" in {
      val userAnswers = emptyUserAnswers
        .setValue(InlandModePage, inlandMode)
        .setValue(VehicleCountryPage, nationality)

      val expectedResult = TransportMeansDomain(
        inlandMode = inlandMode,
        nationality
      )

      val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "can not be parsed from user answers" - {
      "when answers are empty" in {
        val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain].run(emptyUserAnswers)

        result.left.value.page mustBe InlandModePage
      }

      "when mandatory page is missing" in {
        val mandatoryPages: Seq[QuestionPage[_]] = Seq(
          InlandModePage,
          VehicleCountryPage
        )

        val userAnswers = emptyUserAnswers
          .setValue(InlandModePage, inlandMode)
          .setValue(VehicleCountryPage, nationality)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedAnswers                           = userAnswers.removeValue(mandatoryPage)
            val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain].run(updatedAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }
}
