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

package views.preTaskList

import forms.preTaskList.ProcedureTypeFormProvider
import models.{NormalMode, ProcedureType}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.preTaskList.ProcedureTypeView

class ProcedureTypeViewSpec extends RadioViewBehaviours[ProcedureType] {

  override def form: Form[ProcedureType] = new ProcedureTypeFormProvider()()

  override def applyView(form: Form[ProcedureType]): HtmlFormat.Appendable =
    injector.instanceOf[ProcedureTypeView].apply(form, radioItems, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "procedureType"

  override def radioItems(fieldId: String, checkedValue: Option[ProcedureType] = None): Seq[RadioItem] =
    ProcedureType.radioItems(fieldId, checkedValue)

  override def values: Seq[ProcedureType] = ProcedureType.values

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
