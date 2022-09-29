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

package pages.routeDetails.loadingAndUnloading.loading

import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddExtraInformationYesNoPageSpec extends PageBehaviours {

  "AddExtraInformationYesNoPage" - {

    beRetrievable[Boolean](AddExtraInformationYesNoPage)

    beSettable[Boolean](AddExtraInformationYesNoPage)

    beRemovable[Boolean](AddExtraInformationYesNoPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up country and location" in {
          forAll(arbitrary[Country], arbitrary[String]) {
            (country, str) =>
              val preChange = emptyUserAnswers
                .setValue(CountryPage, country)
                .setValue(LocationPage, str)

              val postChange = preChange.setValue(AddExtraInformationYesNoPage, false)

              postChange.get(CountryPage) mustNot be(defined)
              postChange.get(LocationPage) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          forAll(arbitrary[Country], arbitrary[String]) {
            (country, str) =>
              val preChange = emptyUserAnswers
                .setValue(CountryPage, country)
                .setValue(LocationPage, str)

              val postChange = preChange.setValue(AddExtraInformationYesNoPage, true)

              postChange.get(CountryPage) must be(defined)
              postChange.get(LocationPage) must be(defined)
          }
        }
      }
    }
  }
}
