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

import models.reference.UnLocode
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class PlaceOfLoadingAddUnLocodeYesNoPageSpec extends PageBehaviours {

  "PlaceOfLoadingAddUnLocodeYesNoPage" - {

    beRetrievable[Boolean](PlaceOfLoadingAddUnLocodeYesNoPage)

    beSettable[Boolean](PlaceOfLoadingAddUnLocodeYesNoPage)

    beRemovable[Boolean](PlaceOfLoadingAddUnLocodeYesNoPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up UN/LOCODE and add location yes/no" in {
          forAll(arbitrary[UnLocode], arbitrary[Boolean]) {
            (unLocode, bool) =>
              val preChange = emptyUserAnswers
                .setValue(PlaceOfLoadingUnLocodePage, unLocode)
                .setValue(PlaceOfLoadingAddExtraInformationYesNoPage, bool)

              val postChange = preChange.setValue(PlaceOfLoadingAddUnLocodeYesNoPage, false)

              postChange.get(PlaceOfLoadingUnLocodePage) mustNot be(defined)
              postChange.get(PlaceOfLoadingAddExtraInformationYesNoPage) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          forAll(arbitrary[UnLocode], arbitrary[Boolean]) {
            (unLocode, bool) =>
              val preChange = emptyUserAnswers
                .setValue(PlaceOfLoadingUnLocodePage, unLocode)
                .setValue(PlaceOfLoadingAddExtraInformationYesNoPage, bool)

              val postChange = preChange.setValue(PlaceOfLoadingAddUnLocodeYesNoPage, true)

              postChange.get(PlaceOfLoadingUnLocodePage) must be(defined)
              postChange.get(PlaceOfLoadingAddExtraInformationYesNoPage) must be(defined)
          }
        }
      }
    }
  }
}
