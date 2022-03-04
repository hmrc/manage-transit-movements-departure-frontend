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

import models.{CommonAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddConsigneePageSpec extends PageBehaviours {

  "AddConsigneePage" - {

    beRetrievable[Boolean](AddConsigneePage)

    beSettable[Boolean](AddConsigneePage)

    beRemovable[Boolean](AddConsigneePage)

    clearDownItems[Boolean](AddConsigneePage)
  }

  "cleanup" - {

    "must remove ConsigneeAddressPage, ConsigneeNamePage and WhatIsConsigneeEoriPage when they exist in userAnswers" in {

      val consigneeAddress = arbitrary[CommonAddress].sample.value
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(AddConsigneePage, true)
            .success
            .value
            .set(ConsigneeNamePage, "answer")
            .success
            .value
            .set(ConsigneeAddressPage, consigneeAddress)
            .success
            .value
            .set(WhatIsConsigneeEoriPage, "GB123456")
            .success
            .value
            .set(IsConsigneeEoriKnownPage, true)
            .success
            .value
            .set(AddConsigneePage, false)
            .success
            .value

          result.get(ConsigneeNamePage) must not be defined
          result.get(ConsigneeAddressPage) must not be defined
          result.get(WhatIsConsigneeEoriPage) must not be defined
          result.get(IsConsigneeEoriKnownPage) must not be defined

      }
    }
  }
}
