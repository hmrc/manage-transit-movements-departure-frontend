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

package viewModels.routeDetails.transit

import base.SpecBase
import generators.Generators
import models.reference.{Country, CustomsOffice}
import models.{CountryList, Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.routeDetails.transit.index.{AddOfficeOfTransitETAYesNoPage, OfficeOfTransitCountryPage, OfficeOfTransitPage}
import viewModels.routeDetails.transit.AddAnotherOfficeOfTransitViewModel.AddAnotherOfficeOfTransitViewModelProvider

class AddAnotherOfficeOfTransitViewModelSpec extends SpecBase with Generators {

  "must get list items" in {

    val mode = arbitrary[Mode].sample.value

    val noOfOfficesOfTransit = Gen.choose(1, frontendAppConfig.maxOfficesOfTransit).sample.value
    val country              = arbitrary[Country].sample.value
    val customsOffice        = arbitrary[CustomsOffice].sample.value

    val userAnswers = (0 until noOfOfficesOfTransit).foldLeft(emptyUserAnswers) {
      (acc, i) =>
        acc
          .setValue(OfficeOfTransitCountryPage(Index(i)), country)
          .setValue(OfficeOfTransitPage(Index(i)), customsOffice)
          .setValue(AddOfficeOfTransitETAYesNoPage(Index(i)), false)
    }

    val viewModelProvider = new AddAnotherOfficeOfTransitViewModelProvider()
    val result            = viewModelProvider.apply(userAnswers, mode, CountryList(Nil), CountryList(Nil))
    result.listItems.length mustBe noOfOfficesOfTransit
  }

}
