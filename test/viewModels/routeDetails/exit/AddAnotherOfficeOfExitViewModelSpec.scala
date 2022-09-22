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

package viewModels.routeDetails.exit

import base.SpecBase
import generators.Generators
import models.reference.{Country, CustomsOffice}
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.routeDetails.exit.index.{OfficeOfExitCountryPage, OfficeOfExitPage}
import viewModels.routeDetails.exit.AddAnotherOfficeOfExitViewModel.AddAnotherOfficeOfExitViewModelProvider

class AddAnotherOfficeOfExitViewModelSpec extends SpecBase with Generators {

  "must get list items" in {

    val mode = arbitrary[Mode].sample.value

    val noOfOfficesOfExit = Gen.choose(1, frontendAppConfig.maxOfficesOfExit).sample.value
    val country           = arbitrary[Country].sample.value
    val customsOffice     = arbitrary[CustomsOffice].sample.value

    val userAnswers = (0 until noOfOfficesOfExit).foldLeft(emptyUserAnswers) {
      (acc, i) =>
        acc
          .setValue(OfficeOfExitCountryPage(Index(i)), country)
          .setValue(OfficeOfExitPage(Index(i)), customsOffice)
    }

    val viewModelProvider = new AddAnotherOfficeOfExitViewModelProvider()
    val result            = viewModelProvider.apply(userAnswers, mode)
    result.listItems.length mustBe noOfOfficesOfExit
  }

}
