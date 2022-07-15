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

package views.guaranteeDetails.guarantee

import forms.guaranteeDetails.GuaranteeTypeFormProvider
import models.NormalMode
import models.guaranteeDetails.GuaranteeType
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.guaranteeDetails.guarantee.GuaranteeTypeView

class GuaranteeTypeViewSpec extends RadioViewBehaviours[GuaranteeType] {

  override def form: Form[GuaranteeType] = new GuaranteeTypeFormProvider()()

  override def applyView(form: Form[GuaranteeType]): HtmlFormat.Appendable =
    injector.instanceOf[GuaranteeTypeView].apply(form, lrn, GuaranteeType.radioItems, NormalMode, index)(fakeRequest, messages)

  override val prefix: String = "guaranteeDetails.guaranteeType"

  override def radioItems(fieldId: String, checkedValue: Option[GuaranteeType] = None): Seq[RadioItem] =
    GuaranteeType.radioItems(fieldId, checkedValue)

  override def values: Seq[GuaranteeType] = GuaranteeType.values

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Guarantee details")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
