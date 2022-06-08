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

package views.traderDetails.holderOfTransit.contact

import forms.Constants.maxTelephoneNumberLength
import forms.TelephoneNumberFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.traderDetails.holderOfTransit.contact.TelephoneNumberView

class TelephoneNumberViewSpec extends InputTextViewBehaviours[String] {

  private val name: String    = Gen.alphaNumStr.sample.value
  override val prefix: String = "traderDetails.holderOfTransit.contact.telephoneNumber"

  override def form: Form[String] = new TelephoneNumberFormProvider()(prefix, name)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[TelephoneNumberView].apply(form, lrn, NormalMode, name)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(stringsWithMaxLength(maxTelephoneNumberLength, Gen.numChar))

  behave like pageWithTitle(name)

  behave like pageWithBackLink

  behave like pageWithHeading(name)

  behave like pageWithSectionCaption("Trader details")

  behave like pageWithHint("Must include the country code, like +44 808 157 0192.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
