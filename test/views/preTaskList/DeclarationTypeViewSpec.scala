/*
 * Copyright 2024 HM Revenue & Customs
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

import forms.EnumerableFormProvider
import models.NormalMode
import models.reference.DeclarationType
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.EnumerableViewBehaviours
import views.html.preTaskList.DeclarationTypeView

class DeclarationTypeViewSpec extends EnumerableViewBehaviours[DeclarationType] {

  override def form: Form[DeclarationType] = new EnumerableFormProvider()(prefix, values)

  override def applyView(form: Form[DeclarationType]): HtmlFormat.Appendable =
    injector.instanceOf[DeclarationTypeView].apply(form, values, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "declarationType"

  override def radioItems(fieldId: String, checkedValue: Option[DeclarationType] = None): Seq[RadioItem] =
    values.toRadioItems(fieldId, checkedValue)

  override def values: Seq[DeclarationType] = Seq(
    DeclarationType("T2", "Goods having the customs status of Union goods, which are placed under the common transit procedure"),
    DeclarationType("TIR", "TIR carnet")
  )

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
