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

package pages.traderDetails

import models.traderDetails.representative.RepresentativeCapacity
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.traderDetails.representative.{CapacityPage, EoriPage, NamePage, TelephoneNumberPage}

class ActingAsRepresentativePageSpec extends PageBehaviours {

  "ActingRepresentativePage" - {

    beRetrievable[Boolean](ActingAsRepresentativePage)

    beSettable[Boolean](ActingAsRepresentativePage)

    beRemovable[Boolean](ActingAsRepresentativePage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up Representative pages" in {
          forAll(arbitrary[String], arbitrary[String], arbitrary[String], arbitrary[RepresentativeCapacity]) {
            (eori, name, telephone, capacity) =>
              val preChange = emptyUserAnswers
                .setValue(ActingAsRepresentativePage, true)
                .setValue(EoriPage, eori)
                .setValue(NamePage, name)
                .setValue(CapacityPage, capacity)
                .setValue(TelephoneNumberPage, telephone)
              val postChange = preChange.setValue(ActingAsRepresentativePage, false)

              postChange.get(EoriPage) mustNot be(defined)
              postChange.get(NamePage) mustNot be(defined)
              postChange.get(CapacityPage) mustNot be(defined)
              postChange.get(TelephoneNumberPage) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            eori =>
              val preChange  = emptyUserAnswers.setValue(EoriPage, eori)
              val postChange = preChange.setValue(ActingAsRepresentativePage, true)

              postChange.get(EoriPage) must be(defined)
          }
        }
      }
    }
  }
}
