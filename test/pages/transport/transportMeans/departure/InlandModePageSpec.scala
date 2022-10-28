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

package pages.transport.transportMeans.departure

import models.reference.Nationality
import models.transport.transportMeans.departure.{Identification, InlandMode}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class InlandModePageSpec extends PageBehaviours {

  "InlandModePage" - {

    beRetrievable[InlandMode](InlandModePage)

    beSettable[InlandMode](InlandModePage)

    beRemovable[InlandMode](InlandModePage)

    "cleanup" - {
      "when answer changes" - {
        "must remove identification, identification number and vehicle country" in {
          forAll(arbitrary[InlandMode]) {
            inlandMode =>
              val userAnswers = emptyUserAnswers
                .setValue(InlandModePage, inlandMode)
                .setValue(IdentificationPage, arbitrary[Identification].sample.value)
                .setValue(MeansIdentificationNumberPage, arbitrary[String].sample.value)
                .setValue(VehicleCountryPage, arbitrary[Nationality].sample.value)

              forAll(arbitrary[InlandMode].retryUntil(_ != inlandMode)) {
                differentInlandMode =>
                  val result = userAnswers.setValue(InlandModePage, differentInlandMode)

                  result.get(IdentificationPage) must not be defined
                  result.get(MeansIdentificationNumberPage) must not be defined
                  result.get(VehicleCountryPage) must not be defined
              }
          }
        }
      }
    }
  }
}
