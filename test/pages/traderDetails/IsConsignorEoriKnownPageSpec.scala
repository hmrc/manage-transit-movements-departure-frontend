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

package pages.traderDetails

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsConsignorEoriKnownPageSpec extends PageBehaviours {

  "IsConsignorEoriKnownPage" - {

    beRetrievable[Boolean](IsConsignorEoriKnownPage)

    beSettable[Boolean](IsConsignorEoriKnownPage)

    beRemovable[Boolean](IsConsignorEoriKnownPage)

    clearDownItems[Boolean](IsConsignorEoriKnownPage)
  }

  "cleanup" - {

    "must remove ConsignorEoriPage when there is a change of the answer to 'No'" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(IsConsignorEoriKnownPage, true)
            .success
            .value
            .set(ConsignorEoriPage, "GB123456")
            .success
            .value
            .set(IsConsignorEoriKnownPage, false)
            .success
            .value

          result.get(ConsignorEoriPage) must not be defined
      }
    }
  }

}
