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

package pages.routeDetails.transit

import generators.Generators
import pages.behaviours.PageBehaviours

class AddOfficeOfTransitYesNoPageSpec extends PageBehaviours with Generators {

  "AddOfficeOfTransitYesNoPage" - {

    beRetrievable[Boolean](AddOfficeOfTransitYesNoPage)

    beSettable[Boolean](AddOfficeOfTransitYesNoPage)

    beRemovable[Boolean](AddOfficeOfTransitYesNoPage)

    "cleanup" - {
      val transitCountry       = arbitraryCountry.arbitrary.sample.get
      val transitCustomsOffice = arbitraryCustomsOffice.arbitrary.sample.get
      val eta                  = arbitraryDateTime.arbitrary.sample.get

      "when No selected" - {
        "must clean up Office Of Transit pages" in {
          val preChange = emptyUserAnswers
            .setValue(OfficeOfTransitCountryPage(index), transitCountry)
            .setValue(OfficeOfTransitPage(index), transitCustomsOffice)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), true)
            .setValue(OfficeOfTransitETAPage(index), eta)

          val postChange = preChange.setValue(AddOfficeOfTransitYesNoPage, false)

          postChange.get(OfficeOfTransitCountryPage(index)) mustNot be(defined)
          postChange.get(OfficeOfTransitPage(index)) mustNot be(defined)
          postChange.get(AddOfficeOfTransitETAYesNoPage(index)) mustNot be(defined)
          postChange.get(OfficeOfTransitETAPage(index)) mustNot be(defined)
        }
      }

      "when Yes selected" - {
        "must do nothing" in {
          val preChange = emptyUserAnswers
            .setValue(OfficeOfTransitCountryPage(index), transitCountry)
            .setValue(OfficeOfTransitPage(index), transitCustomsOffice)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), true)
            .setValue(OfficeOfTransitETAPage(index), eta)
          val postChange = preChange.setValue(AddOfficeOfTransitYesNoPage, true)

          postChange.get(OfficeOfTransitCountryPage(index)) must be(defined)
          postChange.get(OfficeOfTransitPage(index)) must be(defined)
          postChange.get(AddOfficeOfTransitETAYesNoPage(index)) must be(defined)
          postChange.get(OfficeOfTransitETAPage(index)) must be(defined)
        }
      }
    }
  }
}
