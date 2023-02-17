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

import viewModels.InputSize

trait CurrencyInputViewBehaviours extends InputTextViewBehaviours[BigDecimal] {

  val currencySymbol: String = nonEmptyString.sample.value

  def pageWithCurrencyInput(): Unit = {

    behave like pageWithInputText(Some(InputSize.Width10))

    "page with a currency input field" - {
      "must have correct pattern" in {
        assert(getElementById(doc, "value").attr("pattern") == "[0-9]*")
      }

      "must have correct input mode" in {
        assert(getElementById(doc, "value").attr("inputmode") == "numeric")
      }

      "must have correct prefix" in {
        assert(getElementByClass(doc, "govuk-input__prefix").text() == currencySymbol)
      }
    }
  }

}
