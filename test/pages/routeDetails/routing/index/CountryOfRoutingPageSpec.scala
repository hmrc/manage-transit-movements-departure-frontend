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

package pages.routeDetails.routing.index

import models.reference.{Country, CountryCode}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.routeDetails.exit.index.OfficeOfExitCountryPage
import pages.routeDetails.transit.index.OfficeOfTransitCountryPage

class CountryOfRoutingPageSpec extends PageBehaviours {

  "CountryOfRoutingPage" - {

    beRetrievable[Country](CountryOfRoutingPage(index))

    beSettable[Country](CountryOfRoutingPage(index))

    beRemovable[Country](CountryOfRoutingPage(index))

    "cleanup" - {
      "when value changes" - {
        "must clean up transit and exit sections" in {
          val france  = Country(CountryCode("FR"), "France")
          val italy   = Country(CountryCode("IT"), "Italy")
          val country = arbitrary[Country].sample.value

          val preChange = emptyUserAnswers
            .setValue(CountryOfRoutingPage(index), france)
            .setValue(OfficeOfTransitCountryPage(index), country)
            .setValue(OfficeOfExitCountryPage(index), country)

          val postChange = preChange.setValue(CountryOfRoutingPage(index), italy)

          postChange.get(OfficeOfTransitCountryPage(index)) mustNot be(defined)
          postChange.get(OfficeOfExitCountryPage(index)) mustNot be(defined)
        }
      }
    }
  }
}
