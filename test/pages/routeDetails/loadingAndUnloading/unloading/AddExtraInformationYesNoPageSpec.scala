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

package pages.routeDetails.loadingAndUnloading.unloading

import pages.behaviours.PageBehaviours
import pages.sections.routeDetails.unloading.UnloadingLocationSection
import play.api.libs.json.Json

class AddExtraInformationYesNoPageSpec extends PageBehaviours {

  "AddExtraInformationYesNoPage" - {

    beRetrievable[Boolean](AddExtraInformationYesNoPage)

    beSettable[Boolean](AddExtraInformationYesNoPage)

    beRemovable[Boolean](AddExtraInformationYesNoPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up unloading location section" in {
          val preChange = emptyUserAnswers.setValue(UnloadingLocationSection, Json.obj("foo" -> "bar"))

          val postChange = preChange.setValue(AddExtraInformationYesNoPage, false)

          postChange.get(UnloadingLocationSection) mustNot be(defined)
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          val preChange = emptyUserAnswers.setValue(UnloadingLocationSection, Json.obj("foo" -> "bar"))

          val postChange = preChange.setValue(AddExtraInformationYesNoPage, true)

          postChange.get(UnloadingLocationSection) must be(defined)
        }
      }
    }
  }
}
