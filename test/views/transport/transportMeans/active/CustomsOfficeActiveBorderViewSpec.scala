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

package views.transport.transportMeans.active

import forms.CustomsOfficeFormProvider
import views.behaviours.InputSelectViewBehaviours
import models.NormalMode
import models.reference.CustomsOffice
import models.CustomsOfficeList
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.transport.transportMeans.active.CustomsOfficeActiveBorderView

class CustomsOfficeActiveBorderViewSpec extends InputSelectViewBehaviours[CustomsOffice] {

  override def form: Form[CustomsOffice] = new CustomsOfficeFormProvider()(prefix, CustomsOfficeList(values))

  override def applyView(form: Form[CustomsOffice]): HtmlFormat.Appendable =
    injector.instanceOf[CustomsOfficeActiveBorderView].apply(form, lrn, CustomsOfficeList(values), NormalMode, activeIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[CustomsOffice] = arbitraryCustomsOffice

  override val prefix: String = "transport.transportMeans.active.customsOfficeActiveBorder"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Transport details - Border means of transport")

  behave like pageWithContent("p", "This is the customs office after crossing the border.")

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithHint("Enter the office location or code, like Calais or FR620001.")

  behave like pageWithSubmitButton("Save and continue")
}
