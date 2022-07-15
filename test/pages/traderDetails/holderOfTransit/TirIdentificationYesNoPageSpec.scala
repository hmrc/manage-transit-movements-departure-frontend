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

package pages.traderDetails.holderOfTransit

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class TirIdentificationYesNoPageSpec extends PageBehaviours {

  "TirIdentificationYesNoPage" - {

    beRetrievable[Boolean](TirIdentificationYesNoPage)

    beSettable[Boolean](TirIdentificationYesNoPage)

    beRemovable[Boolean](TirIdentificationYesNoPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up TirIdentification" in {
          forAll(arbitrary[String]) {
            tirId =>
              val preChange  = emptyUserAnswers.setValue(TirIdentificationPage, tirId)
              val postChange = preChange.setValue(TirIdentificationYesNoPage, false)

              postChange.get(TirIdentificationPage) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            tirId =>
              val preChange  = emptyUserAnswers.setValue(TirIdentificationPage, tirId)
              val postChange = preChange.setValue(TirIdentificationYesNoPage, true)

              postChange.get(TirIdentificationPage) must be(defined)
          }
        }
      }
    }
  }
}
