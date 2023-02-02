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

package pages.transport.equipment.index

import pages.behaviours.PageBehaviours
import pages.sections.transport.equipment.ItemNumbersSection
import play.api.libs.json.{JsArray, Json}

class AddGoodsItemNumberYesNoPageSpec extends PageBehaviours {

  "AddGoodsItemNumberYesNoPage" - {

    beRetrievable[Boolean](AddGoodsItemNumberYesNoPage(equipmentIndex))

    beSettable[Boolean](AddGoodsItemNumberYesNoPage(equipmentIndex))

    beRemovable[Boolean](AddGoodsItemNumberYesNoPage(equipmentIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove goods item numbers" in {
          val userAnswers = emptyUserAnswers
            .setValue(ItemNumbersSection(equipmentIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

          val result = userAnswers.setValue(AddGoodsItemNumberYesNoPage(equipmentIndex), false)

          result.get(ItemNumbersSection(equipmentIndex)) must not be defined
        }
      }
    }
  }
}
