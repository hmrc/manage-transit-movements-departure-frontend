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
import forms.CustomsOfficeFormProvider
import models.CustomsOfficeList
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import views.html.components.InputSelect
import views.html.templates.MainTemplate

class InputSelectSpec extends A11ySpecBase {

  "the 'input select' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[InputSelect]

    val prefix         = Gen.alphaNumStr.sample.value
    val title          = nonEmptyString.sample.value
    val customsOffices = arbitrary[CustomsOfficeList].sample.value
    val label          = nonEmptyString.sample.value
    val hint           = Gen.option(nonEmptyString).sample.value
    val placeholder    = nonEmptyString.sample.value
    val selectedValue  = Gen.oneOf(None, Some(customsOffices.customsOffices.head)).sample.value
    val selectItems    = customsOffices.customsOffices.toSelectItems(selectedValue)
    val form           = new CustomsOfficeFormProvider()(prefix, customsOffices)
    val preparedForm = selectedValue match {
      case Some(customsOffice) => form.fill(customsOffice)
      case None                => form
    }

    "pass accessibility checks" when {

      "label is heading" in {
        val content = template.apply(title) {
          component.apply(preparedForm("value"), label, placeholder, labelIsHeading = true, hint, selectItems)
        }
        content.toString() must passAccessibilityChecks
      }

      "label isn't heading" in {
        val content = template.apply(title) {
          component.apply(preparedForm("value"), label, placeholder, labelIsHeading = false, hint, selectItems).withHeading(title)
        }
        content.toString() must passAccessibilityChecks
      }
    }
  }
}
