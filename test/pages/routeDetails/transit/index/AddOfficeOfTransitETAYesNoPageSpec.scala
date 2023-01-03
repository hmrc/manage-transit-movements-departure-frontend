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

package pages.routeDetails.transit.index

import pages.behaviours.PageBehaviours

class AddOfficeOfTransitETAYesNoPageSpec extends PageBehaviours {

  "AddOfficeOfTransitETAYesNoPage" - {

    beRetrievable[Boolean](AddOfficeOfTransitETAYesNoPage(index))

    beSettable[Boolean](AddOfficeOfTransitETAYesNoPage(index))

    beRemovable[Boolean](AddOfficeOfTransitETAYesNoPage(index))

    "cleanup" - {
      val eta = arbitraryDateTime.arbitrary.sample.get

      "when No selected" - {
        "must clean up Office Of Transit ETA page" in {
          val preChange = emptyUserAnswers
            .setValue(AddOfficeOfTransitETAYesNoPage(index), true)
            .setValue(OfficeOfTransitETAPage(index), eta)

          val postChange = preChange.setValue(AddOfficeOfTransitETAYesNoPage(index), false)

          postChange.get(OfficeOfTransitETAPage(index)) mustNot be(defined)
        }
      }

      "when Yes selected" - {
        "must do nothing" in {
          val preChange = emptyUserAnswers
            .setValue(AddOfficeOfTransitETAYesNoPage(index), true)
            .setValue(OfficeOfTransitETAPage(index), eta)

          val postChange = preChange.setValue(AddOfficeOfTransitETAYesNoPage(index), true)

          postChange.get(OfficeOfTransitETAPage(index)) must be(defined)
        }
      }
    }
  }
}
