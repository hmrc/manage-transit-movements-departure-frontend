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
import generators.Generators
import models.Mode
import models.domain.UserAnswersReader
import models.journeyDomain.transport.equipment.EquipmentDomain
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
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
        "and add equipment yes/no page is defined and true" - {
          "must return one list item with remove link" ignore {
            forAll(arbitrary[Mode], arbitraryEquipmentAnswers(emptyUserAnswers, equipmentIndex)) {
              (mode, userAnswers) =>
                val equipment = UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(equipmentIndex)).run(userAnswers).value
                val helper    = new EquipmentsAnswersHelper(userAnswers, mode)

                helper.listItems mustBe Seq( // TODO sort out test logic
                  Right(
                    ListItem(
                      name = ???,
                      changeUrl = ???,
                      removeUrl = ???
                    )
                  )
                )
            }
          }
        }

        "and add goodsItemNumber yes/no page is undefined" - {
          "must return one list item with no remove link" ignore {
            forAll(arbitrary[Mode], arbitraryEquipmentAnswers(emptyUserAnswers, equipmentIndex)) {
              (mode, userAnswers) =>
                val equipment = UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(equipmentIndex)).run(userAnswers).value
                val helper    = new EquipmentsAnswersHelper(userAnswers, mode)

                helper.listItems mustBe Seq( // TODO sort out test logic
                  Right(
                    ListItem(
                      name = ???,
                      changeUrl = ???,
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
