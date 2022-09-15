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

package views.routeDetails.locationOfGoods

import forms.locationOfGoods.LocationTypeFormProvider
import models.{LocationType, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.routeDetails.locationOfGoods.LocationTypeView

class LocationTypeViewSpec extends RadioViewBehaviours[LocationType] {

  override def form: Form[LocationType] = new LocationTypeFormProvider()()

  override def applyView(form: Form[LocationType]): HtmlFormat.Appendable =
    injector.instanceOf[LocationTypeView].apply(form, lrn, LocationType.radioItems, NormalMode)(fakeRequest, messages)

  override val prefix: String = "routeDetails.locationOfGoods.locationType"

  override def radioItems(fieldId: String, checkedValue: Option[LocationType] = None): Seq[RadioItem] =
    LocationType.radioItems(fieldId, checkedValue)

  override def values: Seq[LocationType] = LocationType.values

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Route details")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}