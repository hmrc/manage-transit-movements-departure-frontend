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

package pages.transport.transportMeans.active

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ConveyanceReferenceNumberYesNoPageSpec extends PageBehaviours {

  "ConveyanceReferenceNumberYesNoPage" - {

    beRetrievable[Boolean](ConveyanceReferenceNumberYesNoPage(index))

    beSettable[Boolean](ConveyanceReferenceNumberYesNoPage(index))

    beRemovable[Boolean](ConveyanceReferenceNumberYesNoPage(index))

    "cleanup" - {
      "when NO selected" - {
        "must clean up ConveyanceReferenceNumberPage" in {
          forAll(arbitrary[String]) {
            crn =>
              val userAnswers = emptyUserAnswers
                .setValue(ConveyanceReferenceNumberYesNoPage(index), true)
                .setValue(ConveyanceReferenceNumberPage(index), crn)

              val result = userAnswers.setValue(ConveyanceReferenceNumberYesNoPage(index), false)

              result.get(ConveyanceReferenceNumberPage(index)) must not be defined
          }
        }
      }
    }
  }
}
