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
import forms.preTaskList.ProcedureTypeFormProvider
import generators.Generators
import models.{LocalReferenceNumber, Mode, ProcedureType}
import org.scalacheck.Arbitrary.arbitrary
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.html.preTaskList.ProcedureTypeView

class ProcedureTypeViewSpec extends A11ySpecBase with Generators {

  "the 'procedure type' view" must {
    val view = app.injector.instanceOf[ProcedureTypeView]

    val form                                                      = new ProcedureTypeFormProvider()()
    val radios: (String, Option[ProcedureType]) => Seq[RadioItem] = ProcedureType.radioItems
    val lrn                                                       = arbitrary[LocalReferenceNumber].sample.value
    val mode                                                      = arbitrary[Mode].sample.value

    val content = view(form, radios, lrn, mode)

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks
    }
  }
}
