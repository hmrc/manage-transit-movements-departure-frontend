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

package utils.cyaHelpers.transport.equipment

import base.SpecBase
import controllers.transport.equipment.index.routes
import generators.Generators
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.equipment.index._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class EquipmentAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "EquipmentAnswersHelper" - {

    "containerIdentificationNumberYesNo" - {
      "must return None" - {
        "when AddContainerIdentificationNumberYesNoPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new EquipmentAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.containerIdentificationNumberYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddContainerIdentificationNumberYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddContainerIdentificationNumberYesNoPage(index), true)

              val helper = new EquipmentAnswersHelper(answers, mode, index)
              val result = helper.containerIdentificationNumberYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add a container identification number?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AddContainerIdentificationNumberYesNoController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("if you want to add an identification number"),
                          attributes = Map("id" -> "change-add-container-identification-number")
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

    "containerIdentificationNumber" - {
      "must return None" - {
        "when ContainerIdentificationNumberPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new EquipmentAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.containerIdentificationNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ContainerIdentificationNumberPage defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, containerIdentificationNumber) =>
              val answers = emptyUserAnswers
                .setValue(ContainerIdentificationNumberPage(index), containerIdentificationNumber)

              val helper = new EquipmentAnswersHelper(answers, mode, index)
              val result = helper.containerIdentificationNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("Container identification number".toText),
                  value = Value(containerIdentificationNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.ContainerIdentificationNumberController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("identification number"),
                          attributes = Map("id" -> "change-container-identification-number")
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

    "sealsYesNo" - {
      "must return None" - {
        "when AddSealYesNoPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new EquipmentAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.sealsYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddSealYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddSealYesNoPage(index), true)

              val helper = new EquipmentAnswersHelper(answers, mode, index)
              val result = helper.sealsYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add a seal?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AddSealYesNoController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("if you want to add a seal"),
                          attributes = Map("id" -> "change-add-seals")
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

    "itemNumbersYesNo" - {
      "must return None" - {
        "when AddGoodsItemNumberYesNoPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new EquipmentAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.itemNumbersYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddGoodsItemNumberYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers
                .setValue(AddGoodsItemNumberYesNoPage(index), true)

              val helper = new EquipmentAnswersHelper(answers, mode, index)
              val result = helper.itemNumbersYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add a goods item number?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AddGoodsItemNumberYesNoController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("if you want to add a goods item number"),
                          attributes = Map("id" -> "change-add-item-numbers")
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
