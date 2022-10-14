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
import generators.{Generators, TraderDetailsUserAnswersGenerator}
import models.reference.{Country, CountryCode}
import models.{Address, DynamicAddress, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.traderDetails.consignment._
import pages.traderDetails.consignment.consignor.CountryPage
import viewModels.traderDetails.ConsignmentViewModel.ConsignmentViewModelProvider

class ConsignmentViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with TraderDetailsUserAnswersGenerator {

  "apply" - {
    "when user answers empty" - {
      "must return empty rows" in {
        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[ConsignmentViewModelProvider]
        val sections          = viewModelProvider.apply(emptyUserAnswers, mode).sections

        sections.size mustBe 3

        sections.head.sectionTitle.get mustBe "Consignor"
        sections.head.rows must be(empty)

        sections(1).sectionTitle.get mustBe "Consignor contact"
        sections(1).rows must be(empty)

        sections(2).sectionTitle.get mustBe "Consignee"
        sections(2).rows must be(empty)
      }
    }

    "when user answers populated" - {
      "must return row for each answer" in {
        val answers = emptyUserAnswers
          .setValue(ApprovedOperatorPage, false)
          .setValue(consignor.EoriYesNoPage, true)
          .setValue(consignor.EoriPage, "eori")
          .setValue(consignor.NamePage, "name")
          .setValue(CountryPage, Country(CountryCode("GB"), "Great Britain"))
          .setValue(consignor.AddressPage, DynamicAddress("line1", "line2", Some("postal code")))
          .setValue(consignor.AddContactPage, true)
          .setValue(consignor.contact.NamePage, "contact name")
          .setValue(consignor.contact.TelephoneNumberPage, "phone number")
          .setValue(MoreThanOneConsigneePage, false)
          .setValue(consignee.EoriYesNoPage, true)
          .setValue(consignee.EoriNumberPage, "eori2")
          .setValue(consignee.NamePage, "name2")
          .setValue(consignee.AddressPage, Address("line12", "line22", "postal code2", Country(CountryCode("code"), "description")))

        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[ConsignmentViewModelProvider]
        val sections          = viewModelProvider.apply(answers, mode).sections

        sections.size mustBe 3

        sections.head.sectionTitle.get mustBe "Consignor"
        sections.head.rows.size mustBe 6
        sections.head.rows.head.value.content.asHtml.toString() mustBe "No"
        sections.head.rows(1).value.content.asHtml.toString() mustBe "Yes"
        sections.head.rows(2).value.content.asHtml.toString() mustBe "eori"
        sections.head.rows(3).value.content.asHtml.toString() mustBe "name"
        sections.head.rows(4).value.content.asHtml.toString() mustBe "Great Britain"
        sections.head.rows(5).value.content.asHtml.toString() mustBe "line1<br>line2<br>postal code"

        sections(1).sectionTitle.get mustBe "Consignor contact"
        sections(1).rows.size mustBe 3
        sections(1).rows.head.value.content.asHtml.toString() mustBe "Yes"
        sections(1).rows(1).value.content.asHtml.toString() mustBe "contact name"
        sections(1).rows(2).value.content.asHtml.toString() mustBe "phone number"

        sections(2).sectionTitle.get mustBe "Consignee"
        sections(2).rows.size mustBe 5
        sections(2).rows.head.value.content.asHtml.toString() mustBe "No"
        sections(2).rows(1).value.content.asHtml.toString() mustBe "Yes"
        sections(2).rows(2).value.content.asHtml.toString() mustBe "eori2"
        sections(2).rows(3).value.content.asHtml.toString() mustBe "name2"
        sections(2).rows(4).value.content.asHtml.toString() mustBe "line12<br>line22<br>postal code2<br>description"
      }
    }
  }
}
