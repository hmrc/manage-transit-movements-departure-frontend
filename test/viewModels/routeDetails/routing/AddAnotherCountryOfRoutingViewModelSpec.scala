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
import models.reference.Country
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.routeDetails.routing.index.CountryOfRoutingPage
import viewModels.routeDetails.routing.AddAnotherCountryOfRoutingViewModel.AddAnotherCountryOfRoutingViewModelProvider

class AddAnotherCountryOfRoutingViewModelSpec extends SpecBase with Generators {

  "must get list items" in {

    val numberOfCountries = Gen.choose(1, frontendAppConfig.maxCountriesOfRouting).sample.value
    def country           = arbitrary[Country].sample.value
    val mode              = arbitrary[Mode].sample.value

    val userAnswers = (0 until numberOfCountries).foldLeft(emptyUserAnswers) {
      (acc, i) =>
        acc.setValue(CountryOfRoutingPage(Index(i)), country)
    }

    val viewModelProvider = injector.instanceOf[AddAnotherCountryOfRoutingViewModelProvider]
    val result            = viewModelProvider.apply(userAnswers, mode)
    result.listItems.length mustBe numberOfCountries
  }

}
