/*
 * Copyright 2024 HM Revenue & Customs
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

import models.reference.{CustomsOffice, DeclarationType}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class OfficeOfDeparturePageSpec extends PageBehaviours {

  private val xiCustomsOffice = CustomsOffice("XI1", "Belfast", Some("phone number1"), "XI", "EN")
  private val gbCustomsOffice = CustomsOffice("GB1", "Dover", Some("phone number2"), "GB", "EN")

  "OfficeOfDeparturePage" - {

    beRetrievable[CustomsOffice](OfficeOfDeparturePage)

    beSettable[CustomsOffice](OfficeOfDeparturePage)

    beRemovable[CustomsOffice](OfficeOfDeparturePage)

    "cleanup" - {
      "when changing to a GB customs office" - {
        "and declaration type is TIR (i.e. we've changed from XI to GB)" - {
          "must clean up DeclarationTypePage and TIRCarnetReferencePage" in {
            forAll(arbitrary[String], arbitrary[DeclarationType](arbitraryTIRDeclarationType)) {
              (ref, declarationType) =>
                val preChange = emptyUserAnswers
                  .setValue(OfficeOfDeparturePage, xiCustomsOffice)
                  .setValue(DeclarationTypePage, declarationType)
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
