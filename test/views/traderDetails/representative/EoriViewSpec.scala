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

import forms.EoriNumberFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.traderDetails.representative.EoriView

class EoriViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String = "traderDetails.representative.eori"

  override def form: Form[String] = new EoriNumberFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[EoriView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Trader details")

  behave like pageWithHeading()

  behave like pageWithHintWithVisuallyHiddenText(
    expectedVisibleText = "This will start GB or XI followed by 12 or 15 numbers, for example GB123456789000.",
    expectedHiddenText = "This will start GB or X I followed by 12 or 15 numbers, for example GB123456789000."
  )

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
