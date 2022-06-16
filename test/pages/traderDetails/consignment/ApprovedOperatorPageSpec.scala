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

package pages.traderDetails.consignment

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.traderDetails.consignment.consignor.{EoriPage, EoriYesNoPage}

class ApprovedOperatorPageSpec extends PageBehaviours {

  "ApprovedOperatorPage" - {

    beRetrievable[Boolean](ApprovedOperatorPage)

    beSettable[Boolean](ApprovedOperatorPage)

    beRemovable[Boolean](ApprovedOperatorPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up Consignor pages" in {
          forAll(arbitrary[String]) {
            eori =>
              val preChange = emptyUserAnswers
                .setValue(ApprovedOperatorPage, true)
                .setValue(EoriYesNoPage, true)
                .setValue(EoriPage, eori)
              val postChange = preChange.set(ApprovedOperatorPage, false).success.value

              postChange.get(EoriPage) mustNot be(defined)
              postChange.get(EoriYesNoPage) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            eori =>
              val preChange  = emptyUserAnswers.setValue(EoriPage, eori)
              val postChange = preChange.set(EoriYesNoPage, true).success.value

              postChange.get(EoriPage) must be(defined)
          }
        }
      }
    }
  }
}
