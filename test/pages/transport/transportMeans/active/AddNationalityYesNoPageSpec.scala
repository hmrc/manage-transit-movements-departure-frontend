/*
 * Copyright 2022 HM Revenue & Customs
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

package pages.transport.transportMeans.active

import models.reference.Nationality
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddNationalityYesNoPageSpec extends PageBehaviours {

  "AddNationalityYesNoPage" - {

    beRetrievable[Boolean](AddNationalityYesNoPage(activeIndex))

    beSettable[Boolean](AddNationalityYesNoPage(activeIndex))

    beRemovable[Boolean](AddNationalityYesNoPage(activeIndex))

    "cleanup" - {
      "when NO selected" - {
        "must clean up NationalityPage" in {
          forAll(arbitrary[Nationality]) {
            nationality =>
              val userAnswers = emptyUserAnswers
                .setValue(AddNationalityYesNoPage(index), true)
                .setValue(NationalityPage(index), nationality)

              val result = userAnswers.setValue(AddNationalityYesNoPage(index), false)

              result.get(NationalityPage(index)) must not be defined
          }
        }
      }
    }
  }
}
