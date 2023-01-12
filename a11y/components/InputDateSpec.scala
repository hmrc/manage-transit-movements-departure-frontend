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

package components

import a11ySpecBase.A11ySpecBase
import forms.DateFormProvider
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.twirl.api.Html
import viewModels.components.InputDateViewModel.{DateInputWithAdditionalHtml, OrdinaryDateInput}
import views.html.components.InputDate
import views.html.templates.MainTemplate

import java.time.LocalDate

class InputDateSpec extends A11ySpecBase {

  "the 'input date' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[InputDate]

    val prefix         = Gen.alphaNumStr.sample.value
    val minDate        = arbitrary[LocalDate].sample.value
    val maxDate        = arbitrary[LocalDate].sample.value
    val title          = nonEmptyString.sample.value
    val hint           = Gen.option(nonEmptyString).sample.value
    val caption        = Gen.option(nonEmptyString).sample.value
    val additionalHtml = arbitrary[Html].sample.value
    val form           = new DateFormProvider()(prefix, minDate, maxDate)

    "pass accessibility checks" when {

      "ordinary date input" in {
        val content = template.apply(title) {
          component.apply(form("value"), OrdinaryDateInput(title, caption), hint)
        }
        content.toString() must passAccessibilityChecks
      }

      "date input with additional html" in {
        val content = template.apply(title) {
          component.apply(form("value"), DateInputWithAdditionalHtml(title, caption, additionalHtml), hint)
        }
        content.toString() must passAccessibilityChecks
      }
    }
  }
}
