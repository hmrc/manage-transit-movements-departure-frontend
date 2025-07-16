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

import models.ProcedureType
import models.ProcedureType._
import models.reference.DeclarationType
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ProcedureTypePageSpec extends PageBehaviours {

  "ProcedureTypePage" - {

    beRetrievable[ProcedureType](ProcedureTypePage)

    beSettable[ProcedureType](ProcedureTypePage)

    beRemovable[ProcedureType](ProcedureTypePage)

    "cleanup" - {

      "when changing from Normal to Simplified" - {
        "and declaration type is TIR" - {
          "must clean up DeclarationTypePage and TIRCarnetReferencePage" in {
            forAll(arbitrary[String], arbitrary[DeclarationType](arbitraryTIRDeclarationType)) {
              (ref, declarationType) =>
                val preChange = emptyUserAnswers
                  .setValue(ProcedureTypePage, Normal)
                  .setValue(DeclarationTypePage, declarationType)
                  .setValue(TIRCarnetReferencePage, ref)

                val postChange = preChange.setValue(ProcedureTypePage, Simplified)

                postChange.get(DeclarationTypePage) must not be defined
                postChange.get(TIRCarnetReferencePage) must not be defined
            }
          }
        }
      }
    }
  }
}
