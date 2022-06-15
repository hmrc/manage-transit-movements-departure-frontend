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
import forms.YesNoFormProvider
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Content
import views.html.components.InputYesNo
import views.html.templates.MainTemplate

class InputYesNoSpec extends A11ySpecBase {

  "the 'input yes/no' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[InputYesNo]

    val prefix          = Gen.alphaNumStr.sample.value
    val title           = nonEmptyString.sample.value
    val caption         = Gen.option(nonEmptyString).sample.value
    val hint            = Gen.option(arbitrary[Content]).sample.value
    val legendIsVisible = arbitrary[Boolean].sample.value
    val legendParams    = listWithMaxLength[String]().sample.value
    val form            = new YesNoFormProvider()(prefix)

    "pass accessibility checks" when {

      "label is heading" in {
        val content = template.apply(title) {
          component.apply(form("value"), prefix, caption, hint, legendIsHeading = true, legendIsVisible, legendParams)
        }
        content.toString() must passAccessibilityChecks
      }

      "label isn't heading" in {
        val content = template.apply(title) {
          component.apply(form("value"), prefix, caption, hint, legendIsHeading = false, legendIsVisible, legendParams).withHeading(title)
        }
        content.toString() must passAccessibilityChecks
      }
    }
  }
}
