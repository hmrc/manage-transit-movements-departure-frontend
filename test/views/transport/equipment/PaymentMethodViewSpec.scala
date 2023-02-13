/*
 * Copyright 2023 HM Revenue & Customs
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

package views.transport.equipment

import forms.EnumerableFormProvider
import models.NormalMode
import models.transport.equipment.PaymentMethod
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.transport.equipment.PaymentMethodView

class PaymentMethodViewSpec extends RadioViewBehaviours[PaymentMethod] {

  override def form: Form[PaymentMethod] = new EnumerableFormProvider()(prefix)

  override def applyView(form: Form[PaymentMethod]): HtmlFormat.Appendable =
    injector.instanceOf[PaymentMethodView].apply(form, lrn, PaymentMethod.radioItems, NormalMode)(fakeRequest, messages)

  override val prefix: String = "transport.equipment.paymentMethod"

  override def radioItems(fieldId: String, checkedValue: Option[PaymentMethod] = None): Seq[RadioItem] =
    PaymentMethod.radioItems(fieldId, checkedValue)

  override def values: Seq[PaymentMethod] = PaymentMethod.values

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Transport details - Transport charges")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
