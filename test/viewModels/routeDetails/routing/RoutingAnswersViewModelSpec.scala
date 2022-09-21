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

package viewModels.routeDetails.routing

import base.SpecBase
import generators.Generators
import models.NormalMode
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import pages.routeDetails.routing._
import pages.routeDetails.routing.index.CountryOfRoutingPage
import viewModels.Link

class RoutingAnswersViewModelSpec extends SpecBase with Generators {

  "must return sections" in {
    val userAnswers = emptyUserAnswers
      .setValue(OfficeOfDestinationPage, arbitrary[CustomsOffice].sample.value)
      .setValue(BindingItineraryPage, arbitrary[Boolean].sample.value)
      .setValue(AddCountryOfRoutingYesNoPage, arbitrary[Boolean].sample.value)
      .setValue(CountryOfRoutingPage(index), arbitrary[Country].sample.value)

    val sections = RoutingAnswersViewModel.apply(userAnswers, NormalMode).sections

    sections.size mustBe 2

    sections.head.sectionTitle mustNot be(defined)
    sections.head.rows.size mustBe 3
    sections.head.addAnotherLink mustNot be(defined)

    sections(1).sectionTitle.get mustBe "Transit route countries"
    sections(1).rows.size mustBe 1
    sections(1).addAnotherLink.get mustBe Link(
      "add-or-remove-transit-route-countries",
      "Add or remove transit route countries",
      controllers.routeDetails.routing.routes.AddAnotherCountryOfRoutingController.onPageLoad(userAnswers.lrn).url
    )
  }
}
