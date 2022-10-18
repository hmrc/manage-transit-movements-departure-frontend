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

package components

import a11ySpecBase.A11ySpecBase
import forms.DynamicAddressFormProvider
import org.scalacheck.Gen
import views.html.components.InputDynamicAddress
import views.html.templates.MainTemplate

class InputDynamicAddressSpec extends A11ySpecBase {

  "the 'input dynamic address' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[InputDynamicAddress]

    val prefix      = Gen.alphaNumStr.sample.value
    val name        = Gen.alphaNumStr.sample.value
    val title       = nonEmptyString.sample.value
    val caption     = Gen.option(nonEmptyString).sample.value
    val headingArgs = listWithMaxLength[Any]().sample.value

    "pass accessibility checks" when {
      "postal code is required" in {
        val isPostalCodeRequired = true
        val form                 = DynamicAddressFormProvider(prefix, isPostalCodeRequired, name)

        val content = template.apply(title) {
          component.apply(form, prefix, caption, isPostalCodeRequired, headingArgs)
        }

        content.toString() must passAccessibilityChecks
      }

      "postal code is not required" in {
        val isPostalCodeRequired = false
        val form                 = DynamicAddressFormProvider(prefix, isPostalCodeRequired, name)

        val content = template.apply(title) {
          component.apply(form, prefix, caption, isPostalCodeRequired, headingArgs)
        }

        content.toString() must passAccessibilityChecks
      }
    }
  }
}
