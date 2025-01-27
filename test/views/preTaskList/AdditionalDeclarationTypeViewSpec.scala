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
import models.reference.AdditionalDeclarationType
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.EnumerableViewBehaviours
import views.html.preTaskList.AdditionalDeclarationTypeView

class AdditionalDeclarationTypeViewSpec extends EnumerableViewBehaviours[AdditionalDeclarationType] {

  override def form: Form[AdditionalDeclarationType] = new EnumerableFormProvider()(prefix, values)

  override def applyView(form: Form[AdditionalDeclarationType]): HtmlFormat.Appendable =
    injector.instanceOf[AdditionalDeclarationTypeView].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "additionalDeclarationType"

  override def radioItems(fieldId: String, checkedValue: Option[AdditionalDeclarationType] = None): Seq[RadioItem] =
    values.toRadioItems(fieldId, checkedValue)

  override def values: Seq[AdditionalDeclarationType] = Seq(
    AdditionalDeclarationType("A", "for a standard customs declaration (under Article 162 of the Code)"),
    AdditionalDeclarationType("D", "For lodging a standard customs declaration (such as referred to under code A) in accordance with Article 171 of the Code.")
  )

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
