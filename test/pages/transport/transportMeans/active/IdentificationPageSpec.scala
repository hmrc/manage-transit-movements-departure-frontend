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

package pages.transport.transportMeans.active

import models.transport.transportMeans.active.Identification
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours

class IdentificationPageSpec extends PageBehaviours {

  "IdentificationPage" - {

    beRetrievable[Identification](IdentificationPage(activeIndex))

    beSettable[Identification](IdentificationPage(activeIndex))

    beRemovable[Identification](IdentificationPage(activeIndex))

    "cleanup" - {
      "when answer changes" - {
        "must remove identification number" in {
          forAll(arbitrary[Identification], Gen.alphaNumStr) {
            (identification, identificationNumber) =>
              val userAnswers = emptyUserAnswers
                .setValue(IdentificationPage(index), identification)
                .setValue(IdentificationNumberPage(index), identificationNumber)

              forAll(arbitrary[Identification].retryUntil(_ != identification)) {
                differentIdentification =>
                  val result = userAnswers.setValue(IdentificationPage(index), differentIdentification)

                  result.get(IdentificationNumberPage(index)) must not be defined
              }
          }
        }
      }
    }
  }
}
