/*
 * Copyright 2024 HM Revenue & Customs
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
import forms.YesNoFormProvider
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import viewModels.components.InputRadioViewModel.{Radio, RadioWithAdditionalHtml}
import views.html.components.InputRadio
import views.html.templates.MainTemplate

class InputRadioSpec extends A11ySpecBase {

  "the 'input radio' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[InputRadio]

    val prefix         = Gen.alphaNumStr.sample.value
    val title          = nonEmptyString.sample.value
    val caption        = Gen.option(nonEmptyString).sample.value
    val hint           = Gen.option(nonEmptyString).sample.value
    val radioItems     = (_: String) => arbitrary[List[RadioItem]].sample.value
    val inline         = arbitrary[Boolean].sample.value
    val additionalHtml = arbitrary[Html].sample.value
    val form           = new YesNoFormProvider()(prefix)

    "pass accessibility checks" when {

      "ordinary inputRadio" in {
        val content = template.apply(title) {
          component.apply(form("value"), Radio(title, caption), hint, radioItems, inline)
        }
        content.toString() must passAccessibilityChecks
      }

      "radio with additionalHtml" in {
        val content = template.apply(title) {
          component.apply(form("value"), RadioWithAdditionalHtml(title, caption, additionalHtml), hint, radioItems, inline)
        }
        content.toString() must passAccessibilityChecks

      }
    }
  }
}
