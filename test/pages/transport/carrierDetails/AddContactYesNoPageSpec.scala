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

package pages.transport.carrierDetails

import pages.behaviours.PageBehaviours
import pages.sections.transport.carrierDetails.ContactSection
import play.api.libs.json.Json

class AddContactYesNoPageSpec extends PageBehaviours {

  "AddContactYesNoPage" - {

    beRetrievable[Boolean](AddContactYesNoPage)

    beSettable[Boolean](AddContactYesNoPage)

    beRemovable[Boolean](AddContactYesNoPage)

    "cleanup" - {
      "must remove contact details when no selected" in {
        val userAnswers = emptyUserAnswers
          .setValue(AddContactYesNoPage, true)
          .setValue(ContactSection, Json.obj("foo" -> "bar"))

        val result = userAnswers.setValue(AddContactYesNoPage, false)

        result.get(ContactSection) must not be defined
      }

      "must keep contact details when yes selected" in {
        val userAnswers = emptyUserAnswers
          .setValue(AddContactYesNoPage, true)
          .setValue(ContactSection, Json.obj("foo" -> "bar"))

        val result = userAnswers.setValue(AddContactYesNoPage, true)

        result.get(ContactSection) must be(defined)
      }
    }
  }
}
