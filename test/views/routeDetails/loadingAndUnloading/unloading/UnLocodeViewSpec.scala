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

package views.routeDetails.loadingAndUnloading.unloading

import forms.UnLocodeFormProvider
import generators.Generators
import views.behaviours.InputSelectViewBehaviours
import models.NormalMode
import models.reference.UnLocode
import models.UnLocodeList
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.routeDetails.loadingAndUnloading.unloading.UnLocodeView

class UnLocodeViewSpec extends InputSelectViewBehaviours[UnLocode] with Generators {

  private lazy val unLocode1 = arbitraryUnLocode.arbitrary.sample.get
  private lazy val unLocode2 = arbitraryUnLocode.arbitrary.sample.get
  private lazy val unLocode3 = arbitraryUnLocode.arbitrary.sample.get

  override def values: Seq[UnLocode] =
    Seq(
      unLocode1,
      unLocode2,
      unLocode3
    )

  override def form: Form[UnLocode] = new UnLocodeFormProvider()(prefix, UnLocodeList(values))

  override def applyView(form: Form[UnLocode]): HtmlFormat.Appendable =
    injector.instanceOf[UnLocodeView].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "routeDetails.loadingAndUnloading.unloading.unLocode"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Route details - Place of unloading")

  behave like pageWithHeading()

  behave like pageWithSelect

  behave like pageWithHint("Enter the code, like DEBER or ESMAD.")

  behave like pageWithSubmitButton("Save and continue")
}