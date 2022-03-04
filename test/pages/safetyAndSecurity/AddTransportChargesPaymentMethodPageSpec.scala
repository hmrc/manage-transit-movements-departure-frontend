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
import models.reference.MethodOfPayment
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddTransportChargesPaymentMethodPageSpec extends PageBehaviours {

  "AddTransportChargesPaymentMethodPage" - {

    beRetrievable[Boolean](AddTransportChargesPaymentMethodPage)

    beSettable[Boolean](AddTransportChargesPaymentMethodPage)

    beRemovable[Boolean](AddTransportChargesPaymentMethodPage)

    clearDownItems[Boolean](AddTransportChargesPaymentMethodPage)
  }

  "cleanup" - {

    "must remove TransportChargesPaymentMethodPage when AddTransportChargesPaymentMethodPage is false" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>
          val result = userAnswers
            .set(AddTransportChargesPaymentMethodPage, true)
            .success
            .value
            .set(TransportChargesPaymentMethodPage, MethodOfPayment("code", "description"))
            .success
            .value
            .set(AddTransportChargesPaymentMethodPage, false)
            .success
            .value

          result.get(TransportChargesPaymentMethodPage) must not be defined
      }
    }
  }
}
