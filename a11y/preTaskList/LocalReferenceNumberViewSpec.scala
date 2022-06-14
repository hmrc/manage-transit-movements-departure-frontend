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

package preTaskList

import a11ySpecBase.A11ySpecBase
import forms.preTaskList.LocalReferenceNumberFormProvider
import views.html.preTaskList.LocalReferenceNumberView

class LocalReferenceNumberViewSpec extends A11ySpecBase {

  "the 'local reference number' view" must {
    val view = app.injector.instanceOf[LocalReferenceNumberView]

    val form = new LocalReferenceNumberFormProvider()()

    val content = view(form)

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks
    }
  }
}
