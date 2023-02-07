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

package views.preTaskList

import forms.CustomsOfficeFormProvider
import models.reference.CustomsOffice
import models.{CustomsOfficeList, NormalMode}
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.preTaskList.OfficeOfDepartureView

class OfficeOfDepartureViewSpec extends InputSelectViewBehaviours[CustomsOffice] {

  override def form: Form[CustomsOffice] = new CustomsOfficeFormProvider()(prefix, CustomsOfficeList(Nil))

  override def applyView(form: Form[CustomsOffice]): HtmlFormat.Appendable =
    injector.instanceOf[OfficeOfDepartureView].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[CustomsOffice] = arbitraryCustomsOffice

  override val prefix: String = "officeOfDeparture"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithHint("Enter the office location or code, like Dover or GB000060.")

  behave like pageWithContent(
    "p",
    "This is the customs office where the transit movement starts. Or if you’re using a simplified procedure, this will be your supervising office."
  )

  behave like pageWithSubmitButton("Continue")
}
