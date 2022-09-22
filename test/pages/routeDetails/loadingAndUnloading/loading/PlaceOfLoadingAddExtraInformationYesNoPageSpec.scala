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

class PlaceOfLoadingAddExtraInformationYesNoPageSpec extends PageBehaviours {

  "PlaceOfLoadingAddExtraInformationYesNoPage" - {

    beRetrievable[Boolean](PlaceOfLoadingAddExtraInformationYesNoPage)

    beSettable[Boolean](PlaceOfLoadingAddExtraInformationYesNoPage)

    beRemovable[Boolean](PlaceOfLoadingAddExtraInformationYesNoPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up country and location" in {
          forAll(arbitrary[Country], arbitrary[String]) {
            (country, str) =>
              val preChange = emptyUserAnswers
                .setValue(PlaceOfLoadingCountryPage, country)
                .setValue(PlaceOfLoadingLocationPage, str)

              val postChange = preChange.setValue(PlaceOfLoadingAddExtraInformationYesNoPage, false)

              postChange.get(PlaceOfLoadingCountryPage) mustNot be(defined)
              postChange.get(PlaceOfLoadingLocationPage) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          forAll(arbitrary[Country], arbitrary[String]) {
            (country, str) =>
              val preChange = emptyUserAnswers
                .setValue(PlaceOfLoadingCountryPage, country)
                .setValue(PlaceOfLoadingLocationPage, str)

              val postChange = preChange.setValue(PlaceOfLoadingAddExtraInformationYesNoPage, true)

              postChange.get(PlaceOfLoadingCountryPage) must be(defined)
              postChange.get(PlaceOfLoadingLocationPage) must be(defined)
          }
        }
      }
    }
  }
}
