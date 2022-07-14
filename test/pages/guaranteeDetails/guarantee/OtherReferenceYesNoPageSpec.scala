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

package pages.guaranteeDetails.guarantee

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.guaranteeDetails.guarantee

class OtherReferenceYesNoPageSpec extends PageBehaviours {

  "OtherReferenceYesNoPage" - {

    beRetrievable[Boolean](OtherReferenceYesNoPage(index))

    beSettable[Boolean](guarantee.OtherReferenceYesNoPage(index))

    beRemovable[Boolean](guarantee.OtherReferenceYesNoPage(index))

    "cleanup" - {
      "when NO selected" - {
        "must clean up OtherReferencePage" in {
          forAll(arbitrary[String]) {
            ref =>
              val preChange  = emptyUserAnswers.setValue(OtherReferencePage(index), ref)
              val postChange = preChange.setValue(guarantee.OtherReferenceYesNoPage(index), false)

              postChange.get(guarantee.OtherReferencePage(index)) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must not clean up OtherReferencePage" in {
          forAll(arbitrary[String]) {
            ref =>
              val preChange  = emptyUserAnswers.setValue(guarantee.OtherReferencePage(index), ref)
              val postChange = preChange.setValue(guarantee.OtherReferenceYesNoPage(index), true)

              postChange.get(guarantee.OtherReferencePage(index)) must be(defined)
          }
        }
      }
    }
  }
}
