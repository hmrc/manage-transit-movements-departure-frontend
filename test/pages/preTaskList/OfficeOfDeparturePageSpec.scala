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

package pages.preTaskList

import models.DeclarationType.Option4
import models.reference.CustomsOffice
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class OfficeOfDeparturePageSpec extends PageBehaviours {

  "OfficeOfDeparturePage" - {

    beRetrievable[CustomsOffice](OfficeOfDeparturePage)

    beSettable[CustomsOffice](OfficeOfDeparturePage)

    beRemovable[CustomsOffice](OfficeOfDeparturePage)

    "cleanup" - {
      "when changing from XI to GB" - {
        "and declaration type is TIR" - {
          "must clean up DeclarationTypePage and TIRCarnetReferencePage" in {
            forAll(
              arbitrary[CustomsOffice](arbitraryXiCustomsOffice),
              arbitrary[CustomsOffice](arbitraryGbCustomsOffice),
              arbitrary[String]
            ) {
              (xiCustomsOffice, gbCustomsOffice, ref) =>
                val preChange = emptyUserAnswers
                  .setValue(OfficeOfDeparturePage, xiCustomsOffice)
                  .setValue(DeclarationTypePage, Option4)
                  .setValue(TIRCarnetReferencePage, ref)

                val postChange = preChange.setValue(OfficeOfDeparturePage, gbCustomsOffice)

                postChange.get(DeclarationTypePage) mustNot be(defined)
                postChange.get(TIRCarnetReferencePage) mustNot be(defined)
            }
          }
        }
      }
    }
  }
}
