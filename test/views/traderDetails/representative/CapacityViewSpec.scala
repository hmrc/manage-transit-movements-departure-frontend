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

package views.traderDetails.representative

import forms.traderDetails.representative.RepresentativeCapacityFormProvider
import models.NormalMode
import models.traderDetails.representative.RepresentativeCapacity
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.traderDetails.representative.CapacityView

class CapacityViewSpec extends RadioViewBehaviours[RepresentativeCapacity] {

  override def form: Form[RepresentativeCapacity] = new RepresentativeCapacityFormProvider()()

  override def applyView(form: Form[RepresentativeCapacity]): HtmlFormat.Appendable =
    injector.instanceOf[CapacityView].apply(form, lrn, RepresentativeCapacity.radioItems, NormalMode)(fakeRequest, messages)

  override val prefix: String = "traderDetails.representative.capacity"

  override def radioItems(fieldId: String, checkedValue: Option[RepresentativeCapacity] = None): Seq[RadioItem] =
    RepresentativeCapacity.radioItems(fieldId, checkedValue)

  override def values: Seq[RepresentativeCapacity] = RepresentativeCapacity.values

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Trader details")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
