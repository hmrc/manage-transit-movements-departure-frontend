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

import forms.EnumerableFormProvider
import models.{NormalMode, SecurityDetailsType}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.preTaskList.SecurityDetailsTypeView

class SecurityDetailsTypeViewSpec extends RadioViewBehaviours[SecurityDetailsType] {

  override def form: Form[SecurityDetailsType] = new EnumerableFormProvider()(prefix)

  override def applyView(form: Form[SecurityDetailsType]): HtmlFormat.Appendable =
    injector.instanceOf[SecurityDetailsTypeView].apply(form, radioItems, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "securityDetailsType"

  override def radioItems(fieldId: String, checkedValue: Option[SecurityDetailsType] = None): Seq[RadioItem] =
    SecurityDetailsType.radioItems(fieldId, checkedValue)

  override def values: Seq[SecurityDetailsType] = SecurityDetailsType.values

  override val urlContainsLrn: Boolean = true

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
