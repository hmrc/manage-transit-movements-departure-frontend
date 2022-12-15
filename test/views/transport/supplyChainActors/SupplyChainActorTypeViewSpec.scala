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

package views.transport.supplyChainActors

import forms.EnumerableFormProvider
import models.NormalMode
import models.transport.supplyChainActors.SupplyChainActorType
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.transport.supplyChainActors.SupplyChainActorTypeView

class SupplyChainActorTypeViewSpec extends RadioViewBehaviours[SupplyChainActorType] {

  override def form: Form[SupplyChainActorType] = new EnumerableFormProvider()(prefix)

  override def applyView(form: Form[SupplyChainActorType]): HtmlFormat.Appendable =
    injector.instanceOf[SupplyChainActorTypeView].apply(form, lrn, SupplyChainActorType.radioItems, NormalMode)(fakeRequest, messages)

  override val prefix: String = "transport.supplyChainActors.supplyChainActorType"

  override def radioItems(fieldId: String, checkedValue: Option[SupplyChainActorType] = None): Seq[RadioItem] =
    SupplyChainActorType.radioItems(fieldId, checkedValue)

  override def values: Seq[SupplyChainActorType] = SupplyChainActorType.values

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
