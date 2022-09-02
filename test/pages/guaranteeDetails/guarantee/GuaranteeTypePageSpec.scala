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

package pages.guaranteeDetails.guarantee

import models.DeclarationType.Option4
import models.{DeclarationType, GuaranteeType, Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.preTaskList.DeclarationTypePage

class GuaranteeTypePageSpec extends PageBehaviours {

  "GuaranteeTypePage" - {

    beRetrievable[GuaranteeType](GuaranteeTypePage(index))

    beSettable[GuaranteeType](GuaranteeTypePage(index))

    beRemovable[GuaranteeType](GuaranteeTypePage(index))

    "cleanup" - {
      "when value has changed" - {
        "must clean up" in {
          forAll(arbitrary[GuaranteeType], arbitrary[String], arbitrary[BigDecimal]) {
            (guaranteeType, str, amount) =>
              val preChange = emptyUserAnswers
                .setValue(GuaranteeTypePage(index), guaranteeType)
                .setValue(ReferenceNumberPage(index), str)
                .setValue(AccessCodePage(index), str)
                .setValue(LiabilityAmountPage(index), amount)
                .setValue(OtherReferenceYesNoPage(index), true)
                .setValue(OtherReferencePage(index), str)

              forAll(arbitrary[GuaranteeType].suchThat(_ != guaranteeType)) {
                changedGuaranteeType =>
                  val postChange = preChange.setValue(GuaranteeTypePage(index), changedGuaranteeType)

                  postChange.get(ReferenceNumberPage(index)) mustNot be(defined)
                  postChange.get(AccessCodePage(index)) mustNot be(defined)
                  postChange.get(LiabilityAmountPage(index)) mustNot be(defined)
                  postChange.get(OtherReferenceYesNoPage(index)) mustNot be(defined)
                  postChange.get(OtherReferencePage(index)) mustNot be(defined)
              }
          }
        }
      }

      "when value has not changed" - {
        "must not clean up" in {
          forAll(arbitrary[GuaranteeType], arbitrary[String], arbitrary[BigDecimal]) {
            (guaranteeType, str, amount) =>
              val preChange = emptyUserAnswers
                .setValue(GuaranteeTypePage(index), guaranteeType)
                .setValue(ReferenceNumberPage(index), str)
                .setValue(AccessCodePage(index), str)
                .setValue(LiabilityAmountPage(index), amount)
                .setValue(OtherReferenceYesNoPage(index), true)
                .setValue(OtherReferencePage(index), str)

              val postChange = preChange.setValue(GuaranteeTypePage(index), guaranteeType)

              postChange.get(ReferenceNumberPage(index)) must be(defined)
              postChange.get(AccessCodePage(index)) must be(defined)
              postChange.get(LiabilityAmountPage(index)) must be(defined)
              postChange.get(OtherReferenceYesNoPage(index)) must be(defined)
              postChange.get(OtherReferencePage(index)) must be(defined)
          }
        }
      }
    }

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
                controllers.guaranteeDetails.guarantee.routes.GuaranteeTypeController.onPageLoad(userAnswers.lrn, mode, index).url
          }
        }
      }
    }
  }
}
