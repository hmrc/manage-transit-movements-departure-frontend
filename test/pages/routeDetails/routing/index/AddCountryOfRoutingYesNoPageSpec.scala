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

package pages.routeDetails.routing.index

import models.Index
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.routeDetails.routing.AddCountryOfRoutingYesNoPage

class AddCountryOfRoutingYesNoPageSpec extends PageBehaviours {

  "AddCountryOfRoutingYesNoPage" - {

    beRetrievable[Boolean](AddCountryOfRoutingYesNoPage)

    beSettable[Boolean](AddCountryOfRoutingYesNoPage)

    beRemovable[Boolean](AddCountryOfRoutingYesNoPage)

    "cleanup" - {
      "when NO selected" - {
        "must remove countries of routing" in {
          forAll(arbitrary[Country]) {
            country =>
              val preChange = emptyUserAnswers
                .setValue(CountryOfRoutingPage(Index(0)), country)
                .setValue(CountryOfRoutingPage(Index(1)), country)

              val postChange = preChange.setValue(AddCountryOfRoutingYesNoPage, false)

              postChange.get(CountryOfRoutingPage(Index(0))) mustNot be(defined)
              postChange.get(CountryOfRoutingPage(Index(1))) mustNot be(defined)
          }
        }
      }
    }
  }
}
