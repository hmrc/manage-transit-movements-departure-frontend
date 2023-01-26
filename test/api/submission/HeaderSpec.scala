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

package api.submission

import base.SpecBase
import commonTestUtils.UserAnswersSpecHelper
import generated.{MESSAGE_1Sequence, MESSAGE_FROM_TRADERSequence}
import generators.Generators

class HeaderSpec extends SpecBase with UserAnswersSpecHelper with Generators {

  "Conversions" - {

    "message is called" - {

      "will convert to API format" in {

        val converted = Header.message

        val expected = MESSAGE_FROM_TRADERSequence(
          None,
          MESSAGE_1Sequence(
            messageRecipient = "NCTS",
            preparationDateAndTime = converted.messagE_1Sequence2.preparationDateAndTime,
            messageIdentification = "CC015C"
          )
        )

        converted mustBe expected

      }

    }

    "messageType is called" - {

      "will convert to API format" in {

        Header.messageType.toString mustBe "CC015C"

      }

    }

  }
}
