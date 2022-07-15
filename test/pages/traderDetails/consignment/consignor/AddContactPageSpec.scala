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

package pages.traderDetails.consignment.consignor

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddContactPageSpec extends PageBehaviours {

  "traderDetails.consignment.consignor.AddContactPage" - {

    beRetrievable[Boolean](AddContactPage)

    beSettable[Boolean](AddContactPage)

    beRemovable[Boolean](AddContactPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up contact pages" in {
          forAll(arbitrary[String], arbitrary[String]) {
            (name, phoneNumber) =>
              val preChange = emptyUserAnswers
                .setValue(contact.NamePage, name)
                .setValue(contact.TelephoneNumberPage, phoneNumber)
              val postChange = preChange.setValue(AddContactPage, false)

              postChange.get(contact.NamePage) mustNot be(defined)
              postChange.get(contact.TelephoneNumberPage) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          forAll(arbitrary[String], arbitrary[String]) {
            (name, phoneNumber) =>
              val preChange = emptyUserAnswers
                .setValue(contact.NamePage, name)
                .setValue(contact.TelephoneNumberPage, phoneNumber)
              val postChange = preChange.setValue(AddContactPage, true)

              postChange.get(contact.NamePage) must be(defined)
              postChange.get(contact.TelephoneNumberPage) must be(defined)
          }
        }
      }
    }
  }
}
