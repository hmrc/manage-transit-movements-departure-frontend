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

package views.transport.transportMeans.departure

import forms.transport.transportMeans.departure.IdentificationFormProvider
import models.NormalMode
import models.transport.transportMeans.departure.Identification
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.transport.transportMeans.departure.IdentificationView

class IdentificationViewSpec extends RadioViewBehaviours[Identification] {

  override def form: Form[Identification] = new IdentificationFormProvider()()

  override def applyView(form: Form[Identification]): HtmlFormat.Appendable =
    injector.instanceOf[IdentificationView].apply(form, lrn, Identification.radioItems, NormalMode)(fakeRequest, messages)

  override val prefix: String = "transport.transportMeans.departure.identification"

  override def radioItems(fieldId: String, checkedValue: Option[Identification] = None): Seq[RadioItem] =
    Identification.radioItems(fieldId, checkedValue)

  override def values: Seq[Identification] = Identification.values

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Transport details - Departure means of transport")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
