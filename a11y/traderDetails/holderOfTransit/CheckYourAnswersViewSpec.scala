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

package traderDetails.holderOfTransit

import a11ySpecBase.A11ySpecBase
import generators.Generators
import models.LocalReferenceNumber
import org.scalacheck.Arbitrary.arbitrary
import viewModels.sections.Section
import views.html.traderDetails.holderOfTransit.CheckYourAnswersView

class CheckYourAnswersViewSpec extends A11ySpecBase with Generators {

  "the 'check your answers' view" must {
    val view = app.injector.instanceOf[CheckYourAnswersView]

    val lrn      = arbitrary[LocalReferenceNumber].sample.value
    val sections = listWithMaxLength[Section]().sample.value

    val content = view(lrn, sections)

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks
    }
  }
}
