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

import models.SecurityDetailsType
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours
import pages.preTaskList.SecurityDetailsTypePage

class BindingItineraryPageSpec extends PageBehaviours {

  "BindingItineraryPage" - {

    beRetrievable[Boolean](BindingItineraryPage)

    beSettable[Boolean](BindingItineraryPage)

    beRemovable[Boolean](BindingItineraryPage)

    "cleanup" - {
      "when Yes selected" - {
        "must remove AddCountryOfRoutingYesNo" in {

          val preChange = emptyUserAnswers
            .setValue(AddCountryOfRoutingYesNoPage, true)

          val postChange = preChange.setValue(BindingItineraryPage, true)

          postChange.get(AddCountryOfRoutingYesNoPage) mustNot be(defined)
        }
      }

      "when No selected" - {

        "and securityDetailsTypePage has security" - {

          "then must remove AddCountryOfRoutingYesNo" in {

            val genSecurity = Gen.oneOf(SecurityDetailsType.securityValues).sample.value

            val preChange = emptyUserAnswers
              .setValue(AddCountryOfRoutingYesNoPage, true)
              .setValue(SecurityDetailsTypePage, genSecurity)

            val postChange = preChange.setValue(BindingItineraryPage, false)

            postChange.get(AddCountryOfRoutingYesNoPage) mustNot be(defined)
          }
        }

        "and securityDetailsTypePage has no security" - {

          "then must not remove AddCountryOfRoutingYesNo" in {

            val preChange = emptyUserAnswers
              .setValue(AddCountryOfRoutingYesNoPage, true)
              .setValue(SecurityDetailsTypePage, SecurityDetailsType.NoSecurityDetails)

            val postChange = preChange.setValue(BindingItineraryPage, false)

            postChange.get(AddCountryOfRoutingYesNoPage) must be(defined)
          }
        }
      }
    }
  }
}
