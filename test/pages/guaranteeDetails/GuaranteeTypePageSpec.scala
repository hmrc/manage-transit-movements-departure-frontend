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

package pages.guaranteeDetails

import models.DeclarationType.Option4
import models.guaranteeDetails.GuaranteeType
import models.{DeclarationType, Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.preTaskList.DeclarationTypePage

class GuaranteeTypePageSpec extends PageBehaviours {

  "GuaranteeTypePage" - {

    beRetrievable[GuaranteeType](GuaranteeTypePage(index))

    beSettable[GuaranteeType](GuaranteeTypePage(index))

    beRemovable[GuaranteeType](GuaranteeTypePage(index))

    "route" - {
      "when TIR declaration type" - {
        "must point to GuaranteeAddedTIRController" in {
          forAll(arbitrary[Index], arbitrary[Mode]) {
            (index, mode) =>
              val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, Option4)
              GuaranteeTypePage(index).route(userAnswers, mode).get.url mustBe
                controllers.guaranteeDetails.routes.GuaranteeAddedTIRController.onPageLoad(userAnswers.lrn).url
          }
        }
      }

      "when non-TIR declaration type" - {
        "must point to GuaranteeTypeController" in {
          forAll(arbitrary[DeclarationType](arbitraryNonOption4DeclarationType), arbitrary[Index], arbitrary[Mode]) {
            (declarationType, index, mode) =>
              val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, declarationType)
              GuaranteeTypePage(index).route(userAnswers, mode).get.url mustBe
                controllers.guaranteeDetails.routes.GuaranteeTypeController.onPageLoad(userAnswers.lrn, mode, index).url
          }
        }
      }
    }
  }
}
