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

package viewModels.traderDetails

import base.SpecBase
import generators.Generators
import models.Mode
import models.traderDetails.representative.RepresentativeCapacity.Direct
import org.scalacheck.Arbitrary.arbitrary
import pages.traderDetails.ActingAsRepresentativePage
import pages.traderDetails.representative._
import viewModels.traderDetails.RepresentativeViewModel.RepresentativeViewModelProvider

class RepresentativeViewModelSpec extends SpecBase with Generators {

  "apply" - {
    "when user answers empty" - {
      "must return empty rows" in {
        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[RepresentativeViewModelProvider]
        val sections          = viewModelProvider.apply(emptyUserAnswers, mode).sections

        sections.size mustBe 1

        sections.head.sectionTitle.get mustBe "Representative"
        sections.head.rows must be(empty)
      }
    }

    "when user answers populated" - {
      "must return row for each answer" in {
        val answers = emptyUserAnswers
          .setValue(ActingAsRepresentativePage, true)
          .setValue(EoriPage, "eori")
          .setValue(NamePage, "name")
          .setValue(CapacityPage, Direct)
          .setValue(TelephoneNumberPage, "phone")

        val mode              = arbitrary[Mode].sample.value
        val viewModelProvider = injector.instanceOf[RepresentativeViewModelProvider]
        val sections          = viewModelProvider.apply(answers, mode).sections

        sections.size mustBe 1

        sections.head.sectionTitle.get mustBe "Representative"
        sections.head.rows.size mustBe 5
        sections.head.rows.head.value.content.asHtml.toString() mustBe "Yes"
        sections.head.rows(1).value.content.asHtml.toString() mustBe "eori"
        sections.head.rows(2).value.content.asHtml.toString() mustBe "name"
        sections.head.rows(3).value.content.asHtml.toString() mustBe "Direct (principal solely liable)"
        sections.head.rows(4).value.content.asHtml.toString() mustBe "phone"
      }
    }
  }
}
