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

import models.DynamicAddress
import models.SecurityDetailsType.{EntrySummaryDeclarationSecurityDetails, NoSecurityDetails}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.preTaskList.SecurityDetailsTypePage
import pages.traderDetails.consignment.consignor._

class ApprovedOperatorPageSpec extends PageBehaviours {

  "ApprovedOperatorPage" - {

    beRetrievable[Boolean](ApprovedOperatorPage)

    beSettable[Boolean](ApprovedOperatorPage)

    beRemovable[Boolean](ApprovedOperatorPage)

    "cleanup" - {
      val testAddress = arbitrary[DynamicAddress].sample.value

      "when Yes selected and we have No Security Details" - {
        "must clean up Consignor pages" in {

          forAll(arbitrary[String]) {
            eori =>
              val preChange = emptyUserAnswers
                .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                .setValue(ApprovedOperatorPage, false)
                .setValue(EoriYesNoPage, true)
                .setValue(EoriPage, eori)
                .setValue(NamePage, "name")
                .setValue(AddressPage, testAddress)
              val postChange = preChange.setValue(ApprovedOperatorPage, true)

              postChange.get(EoriPage) mustNot be(defined)
              postChange.get(EoriYesNoPage) mustNot be(defined)
              postChange.get(NamePage) mustNot be(defined)
              postChange.get(AddressPage) mustNot be(defined)
          }
        }
      }

      "when No selected and we have No Security Details" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            eori =>
              val preChange = emptyUserAnswers
                .setValue(SecurityDetailsTypePage, NoSecurityDetails)
                .setValue(ApprovedOperatorPage, true)
                .setValue(EoriYesNoPage, true)
                .setValue(EoriPage, eori)
                .setValue(NamePage, "name")
                .setValue(AddressPage, testAddress)

              val postChange = preChange.setValue(ApprovedOperatorPage, false)

              postChange.get(EoriPage).isDefined must be(true)
              postChange.get(EoriYesNoPage).isDefined must be(true)
              postChange.get(NamePage).isDefined must be(true)
              postChange.get(AddressPage).isDefined must be(true)
          }
        }
      }

      "when Yes selected and we have Security Details" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            eori =>
              val preChange = emptyUserAnswers
                .setValue(SecurityDetailsTypePage, EntrySummaryDeclarationSecurityDetails)
                .setValue(ApprovedOperatorPage, false)
                .setValue(EoriYesNoPage, true)
                .setValue(EoriPage, eori)
                .setValue(NamePage, "name")
                .setValue(AddressPage, testAddress)

              val postChange = preChange.setValue(ApprovedOperatorPage, true)

              postChange.get(EoriPage).isDefined must be(true)
              postChange.get(EoriYesNoPage).isDefined must be(true)
              postChange.get(NamePage).isDefined must be(true)
              postChange.get(AddressPage).isDefined must be(true)
          }
        }
      }

      "when Yes selected and haven't populated the security details type" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            eori =>
              val preChange = emptyUserAnswers
                .setValue(ApprovedOperatorPage, false)
                .setValue(EoriYesNoPage, true)
                .setValue(EoriPage, eori)
                .setValue(NamePage, "name")
                .setValue(AddressPage, testAddress)

              val postChange = preChange.setValue(ApprovedOperatorPage, true)

              postChange.get(EoriPage).isDefined must be(true)
              postChange.get(EoriYesNoPage).isDefined must be(true)
              postChange.get(NamePage).isDefined must be(true)
              postChange.get(AddressPage).isDefined must be(true)
          }
        }
      }
    }
  }
}
