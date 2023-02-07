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

package pages.traderDetails.representative

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddDetailsPageSpec extends PageBehaviours {

  "AddDetailsPage" - {

    beRetrievable[Boolean](AddDetailsPage)

    beSettable[Boolean](AddDetailsPage)

    beRemovable[Boolean](AddDetailsPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up name and telephone pages" in {
          forAll(arbitrary[String], arbitrary[String]) {
            (name, telephone) =>
              val preChange = emptyUserAnswers
                .setValue(AddDetailsPage, true)
                .setValue(NamePage, name)
                .setValue(TelephoneNumberPage, telephone)
              val postChange = preChange.setValue(AddDetailsPage, false)

              postChange.get(NamePage) mustNot be(defined)
              postChange.get(TelephoneNumberPage) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          forAll(arbitrary[String], arbitrary[String]) {
            (name, telephone) =>
              val preChange = emptyUserAnswers
                .setValue(NamePage, name)
                .setValue(TelephoneNumberPage, telephone)
              val postChange = preChange.setValue(AddDetailsPage, true)

              postChange.get(NamePage) must be(defined)
          }
        }
      }
    }
  }
}
