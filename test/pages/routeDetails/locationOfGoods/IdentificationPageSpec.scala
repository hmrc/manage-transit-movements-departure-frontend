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

import models.LocationOfGoodsIdentification
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.sections.routeDetails.locationOfGoods.LocationOfGoodsIdentifierSection
import play.api.libs.json.Json

class IdentificationPageSpec extends PageBehaviours {

  "LocationOfGoodsIdentificationPage" - {

    beRetrievable[LocationOfGoodsIdentification](IdentificationPage)

    beSettable[LocationOfGoodsIdentification](IdentificationPage)

    beRemovable[LocationOfGoodsIdentification](IdentificationPage)

    "cleanup" - {
      "when answer has changed" - {
        "must clean up LocationOfGoodsIdentifierSection" in {
          forAll(arbitrary[LocationOfGoodsIdentification]) {
            qualifierOfIdentification =>
              forAll(arbitrary[LocationOfGoodsIdentification].retryUntil(_ != qualifierOfIdentification)) {
                differentQualifierOfIdentification =>
                  val preChange = emptyUserAnswers
                    .setValue(IdentificationPage, qualifierOfIdentification)
                    .setValue(LocationOfGoodsIdentifierSection, Json.obj("foo" -> "bar"))

                  val postChange = preChange.setValue(IdentificationPage, differentQualifierOfIdentification)

                  postChange.get(LocationOfGoodsIdentifierSection) mustNot be(defined)
              }
          }
        }
      }

      "when answer has not changed" - {
        "must do nothing" in {
          forAll(arbitrary[LocationOfGoodsIdentification]) {
            qualifierOfIdentification =>
              val preChange = emptyUserAnswers
                .setValue(IdentificationPage, qualifierOfIdentification)
                .setValue(LocationOfGoodsIdentifierSection, Json.obj("foo" -> "bar"))

              val postChange = preChange.setValue(IdentificationPage, qualifierOfIdentification)

              postChange.get(LocationOfGoodsIdentifierSection) must be(defined)
          }
        }
      }
    }
  }
}
