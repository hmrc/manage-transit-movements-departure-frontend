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

package pages.routeDetails.loadingAndUnloading

import pages.behaviours.PageBehaviours
import pages.sections.routeDetails.unloading.UnloadingSection
import play.api.libs.json.Json

class AddPlaceOfUnloadingPageSpec extends PageBehaviours {

  "AddPlaceOfUnloadingPage" - {

    beRetrievable[Boolean](AddPlaceOfUnloadingPage)

    beSettable[Boolean](AddPlaceOfUnloadingPage)

    beRemovable[Boolean](AddPlaceOfUnloadingPage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up unloading section" in {
          val preChange = emptyUserAnswers.setValue(UnloadingSection, Json.obj("foo" -> "bar"))

          val postChange = preChange.setValue(AddPlaceOfUnloadingPage, false)

          postChange.get(UnloadingSection) mustNot be(defined)
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          val preChange = emptyUserAnswers.setValue(UnloadingSection, Json.obj("foo" -> "bar"))

          val postChange = preChange.setValue(AddPlaceOfUnloadingPage, true)

          postChange.get(UnloadingSection) must be(defined)
        }
      }
    }
  }
}
