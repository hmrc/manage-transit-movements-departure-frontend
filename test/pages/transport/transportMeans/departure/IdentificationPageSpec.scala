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
import models.transport.transportMeans.departure.Identification
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IdentificationPageSpec extends PageBehaviours {

  "IdentificationPage" - {

    beRetrievable[Identification](IdentificationPage)

    beSettable[Identification](IdentificationPage)

    beRemovable[Identification](IdentificationPage)

    "cleanup" - {
      "when answer changes" - {
        "must remove identification, identification number and vehicle country" in {
          forAll(arbitrary[Identification]) {
            identification =>
              val userAnswers = emptyUserAnswers
                .setValue(IdentificationPage, identification)
                .setValue(MeansIdentificationNumberPage, arbitrary[String].sample.value)
                .setValue(VehicleCountryPage, arbitrary[Nationality].sample.value)

              forAll(arbitrary[Identification].retryUntil(_ != identification)) {
                differentIdentification =>
                  val result = userAnswers.setValue(IdentificationPage, differentIdentification)

                  result.get(MeansIdentificationNumberPage) must not be defined
                  result.get(VehicleCountryPage) must not be defined
              }
          }
        }
      }
    }
  }
}
