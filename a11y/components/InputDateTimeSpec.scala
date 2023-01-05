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
import forms.DateTimeFormProvider
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import views.html.components.InputDateTime
import views.html.templates.MainTemplate

import java.time.LocalDate

class InputDateTimeSpec extends A11ySpecBase {

  "the 'input date time' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[InputDateTime]

    val prefix    = Gen.alphaNumStr.sample.value
    val minDate   = arbitrary[LocalDate].sample.value
    val maxDate   = arbitrary[LocalDate].sample.value
    val title     = nonEmptyString.sample.value
    val caption   = Gen.option(nonEmptyString).sample.value
    val legend    = nonEmptyString.sample.value
    val dateLabel = nonEmptyString.sample.value
    val dateHint  = Gen.option(nonEmptyString).sample.value
    val timeLabel = nonEmptyString.sample.value
    val timeHint  = Gen.option(nonEmptyString).sample.value
    val form      = new DateTimeFormProvider()(prefix, minDate, maxDate)

    val content = template.apply(title) {
      component.apply(caption, legend, form, dateLabel, dateHint, timeLabel, timeHint)
    }

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks
    }
  }
}
