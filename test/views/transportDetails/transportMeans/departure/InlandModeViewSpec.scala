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

package views.transportDetails.transportMeans.departure

import forms.transportDetails.transportMeans.departure.InlandModeFormProvider
import models.NormalMode
import models.transportDetails.transportMeans.departure.InlandMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.transportDetails.transportMeans.departure.InlandModeView

class InlandModeViewSpec extends RadioViewBehaviours[InlandMode] {

  override def form: Form[InlandMode] = new InlandModeFormProvider()()

  override def applyView(form: Form[InlandMode]): HtmlFormat.Appendable =
    injector.instanceOf[InlandModeView].apply(form, lrn, InlandMode.radioItems, NormalMode)(fakeRequest, messages)

  override val prefix: String = "transportDetails.transportMeans.departure.inlandMode"

  override def radioItems(fieldId: String, checkedValue: Option[InlandMode] = None): Seq[RadioItem] =
    InlandMode.radioItems(fieldId, checkedValue)

  override def values: Seq[InlandMode] = InlandMode.values

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
