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
import controllers.transport.equipment.index.itemNumber.routes
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.equipment.index.{AddGoodsItemNumberYesNoPage, ContainerIdentificationNumberPage}
import pages.transport.equipment.index.itemNumber.ItemNumberPage
import viewModels.ListItem

class GoodsItemNumbersAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "GoodsItemNumbersAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = new GoodsItemNumbersAnswersHelper(userAnswers, mode, equipmentIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with complete goodsItemNumbers" - {
        "and add goodsItemNumber yes/no page is defined" - {
          "must return list items with remove links" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr, Gen.alphaNumStr) {
              (mode, itemNumber, containerId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(ContainerIdentificationNumberPage(equipmentIndex), containerId)
                  .setValue(AddGoodsItemNumberYesNoPage(equipmentIndex), true)
                  .setValue(ItemNumberPage(equipmentIndex, Index(0)), itemNumber)
                  .setValue(ItemNumberPage(equipmentIndex, Index(1)), itemNumber)

                val helper = new GoodsItemNumbersAnswersHelper(userAnswers, mode, equipmentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = itemNumber,
                      changeUrl = routes.ItemNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, Index(0)).url,
                      removeUrl = Some(routes.RemoveItemNumberYesNoController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, Index(0)).url)
                    )
                  ),
                  Right(
                    ListItem(
                      name = itemNumber,
                      changeUrl = routes.ItemNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, Index(1)).url,
                      removeUrl = Some(routes.RemoveItemNumberYesNoController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, Index(1)).url)
                    )
                  )
                )
            }
          }
        }

        "and add goodsItemNumber yes/no page is undefined" - {
          "and containerIdentification number page is undefined" - {
            "must return list items with no remove link" in {
              forAll(arbitrary[Mode], Gen.alphaNumStr) {
                (mode, itemNumber) =>
                  val userAnswers = emptyUserAnswers
                    .setValue(ItemNumberPage(equipmentIndex, Index(0)), itemNumber)
                    .setValue(ItemNumberPage(equipmentIndex, Index(1)), itemNumber)

                  val helper = new GoodsItemNumbersAnswersHelper(userAnswers, mode, equipmentIndex)
                  helper.listItems mustBe Seq(
                    Right(
                      ListItem(
                        name = itemNumber,
                        changeUrl = routes.ItemNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, Index(0)).url,
                        removeUrl = None
                      )
                    ),
                    Right(
                      ListItem(
                        name = itemNumber,
                        changeUrl = routes.ItemNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, Index(1)).url,
                        removeUrl = None
                      )
                    )
                  )
              }
            }

          }
        }
      }
    }
  }

}
