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

package pages.routeDetails.transit.index

import models.reference.{Country, CountryCode}
import pages.behaviours.PageBehaviours

class OfficeOfTransitCountryPageSpec extends PageBehaviours {

  "OfficeOfTransitCountryPage" - {

    beRetrievable[Country](OfficeOfTransitCountryPage(index))

    beSettable[Country](OfficeOfTransitCountryPage(index))

    beRemovable[Country](OfficeOfTransitCountryPage(index))
  }

  "cleanup" - {
    val transitCountry       = Country(CountryCode("IT"), "Italy")
    val updatedCountry       = Country(CountryCode("GB"), "United Kingdom")
    val transitCustomsOffice = arbitraryCustomsOffice.arbitrary.sample.get

    "when value changes" - {
      "must clean up Office Of Transit page" in {
        val preChange = emptyUserAnswers
          .setValue(OfficeOfTransitCountryPage(index), transitCountry)
          .setValue(OfficeOfTransitPage(index), transitCustomsOffice)

        val postChange = preChange.setValue(OfficeOfTransitCountryPage(index), updatedCountry)

        postChange.get(OfficeOfTransitPage(index)) mustNot be(defined)
      }
    }

    "when value has not changed" - {
      "must not clean up Office Of Transit page" in {
        val preChange = emptyUserAnswers
          .setValue(OfficeOfTransitCountryPage(index), transitCountry)
          .setValue(OfficeOfTransitPage(index), transitCustomsOffice)

        val postChange = preChange.setValue(OfficeOfTransitCountryPage(index), transitCountry)

        postChange.get(OfficeOfTransitPage(index)) mustBe defined
      }
    }
  }
}
