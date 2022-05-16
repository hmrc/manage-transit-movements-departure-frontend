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

import forms.OfficeOfDepartureFormProvider
import generators.Generators
import models.reference.{CountryCode, CustomsOffice}
import models.{CustomsOfficeList, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.preTaskList.OfficeOfDepartureView

class OfficeOfDepartureViewSpec extends InputSelectViewBehaviours[CustomsOffice] with Generators {

  override def form: Form[CustomsOffice] = new OfficeOfDepartureFormProvider()(CustomsOfficeList(Nil))

  override def applyView(form: Form[CustomsOffice]): HtmlFormat.Appendable =
    injector.instanceOf[OfficeOfDepartureView].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "officeOfDeparture"

  override val urlContainsLrn = true

  override val hasPlaceholder = false

  override def values: Seq[CustomsOffice] = Seq(
    CustomsOffice("id1", "name1", CountryCode("GB"), None),
    CustomsOffice("id2", "name2", CountryCode("XI"), None),
    CustomsOffice("id3", "name3", CountryCode("AD"), None)
  )

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithSelect

  behave like pageWithHint("Give the office location or code. Like, Dover or GB000060.")

  behave like pageWithContent("p", "This is the Customs office where the transit movement starts.")

  behave like pageWithSubmitButton("Save and continue")
}
