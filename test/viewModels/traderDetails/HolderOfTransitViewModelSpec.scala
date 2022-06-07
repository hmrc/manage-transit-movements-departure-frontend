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

package viewModels.traderDetails

import base.SpecBase
import generators.Generators
import models.Address
import models.reference.{Country, CountryCode}
import pages.traderDetails.holderOfTransit._
import viewModels.traderDetails.HolderOfTransitViewModel.HolderOfTransitSectionViewModel

class HolderOfTransitViewModelSpec extends SpecBase with Generators {

  "apply" - {
    "when user answers empty" - {
      "must return empty rows" in {
        val sections = new HolderOfTransitSectionViewModel().apply(emptyUserAnswers)

        sections.size mustBe 2

        sections.head.sectionTitle.get mustBe "Transit holder"
        sections.head.rows must be(empty)

        sections(1).sectionTitle.get mustBe "Additional contact"
        sections(1).rows must be(empty)
      }
    }

    "when user answers populated" - {
      "must return row for each answer" in {
        val answers = emptyUserAnswers
          .setValue(EoriYesNoPage, true)
          .setValue(EoriPage, "eori")
          .setValue(TirIdentificationYesNoPage, true)
          .setValue(TirIdentificationPage, "tir id")
          .setValue(NamePage, "name")
          .setValue(AddressPage, Address("line1", "line2", "postal code", Country(CountryCode("code"), "description")))
          .setValue(AddContactPage, true)
          .setValue(contact.NamePage, "contact name")
          .setValue(contact.TelephoneNumberPage, "phone number")

        val sections = new HolderOfTransitSectionViewModel().apply(answers)

        sections.size mustBe 2

        sections.head.sectionTitle.get mustBe "Transit holder"
        sections.head.rows.size mustBe 6
        sections.head.rows.head.value.content.asHtml.toString() mustBe "Yes"
        sections.head.rows(1).value.content.asHtml.toString() mustBe "eori"
        sections.head.rows(2).value.content.asHtml.toString() mustBe "Yes"
        sections.head.rows(3).value.content.asHtml.toString() mustBe "tir id"
        sections.head.rows(4).value.content.asHtml.toString() mustBe "name"
        sections.head.rows(5).value.content.asHtml.toString() mustBe "line1<br>line2<br>postal code<br>description"

        sections(1).sectionTitle.get mustBe "Additional contact"
        sections(1).rows.size mustBe 3
        sections(1).rows.head.value.content.asHtml.toString() mustBe "Yes"
        sections(1).rows(1).value.content.asHtml.toString() mustBe "contact name"
        sections(1).rows(2).value.content.asHtml.toString() mustBe "phone number"
      }
    }
  }
}
