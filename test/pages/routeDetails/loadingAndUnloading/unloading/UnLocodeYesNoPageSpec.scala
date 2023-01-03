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

package pages.routeDetails.loadingAndUnloading.unloading

import models.reference.UnLocode
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class UnLocodeYesNoPageSpec extends PageBehaviours {

  "AddUnLocodeYesNoPage" - {

    beRetrievable[Boolean](UnLocodeYesNoPage)

    beSettable[Boolean](UnLocodeYesNoPage)

    beRemovable[Boolean](UnLocodeYesNoPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up UN/LOCODE and add location yes/no" in {
          forAll(arbitrary[UnLocode], arbitrary[Boolean]) {
            (unLocode, bool) =>
              val preChange = emptyUserAnswers
                .setValue(UnLocodePage, unLocode)
                .setValue(AddExtraInformationYesNoPage, bool)

              val postChange = preChange.setValue(UnLocodeYesNoPage, false)

              postChange.get(UnLocodePage) mustNot be(defined)
              postChange.get(AddExtraInformationYesNoPage) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          forAll(arbitrary[UnLocode], arbitrary[Boolean]) {
            (unLocode, bool) =>
              val preChange = emptyUserAnswers
                .setValue(UnLocodePage, unLocode)
                .setValue(AddExtraInformationYesNoPage, bool)

              val postChange = preChange.setValue(UnLocodeYesNoPage, true)

              postChange.get(UnLocodePage) must be(defined)
              postChange.get(AddExtraInformationYesNoPage) must be(defined)
          }
        }
      }
    }
  }
}
