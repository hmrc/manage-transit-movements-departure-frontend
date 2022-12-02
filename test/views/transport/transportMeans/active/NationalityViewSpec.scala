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

package views.transport.transportMeans.active

import forms.NationalityFormProvider
import views.behaviours.InputSelectViewBehaviours
import models.NormalMode
import models.reference.Nationality
import models.NationalityList
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.transport.transportMeans.active.NationalityView

class NationalityViewSpec extends InputSelectViewBehaviours[Nationality] {

  override def form: Form[Nationality] = new NationalityFormProvider()(prefix, NationalityList(values))

  override def applyView(form: Form[Nationality]): HtmlFormat.Appendable =
    injector.instanceOf[NationalityView].apply(form, lrn, values, NormalMode, index)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[Nationality] = arbitraryNationality

  override val prefix: String = "transport.transportMeans.active.nationality"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Transport details - Border means of transport")

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithHint("Enter the country or code, like AT or Austria.")

  behave like pageWithSubmitButton("Save and continue")
}
