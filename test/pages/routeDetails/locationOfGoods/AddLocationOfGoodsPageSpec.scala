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

package pages.routeDetails.locationOfGoods

import models.{LocationOfGoodsIdentification, LocationType}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddLocationOfGoodsPageSpec extends PageBehaviours {

  "AddLocationOfGoodsPage" - {

    beRetrievable[Boolean](AddLocationOfGoodsPage)

    beSettable[Boolean](AddLocationOfGoodsPage)

    beRemovable[Boolean](AddLocationOfGoodsPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up location of goods pages" in {
          forAll(arbitrary[LocationType], arbitrary[LocationOfGoodsIdentification]) {
            (typeOfLocation, qualifierOfIdentification) =>
              val preChange = emptyUserAnswers
                .setValue(LocationTypePage, typeOfLocation)
                .setValue(IdentificationPage, qualifierOfIdentification)

              val postChange = preChange.setValue(AddLocationOfGoodsPage, false)

              postChange.get(LocationTypePage) mustNot be(defined)
              postChange.get(IdentificationPage) mustNot be(defined)
          }
        }
      }
    }
  }
}
