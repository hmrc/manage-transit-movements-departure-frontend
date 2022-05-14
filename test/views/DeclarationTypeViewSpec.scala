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

package views

import forms.DeclarationTypeFormProvider
import models.{DeclarationType, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.DeclarationTypeView

class DeclarationTypeViewSpec extends RadioViewBehaviours[DeclarationType] {

  override def form: Form[DeclarationType] = new DeclarationTypeFormProvider()()

  override def applyView(form: Form[DeclarationType]): HtmlFormat.Appendable =
    injector.instanceOf[DeclarationTypeView].apply(form, radioItems, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "declarationType"

  private val userAnswers = emptyUserAnswers

  override def radioItems(fieldId: String, checkedValue: Option[DeclarationType] = None): Seq[RadioItem] =
    DeclarationType.radioItemsU(userAnswers)(fieldId, checkedValue)

  override def values: Seq[DeclarationType] = DeclarationType.valuesU(userAnswers)

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
