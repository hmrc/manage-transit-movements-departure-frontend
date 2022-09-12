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

package pages.routeDetails.locationOfGoods

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class LocationOfGoodsAddIdentifierYesNoPageSpec extends PageBehaviours {

  "LocationOfGoodsAddIdentifierPage" - {

    beRetrievable[Boolean](LocationOfGoodsAddIdentifierYesNoPage)

    beSettable[Boolean](LocationOfGoodsAddIdentifierYesNoPage)

    beRemovable[Boolean](LocationOfGoodsAddIdentifierYesNoPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up AdditionalIdentifierPage" in {
          forAll(arbitrary[String]) {
            str =>
              val preChange = emptyUserAnswers.setValue(AdditionalIdentifierPage, str)

              val postChange = preChange.setValue(LocationOfGoodsAddIdentifierYesNoPage, false)

              postChange.get(AdditionalIdentifierPage) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            str =>
              val preChange = emptyUserAnswers.setValue(AdditionalIdentifierPage, str)

              val postChange = preChange.setValue(LocationOfGoodsAddIdentifierYesNoPage, true)

              postChange.get(AdditionalIdentifierPage) must be(defined)
          }
        }
      }
    }
  }
}
