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

package utils.cyaHelpers

import base.SpecBase
import controllers.guaranteeDetails.guarantee.routes
import forms.Constants.accessCodeLength
import generators.Generators
import models.DeclarationType.Option4
import models.guaranteeDetails.GuaranteeType
import models.guaranteeDetails.GuaranteeType._
import models.{DeclarationType, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.guaranteeDetails.guarantee._
import pages.preTaskList.DeclarationTypePage
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import utils.cyaHelpers.guaranteeDetails.GuaranteeCheckYourAnswersHelper

class GuaranteeCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "GuaranteeCheckYourAnswersHelper" - {

    "guaranteeType" - {
      "must return None" - {
        "when GuaranteeTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new GuaranteeCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.guaranteeType
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when GuaranteeTypePage defined" - {
          "when TIR" in {
            forAll(arbitrary[Mode]) {
              mode =>
                val answers = emptyUserAnswers
                  .setValue(DeclarationTypePage, Option4)
                  .setValue(GuaranteeTypePage(index), TIRGuarantee)

                val helper = new GuaranteeCheckYourAnswersHelper(answers, mode, index)
                val result = helper.guaranteeType

                result mustBe Some(
                  SummaryListRow(
                    key = Key("Guarantee type".toText),
                    value = Value("(B) Guarantee for goods dispatched under TIR procedure".toText),
                    actions = None
                  )
                )
            }
          }

          "when not TIR" in {
            forAll(
              arbitrary[DeclarationType](arbitraryNonOption4DeclarationType),
              arbitrary[GuaranteeType](arbitraryNonOption4GuaranteeType),
              arbitrary[Mode]
            ) {
              (declarationType, guaranteeType, mode) =>
                val answers = emptyUserAnswers
                  .setValue(DeclarationTypePage, declarationType)
                  .setValue(GuaranteeTypePage(index), guaranteeType)

                val helper = new GuaranteeCheckYourAnswersHelper(answers, mode, index)
                val result = helper.guaranteeType

                result mustBe Some(
                  SummaryListRow(
                    key = Key("Guarantee type".toText),
                    value = Value(messages(s"guaranteeDetails.guaranteeType.$guaranteeType").toText),
                    actions = Some(
                      Actions(
                        items = List(
                          ActionItem(
                            content = "Change".toText,
                            href = routes.GuaranteeTypeController.onPageLoad(answers.lrn, mode, index).url,
                            visuallyHiddenText = Some("type of guarantee"),
                            attributes = Map("id" -> "type")
                          )
                        )
                      )
                    )
                  )
                )
            }
          }
        }
      }
    }

    "guaranteeReferenceNumber" - {
      "must return None" - {
        "when ReferenceNumberPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new GuaranteeCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.guaranteeReferenceNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ReferenceNumberPage defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (referenceNumber, mode) =>
              val answers = emptyUserAnswers.setValue(ReferenceNumberPage(index), referenceNumber)

              val helper = new GuaranteeCheckYourAnswersHelper(answers, mode, index)
              val result = helper.guaranteeReferenceNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("Guarantee Reference Number (GRN)".toText),
                  value = Value(referenceNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.ReferenceNumberController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("guarantee reference number"),
                          attributes = Map("id" -> "reference-number")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "otherReferenceYesNo" - {
      "must return None" - {
        "when OtherReferenceYesNoPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new GuaranteeCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.otherReferenceYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when OtherReferenceYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(OtherReferenceYesNoPage(index), true)

              val helper = new GuaranteeCheckYourAnswersHelper(answers, mode, index)
              val result = helper.otherReferenceYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Add reference for the guarantee".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.OtherReferenceYesNoController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("if you want to add a reference for the guarantee"),
                          attributes = Map("id" -> "add-other-reference")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "otherReference" - {
      "must return None" - {
        "when OtherReferencePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new GuaranteeCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.otherReference
              result mustBe None
          }
        }

        "when guarantee is not of type 3 or type 8" in {
          forAll(Gen.alphaNumStr, arbitrary[GuaranteeType](arbitraryNonOption3Or8GuaranteeType), arbitrary[Mode]) {
            (referenceNumber, guaranteeType, mode) =>
              val userAnswers = emptyUserAnswers
                .setValue(GuaranteeTypePage(index), guaranteeType)
                .setValue(OtherReferencePage(index), referenceNumber)

              val helper = new GuaranteeCheckYourAnswersHelper(userAnswers, mode, index)
              val result = helper.otherReference
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when OtherReferencePage defined" - {
          "and guarantee type 3" in {
            forAll(Gen.alphaNumStr, arbitrary[Mode]) {
              (referenceNumber, mode) =>
                val answers = emptyUserAnswers
                  .setValue(GuaranteeTypePage(index), CashDepositGuarantee)
                  .setValue(OtherReferencePage(index), referenceNumber)

                val helper = new GuaranteeCheckYourAnswersHelper(answers, mode, index)
                val result = helper.otherReference

                result mustBe Some(
                  SummaryListRow(
                    key = Key("Reference for the guarantee".toText),
                    value = Value(referenceNumber.toText),
                    actions = Some(
                      Actions(
                        items = List(
                          ActionItem(
                            content = "Change".toText,
                            href = routes.OtherReferenceController.onPageLoad(answers.lrn, mode, index).url,
                            visuallyHiddenText = Some("reference for the guarantee"),
                            attributes = Map("id" -> "other-reference")
                          )
                        )
                      )
                    )
                  )
                )
            }
          }

          "and guarantee type 8" in {
            forAll(Gen.alphaNumStr, arbitrary[Mode]) {
              (referenceNumber, mode) =>
                val answers = emptyUserAnswers
                  .setValue(GuaranteeTypePage(index), GuaranteeNotRequiredExemptPublicBody)
                  .setValue(OtherReferencePage(index), referenceNumber)

                val helper = new GuaranteeCheckYourAnswersHelper(answers, mode, index)
                val result = helper.otherReference

                result mustBe Some(
                  SummaryListRow(
                    key = Key("Reference".toText),
                    value = Value(referenceNumber.toText),
                    actions = Some(
                      Actions(
                        items = List(
                          ActionItem(
                            content = "Change".toText,
                            href = routes.OtherReferenceController.onPageLoad(answers.lrn, mode, index).url,
                            visuallyHiddenText = Some("reference"),
                            attributes = Map("id" -> "other-reference")
                          )
                        )
                      )
                    )
                  )
                )
            }
          }
        }
      }
    }

    "accessCode" - {
      "must return None" - {
        "when AccessCodePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new GuaranteeCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.accessCode
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AccessCodePage defined" in {
          forAll(stringsWithLength(accessCodeLength), arbitrary[Mode]) {
            (accessCode, mode) =>
              val answers = emptyUserAnswers.setValue(AccessCodePage(index), accessCode)

              val helper = new GuaranteeCheckYourAnswersHelper(answers, mode, index)
              val result = helper.accessCode

              result mustBe Some(
                SummaryListRow(
                  key = Key("Access code".toText),
                  value = Value("••••".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AccessCodeController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("access code"),
                          attributes = Map("id" -> "access-code")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "liabilityAmount" - {
      "must return None" - {
        "when LiabilityAmountPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new GuaranteeCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.liabilityAmount
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when LiabilityAmountPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(LiabilityAmountPage(index), 1000: BigDecimal)

              val helper = new GuaranteeCheckYourAnswersHelper(answers, mode, index)
              val result = helper.liabilityAmount

              result mustBe Some(
                SummaryListRow(
                  key = Key("Liability amount (in pounds)".toText),
                  value = Value("£1,000.00".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LiabilityAmountController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("liability amount"),
                          attributes = Map("id" -> "liability-amount")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }
  }
}
