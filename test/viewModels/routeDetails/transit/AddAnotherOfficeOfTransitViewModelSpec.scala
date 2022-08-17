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

package viewModels.routeDetails.transit

import base.SpecBase
import generators.Generators
import models.reference.{Country, CustomsOffice}
import models.{CountryList, Index}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.routeDetails.transit.index.{AddOfficeOfTransitETAYesNoPage, OfficeOfTransitCountryPage, OfficeOfTransitPage}
import services.CountriesService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AddAnotherOfficeOfTransitViewModelSpec extends SpecBase with Generators {

  "must get list items" in {

    val countriesService = mock[CountriesService]
    when(countriesService.getTransitCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))
    when(countriesService.getCommunityCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))
    when(countriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))

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

    val result = AddAnotherOfficeOfTransitViewModel(countriesService)(userAnswers).futureValue
    result.listItems.length mustBe noOfOfficesOfTransit
  }

}
