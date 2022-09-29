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

import models.DateTime
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.routeDetails.transit.index._

class T2DeclarationTypeYesNoPageSpec extends PageBehaviours {

  "T2DeclarationTypeYesNoPage" - {

    beRetrievable[Boolean](T2DeclarationTypeYesNoPage)

    beSettable[Boolean](T2DeclarationTypeYesNoPage)

    beRemovable[Boolean](T2DeclarationTypeYesNoPage)

    "cleanup" - {
      val transitCountry       = arbitrary[Country].sample.value
      val transitCustomsOffice = arbitrary[CustomsOffice].sample.value
      val eta                  = arbitrary[DateTime].sample.value

      "when answer changes" - {
        "must clean up add office of transit yes/no and offices of transit" in {
          val bool = arbitrary[Boolean].sample.value

          val preChange = emptyUserAnswers
            .setValue(T2DeclarationTypeYesNoPage, bool)
            .setValue(AddOfficeOfTransitYesNoPage, true)
            .setValue(OfficeOfTransitCountryPage(index), transitCountry)
            .setValue(OfficeOfTransitPage(index), transitCustomsOffice)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), true)
            .setValue(OfficeOfTransitETAPage(index), eta)

          val postChange = preChange.setValue(T2DeclarationTypeYesNoPage, !bool)

          postChange.get(AddOfficeOfTransitYesNoPage) mustNot be(defined)
          postChange.get(OfficeOfTransitCountryPage(index)) mustNot be(defined)
          postChange.get(OfficeOfTransitPage(index)) mustNot be(defined)
          postChange.get(AddOfficeOfTransitETAYesNoPage(index)) mustNot be(defined)
          postChange.get(OfficeOfTransitETAPage(index)) mustNot be(defined)
        }
      }

      "when answer doesn't change" - {
        "must do nothing" in {
          val bool = arbitrary[Boolean].sample.value

          val preChange = emptyUserAnswers
            .setValue(T2DeclarationTypeYesNoPage, bool)
            .setValue(AddOfficeOfTransitYesNoPage, true)
            .setValue(OfficeOfTransitCountryPage(index), transitCountry)
            .setValue(OfficeOfTransitPage(index), transitCustomsOffice)
            .setValue(AddOfficeOfTransitETAYesNoPage(index), true)
            .setValue(OfficeOfTransitETAPage(index), eta)

          val postChange = preChange.setValue(T2DeclarationTypeYesNoPage, bool)

          postChange.get(AddOfficeOfTransitYesNoPage) must be(defined)
          postChange.get(OfficeOfTransitCountryPage(index)) must be(defined)
          postChange.get(OfficeOfTransitPage(index)) must be(defined)
          postChange.get(AddOfficeOfTransitETAYesNoPage(index)) must be(defined)
          postChange.get(OfficeOfTransitETAPage(index)) must be(defined)
        }
      }
    }
  }
}
