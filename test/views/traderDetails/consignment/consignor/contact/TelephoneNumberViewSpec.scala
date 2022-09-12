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

package views.traderDetails.consignment.consignor.contact

import forms.TelephoneNumberWithInternationalCodeFormProvider
import models.NormalMode
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.TelephoneNumberViewBehaviours
import views.html.traderDetails.consignment.consignor.contact.TelephoneNumberView

class TelephoneNumberViewSpec extends TelephoneNumberViewBehaviours {

  private val name: String    = Gen.alphaNumStr.sample.value
  override val prefix: String = "traderDetails.consignment.consignor.contact.telephoneNumber"

  override def form: Form[String] = new TelephoneNumberWithInternationalCodeFormProvider()(prefix, name)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[TelephoneNumberView].apply(form, lrn, NormalMode, name)(fakeRequest, messages)

  behave like pageWithTitle(name)

  behave like pageWithBackLink

  behave like pageWithHeading(name)

  behave like pageWithSectionCaption("Trader details")

  behave like pageWithTelephoneNumberInput()

  behave like pageWithSubmitButton("Save and continue")
}
