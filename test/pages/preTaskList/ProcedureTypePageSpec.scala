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

import models.ProcedureType
import models.ProcedureType._
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ProcedureTypePageSpec extends PageBehaviours {

  "ProcedureTypePage" - {

    beRetrievable[ProcedureType](ProcedureTypePage)

    beSettable[ProcedureType](ProcedureTypePage)

    beRemovable[ProcedureType](ProcedureTypePage)

    "cleanup" - {

      "must remove TIRCarnetReferencePage" - {
        "when Simplified selected" in {
          forAll(arbitrary[String]) {
            carnetReference =>
              val preChange  = emptyUserAnswers.unsafeSetVal(TIRCarnetReferencePage)(carnetReference)
              val postChange = preChange.set(ProcedureTypePage, Simplified).success.value

              postChange.get(TIRCarnetReferencePage) mustNot be(defined)
          }
        }
      }

      "must not remove TIRCarnetReferencePage" - {
        "when Normal selected" in {
          forAll(arbitrary[String]) {
            carnetReference =>
              val preChange  = emptyUserAnswers.unsafeSetVal(TIRCarnetReferencePage)(carnetReference)
              val postChange = preChange.set(ProcedureTypePage, Normal).success.value

              postChange.get(TIRCarnetReferencePage) must be(defined)
          }
        }
      }
    }
  }
}
