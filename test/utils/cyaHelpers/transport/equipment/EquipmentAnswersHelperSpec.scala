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
import controllers.transport.equipment.index.itemNumber.routes._
import controllers.transport.equipment.index.routes._
import controllers.transport.equipment.index.seals.routes._
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.sections.transport.equipment.{ItemNumberSection, SealSection}
import pages.transport.equipment.index._
import play.api.libs.json.Json
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
                          href = AddContainerIdentificationNumberYesNoController.onPageLoad(answers.lrn, mode, index).url,
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
                          href = ContainerIdentificationNumberController.onPageLoad(answers.lrn, mode, index).url,
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
                          href = AddSealYesNoController.onPageLoad(answers.lrn, mode, index).url,
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

    "seal" - {
      "must return None" - {
        "when seal is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new EquipmentAnswersHelper(emptyUserAnswers, mode, equipmentIndex)
              val result = helper.seal(sealIndex)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when seal is defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, sealIdNumber) =>
              val userAnswers = emptyUserAnswers.setValue(seals.IdentificationNumberPage(equipmentIndex, sealIndex), sealIdNumber)
              val helper      = new EquipmentAnswersHelper(userAnswers, mode, equipmentIndex)
              val result      = helper.seal(index).get

              result.key.value mustBe "Seal 1"
              result.value.value mustBe sealIdNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe IdentificationNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, sealIndex).url
              action.visuallyHiddenText.get mustBe "seal 1"
              action.id mustBe "change-seal-1"
          }
        }
      }
    }

    "addOrRemoveSeals" - {
      "must return None" - {
        "when seals array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new EquipmentAnswersHelper(emptyUserAnswers, mode, equipmentIndex)
              val result = helper.addOrRemoveSeals
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when seals array is non-empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(SealSection(equipmentIndex, Index(0)), Json.obj("foo" -> "bar"))
              val helper  = new EquipmentAnswersHelper(answers, mode, equipmentIndex)
              val result  = helper.addOrRemoveSeals.get

              result.id mustBe "add-or-remove-seals"
              result.text mustBe "Add or remove seals"
              result.href mustBe AddAnotherSealController.onPageLoad(answers.lrn, mode, equipmentIndex).url
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
                          href = AddGoodsItemNumberYesNoController.onPageLoad(answers.lrn, mode, index).url,
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

    "itemNumber" - {
      "must return None" - {
        "when itemNumber is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new EquipmentAnswersHelper(emptyUserAnswers, mode, equipmentIndex)
              val result = helper.itemNumber(itemNumberIndex)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when itemNumber is defined" in {
          forAll(arbitrary[Mode], nonEmptyString) {
            (mode, goodsItemNumber) =>
              val userAnswers = emptyUserAnswers.setValue(itemNumber.ItemNumberPage(equipmentIndex, itemNumberIndex), goodsItemNumber)
              val helper      = new EquipmentAnswersHelper(userAnswers, mode, equipmentIndex)
              val result      = helper.itemNumber(index).get

              result.key.value mustBe "Goods item number 1"
              result.value.value mustBe goodsItemNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe ItemNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, itemNumberIndex).url
              action.visuallyHiddenText.get mustBe "goods item number 1"
              action.id mustBe "change-goods-item-number-1"
          }
        }
      }
    }

    "addOrRemoveItemNumbers" - {
      "must return None" - {
        "when item numbers array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new EquipmentAnswersHelper(emptyUserAnswers, mode, equipmentIndex)
              val result = helper.addOrRemoveItemNumbers
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when item numbers array is non-empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(ItemNumberSection(equipmentIndex, Index(0)), Json.obj("foo" -> "bar"))
              val helper  = new EquipmentAnswersHelper(answers, mode, equipmentIndex)
              val result  = helper.addOrRemoveItemNumbers.get

              result.id mustBe "add-or-remove-goods-item-numbers"
              result.text mustBe "Add or remove goods item numbers"
              result.href mustBe AddAnotherGoodsItemNumberController.onPageLoad(answers.lrn, mode, index).url
          }
        }
      }
    }
  }
}
