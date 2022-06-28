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
import views.html.components.HeadingWithSectionCaption
import views.html.templates.MainTemplate

class HeadingWithSectionCaptionSpec extends A11ySpecBase {

  "the 'heading with section' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[HeadingWithSectionCaption]

    val title       = nonEmptyString.sample.value
    val headingKey  = nonEmptyString.sample.value
    val captionKey  = nonEmptyString.sample.value
    val headingArgs = listWithMaxLength[Any]().sample.value

    val content = template.apply(title) {
      component.apply(headingKey, captionKey, headingArgs)
    }

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks
    }
  }
}
