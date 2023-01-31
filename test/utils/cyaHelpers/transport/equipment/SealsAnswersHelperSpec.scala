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
import controllers.transport.equipment.index.seals.routes
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.equipment.index.AddSealYesNoPage
import pages.transport.equipment.index.seals.IdentificationNumberPage
import viewModels.ListItem

class SealsAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "SealsAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = new SealsAnswersHelper(userAnswers, mode, equipmentIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with complete seals" - {
        "and add seal yes/no page is defined" - {
          "must return list items with remove links" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, sealId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(AddSealYesNoPage(equipmentIndex), true)
                  .setValue(IdentificationNumberPage(equipmentIndex, Index(0)), sealId)
                  .setValue(IdentificationNumberPage(equipmentIndex, Index(1)), sealId)

                val helper = new SealsAnswersHelper(userAnswers, mode, equipmentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = sealId,
                      changeUrl = routes.IdentificationNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, Index(0)).url,
                      removeUrl = Some(
                        controllers.transport.supplyChainActors.index.routes.RemoveSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, Index(0)).url
                      ) //TODO: Change to RemoveSeal when added
                    )
                  ),
                  Right(
                    ListItem(
                      name = sealId,
                      changeUrl = routes.IdentificationNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, Index(1)).url,
                      removeUrl = Some(
                        controllers.transport.supplyChainActors.index.routes.RemoveSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, Index(1)).url
                      ) //TODO: Change to RemoveSeal when added
                    )
                  )
                )
            }
          }
        }

        "and add seal yes/no page is undefined" - {
          "must return list items with no remove link a index 0" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, sealId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(IdentificationNumberPage(equipmentIndex, Index(0)), sealId)
                  .setValue(IdentificationNumberPage(equipmentIndex, Index(1)), sealId)

                val helper = new SealsAnswersHelper(userAnswers, mode, equipmentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = sealId,
                      changeUrl = routes.IdentificationNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, Index(0)).url,
                      removeUrl = None
                    )
                  ),
                  Right(
                    ListItem(
                      name = sealId,
                      changeUrl = routes.IdentificationNumberController.onPageLoad(userAnswers.lrn, mode, equipmentIndex, Index(1)).url,
                      removeUrl = Some(
                        controllers.transport.supplyChainActors.index.routes.RemoveSupplyChainActorController.onPageLoad(userAnswers.lrn, mode, Index(1)).url
                      ) //TODO: Change to RemoveSeal when added
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
