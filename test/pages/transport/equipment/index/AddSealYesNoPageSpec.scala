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
import pages.sections.transport.equipment.SealsSection
import play.api.libs.json.{JsArray, Json}

class AddSealYesNoPageSpec extends PageBehaviours {

  "AddSealYesNoPage" - {

    beRetrievable[Boolean](AddSealYesNoPage(equipmentIndex))

    beSettable[Boolean](AddSealYesNoPage(equipmentIndex))

    beRemovable[Boolean](AddSealYesNoPage(equipmentIndex))

    "cleanup" - {
      "when no selected" - {
        "must remove seals" in {
          val userAnswers = emptyUserAnswers
            .setValue(SealsSection(equipmentIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

          val result = userAnswers.setValue(AddSealYesNoPage(equipmentIndex), false)

          result.get(SealsSection(equipmentIndex)) must not be defined
        }
      }
    }
  }
}
