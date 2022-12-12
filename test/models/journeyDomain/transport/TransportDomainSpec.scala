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
import generators.{Generators, TransportUserAnswersGenerator}
import models.domain.{EitherType, UserAnswersReader}
import models.transport.transportMeans.departure.InlandMode
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.transportMeans.departure.InlandModePage

class TransportDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with TransportUserAnswersGenerator {

  "can be parsed from user answers" - {

    "when inland mode is 5 (mail)" in {
      val initialAnswers = emptyUserAnswers.setValue(InlandModePage, InlandMode.Mail)
      forAll(arbitraryTransportAnswers(initialAnswers)) {
        userAnswers =>
          val result: EitherType[TransportDomain] = UserAnswersReader[TransportDomain].run(userAnswers)

          result.value.inlandMode mustBe InlandMode.Mail
          result.value.transportMeans must not be defined
      }
    }

    "when inland mode is not 5 (mail)" in {
      forAll(Gen.oneOf(InlandMode.values.filterNot(_ == InlandMode.Mail))) {
        inlandMode =>
          val initialAnswers = emptyUserAnswers.setValue(InlandModePage, inlandMode)
          forAll(arbitraryTransportAnswers(initialAnswers)) {
            userAnswers =>
              val result: EitherType[TransportDomain] = UserAnswersReader[TransportDomain].run(userAnswers)

              result.value.inlandMode mustBe inlandMode
              result.value.transportMeans mustBe defined
          }
      }
    }
  }
}
