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
import forms.locationOfGoods.LocationOfGoodsCoordinatesFormProvider
import models.Coordinates
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import views.html.components.InputCoordinates
import views.html.templates.MainTemplate

class InputCoordinatesSpec extends A11ySpecBase {

  "the 'input coordinates' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[InputCoordinates]

    val prefix      = Gen.alphaNumStr.sample.value
    val coordinates = arbitrary[Coordinates].sample.value
    val title       = nonEmptyString.sample.value
    val caption     = Gen.option(nonEmptyString).sample.value
    val form        = new LocationOfGoodsCoordinatesFormProvider()(prefix)

    val content = template.apply(title) {
      component.apply(form, prefix, caption, coordinates)
    }

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks
    }
  }
}
