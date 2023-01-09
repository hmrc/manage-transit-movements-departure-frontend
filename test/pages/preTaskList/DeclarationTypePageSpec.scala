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

package pages.preTaskList

import models.DeclarationType
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class DeclarationTypePageSpec extends PageBehaviours {

  "DeclarationTypePage" - {

    beRetrievable[DeclarationType](DeclarationTypePage)

    beSettable[DeclarationType](DeclarationTypePage)

    beRemovable[DeclarationType](DeclarationTypePage)

    "cleanup" - {
      "must remove TIRCarnetReferencePage" - {
        "when anything other than Option4 (TIR) selected" in {
          forAll(arbitrary[String], arbitrary[DeclarationType].suchThat(_ != DeclarationType.Option4)) {
            (carnetReference, declarationType) =>
              val preChange  = emptyUserAnswers.setValue(TIRCarnetReferencePage, carnetReference)
              val postChange = preChange.setValue(DeclarationTypePage, declarationType)

              postChange.get(TIRCarnetReferencePage) mustNot be(defined)
          }
        }
      }

      "must not remove TIRCarnetReferencePage" - {
        "when Option4 (TIR) selected" in {
          forAll(arbitrary[String]) {
            carnetReference =>
              val preChange  = emptyUserAnswers.setValue(TIRCarnetReferencePage, carnetReference)
              val postChange = preChange.setValue(DeclarationTypePage, DeclarationType.Option4)

              postChange.get(TIRCarnetReferencePage) must be(defined)
          }
        }
      }
    }
  }
}
