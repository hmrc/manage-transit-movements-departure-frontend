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

package pages.routeDetails.routing

import models.reference.{Country, CountryCode}
import pages.behaviours.PageBehaviours

class CountryOfDestinationPageSpec extends PageBehaviours {

  "CountryOfDestinationPage" - {

    beRetrievable[Country](CountryOfDestinationPage)

    beSettable[Country](CountryOfDestinationPage)

    beRemovable[Country](CountryOfDestinationPage)
  }

  "cleanup" - {
    val destinationCountry        = Country(CountryCode("IT"), "Italy")
    val updatedDestinationCountry = Country(CountryCode("GB"), "United Kingdom")
    val customsOffice             = arbitraryCustomsOffice.arbitrary.sample.get

    "when value changes" - {
      "must clean up Country of Destination page" in {
        val preChange = emptyUserAnswers
          .setValue(CountryOfDestinationPage, destinationCountry)
          .setValue(OfficeOfDestinationPage, customsOffice)

        val postChange = preChange.setValue(CountryOfDestinationPage, updatedDestinationCountry)

        postChange.get(OfficeOfDestinationPage) mustNot be(defined)
      }
    }

    "when value has not changed" - {
      "must not clean up Office of Destination page" in {
        val preChange = emptyUserAnswers
          .setValue(CountryOfDestinationPage, destinationCountry)
          .setValue(OfficeOfDestinationPage, customsOffice)

        val postChange = preChange.setValue(CountryOfDestinationPage, destinationCountry)

        postChange.get(OfficeOfDestinationPage) mustBe defined
      }
    }
  }
}
