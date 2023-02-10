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
import models.domain.UserAnswersReader
import models.journeyDomain.transport.equipment.EquipmentDomain
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.equipment.AddTransportEquipmentYesNoPage
import pages.transport.equipment.index.{AddContainerIdentificationNumberYesNoPage, ContainerIdentificationNumberPage}
import viewModels.ListItem

class EquipmentsAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "EquipmentsAnswersHelperSpec" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = new EquipmentsAnswersHelper(userAnswers, mode)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with a complete equipment" - {
        "and at index 0 and add equipment yes/no page is defined and true" - {
          "must return one list item with remove link" in {
            val initialAnswers = emptyUserAnswers.setValue(AddTransportEquipmentYesNoPage, true)
            forAll(arbitrary[Mode], arbitraryEquipmentAnswers(initialAnswers, equipmentIndex)) {
              (mode, userAnswers) =>
                val equipment = UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(equipmentIndex)).run(userAnswers).value
                val helper    = new EquipmentsAnswersHelper(userAnswers, mode)

                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = equipment.asString,
                      changeUrl = routes.EquipmentAnswersController.onPageLoad(userAnswers.lrn, mode, equipmentIndex).url,
                      removeUrl = Some(routes.RemoveTransportEquipmentController.onPageLoad(userAnswers.lrn, mode, equipmentIndex).url)
                    )
                  )
                )
            }
          }
        }

        "and add equipment yes/no page is undefined" - {
          "must return one list item with no remove link" in {
            forAll(arbitrary[Mode], arbitraryEquipmentAnswers(emptyUserAnswers, equipmentIndex)) {
              (mode, userAnswers) =>
                val equipment = UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(equipmentIndex)).run(userAnswers).value
                val helper    = new EquipmentsAnswersHelper(userAnswers, mode)

                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = equipment.asString,
                      changeUrl = routes.EquipmentAnswersController.onPageLoad(userAnswers.lrn, mode, equipmentIndex).url,
                      removeUrl = None
                    )
                  )
                )
            }
          }
        }
      }

      "when user answers populated with an in progress equipment" - {
        "and container id number is defined" - {
          "must return an in progress list item" in {
            forAll(arbitrary[Mode], nonEmptyString) {
              (mode, containerId) =>
                val userAnswers = emptyUserAnswers.setValue(ContainerIdentificationNumberPage(equipmentIndex), containerId)
                val helper      = new EquipmentsAnswersHelper(userAnswers, mode)
                val result      = helper.listItems
                result.head.left.value.name mustBe s"Transport equipment 1 - container $containerId"
            }
          }
        }

        "and container id number is undefined" - {
          "must return an in progress list item" in {
            forAll(arbitrary[Mode]) {
              mode =>
                val userAnswers = emptyUserAnswers.setValue(AddContainerIdentificationNumberYesNoPage(equipmentIndex), true)
                val helper      = new EquipmentsAnswersHelper(userAnswers, mode)
                val result      = helper.listItems
                result.head.left.value.name mustBe "Transport equipment 1"
            }
          }
        }
      }
    }
  }

}
