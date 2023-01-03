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

package views.behaviours

import org.scalacheck.{Arbitrary, Gen}
import viewModels.InputSize

trait TelephoneNumberViewBehaviours extends InputTextViewBehaviours[String] {

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  def pageWithTelephoneNumberInput(): Unit = {

    behave like pageWithInputText(Some(InputSize.Width20))

    "page with a telephone number field" - {

      "must contain a telephone number input field" in {
        val input = getElementByTag(doc, "input")
        input.attr("type") mustBe "tel"
        input.attr("autocomplete") mustBe "tel"
      }
    }
  }

}
