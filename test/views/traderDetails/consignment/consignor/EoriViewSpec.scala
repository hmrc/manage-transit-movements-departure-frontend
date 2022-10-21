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

package views.traderDetails.consignment.consignor

import forms.EoriNumberFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.traderDetails.consignment.consignor.EoriView

class EoriViewSpec extends InputTextViewBehaviours[String] {

  override def form: Form[String] = new EoriNumberFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[EoriView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "traderDetails.consignment.consignor.eori"

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaNumStr)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Trader details - Consignor")

  behave like pageWithHeading()

  behave like pageWithHint(
    "This can be up to 17 characters long and include both letters and numbers. For example, GB123456789000."
  )

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
