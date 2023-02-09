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

package viewModels.routeDetails.routing

import base.SpecBase
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.routeDetails.routing.AddAnotherCountryOfRoutingViewModel.AddAnotherCountryOfRoutingViewModelProvider

class AddAnotherCountryOfRoutingViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one country of routing" in {
      forAll(arbitrary[Mode]) {
        mode =>
          val userAnswers = arbitraryCountryOfRoutingAnswers(emptyUserAnswers, index).sample.value

          val result = new AddAnotherCountryOfRoutingViewModelProvider()(userAnswers, mode)

          result.listItems.length mustBe 1
          result.title mustBe "You have added 1 country to the transit route"
          result.heading mustBe "You have added 1 country to the transit route"
          result.legend mustBe "Do you want to add another country to the transit route?"
          result.maxLimitLabel mustBe "You cannot add any more countries to the transit route. To add another country, you need to remove one first."
      }
    }

    "when there are multiple countries of routing" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxCountriesOfRouting)) {
        (mode, count) =>
          val userAnswers = (0 until count).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitraryCountryOfRoutingAnswers(acc, Index(i)).sample.value
          }

          val result = new AddAnotherCountryOfRoutingViewModelProvider()(userAnswers, mode)

          result.listItems.length mustBe count
          result.title mustBe s"You have added ${formatter.format(count)} countries to the transit route"
          result.heading mustBe s"You have added ${formatter.format(count)} countries to the transit route"
          result.legend mustBe "Do you want to add another country to the transit route?"
          result.maxLimitLabel mustBe "You cannot add any more countries to the transit route. To add another country, you need to remove one first."
      }
    }
  }

}
