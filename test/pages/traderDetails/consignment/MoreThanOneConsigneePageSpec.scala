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

package pages.traderDetails.consignment

import models.{DynamicAddress, EoriNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours
import pages.traderDetails.consignment.consignee._

class MoreThanOneConsigneePageSpec extends PageBehaviours {

  "MoreThanOneConsigneePage" - {

    beRetrievable[Boolean](MoreThanOneConsigneePage)

    beSettable[Boolean](MoreThanOneConsigneePage)

    beRemovable[Boolean](MoreThanOneConsigneePage)

    "cleanup" - {
      val consigneeEori    = arbitrary[EoriNumber].sample.value
      val consigneeName    = Gen.alphaNumStr.sample.value
      val consigneeAddress = arbitrary[DynamicAddress].sample.value

      "when Yes selected" - {
        "must clean up Consignee pages" in {
          val preChange = emptyUserAnswers
            .setValue(EoriYesNoPage, true)
            .setValue(EoriNumberPage, consigneeEori.value)
            .setValue(NamePage, consigneeName)
            .setValue(AddressPage, consigneeAddress)
          val postChange = preChange.setValue(MoreThanOneConsigneePage, true)

          postChange.get(EoriNumberPage) mustNot be(defined)
          postChange.get(EoriYesNoPage) mustNot be(defined)
          postChange.get(NamePage) mustNot be(defined)
          postChange.get(AddressPage) mustNot be(defined)
        }
      }

      "when NO selected" - {
        "must do nothing" in {
          val preChange = emptyUserAnswers
            .setValue(EoriYesNoPage, true)
            .setValue(EoriNumberPage, consigneeEori.value)
            .setValue(NamePage, consigneeName)
            .setValue(AddressPage, consigneeAddress)
          val postChange = preChange.setValue(MoreThanOneConsigneePage, false)

          postChange.get(EoriNumberPage) must be(defined)
          postChange.get(EoriYesNoPage) must be(defined)
          postChange.get(NamePage) must be(defined)
          postChange.get(AddressPage) must be(defined)
        }
      }
    }
  }
}
