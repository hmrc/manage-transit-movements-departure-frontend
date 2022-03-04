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

package pages.safetyAndSecurity

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddConveyanceReferenceNumberPageSpec extends PageBehaviours {

  "AddConveyanceReferenceNumberPage" - {

    beRetrievable[Boolean](AddConveyanceReferenceNumberPage)

    beSettable[Boolean](AddConveyanceReferenceNumberPage)

    beRemovable[Boolean](AddConveyanceReferenceNumberPage)

    clearDownItems[Boolean](AddConveyanceReferenceNumberPage)
  }

  "cleanup" - {

    "must remove ConveyanceReferenceNumberPage when AddConveyanceReferenceNumberPage is false" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(AddConveyanceReferenceNumberPage, true)
            .success
            .value
            .set(ConveyanceReferenceNumberPage, "value")
            .success
            .value
            .set(AddConveyanceReferenceNumberPage, false)
            .success
            .value

          result.get(ConveyanceReferenceNumberPage) must not be defined
      }
    }
  }
}
