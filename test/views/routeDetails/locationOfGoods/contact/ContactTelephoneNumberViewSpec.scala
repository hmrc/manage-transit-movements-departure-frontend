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

package views.routeDetails.locationOfGoods.contact

import forms.TelephoneNumberFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.TelephoneNumberViewBehaviours
import views.html.routeDetails.locationOfGoods.contact.ContactTelephoneNumberView

class ContactTelephoneNumberViewSpec extends TelephoneNumberViewBehaviours {

  override val prefix: String = "routeDetails.locationOfGoods.contact.telephoneNumber"

  private val name: String = Gen.alphaNumStr.sample.value

  override def form: Form[String] = new TelephoneNumberFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[ContactTelephoneNumberView].apply(form, lrn, name, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading(name)

  behave like pageWithSectionCaption("Route details")

  behave like pageWithHint("This has to include the country code, like +44 808 157 0192.")

  behave like pageWithTelephoneNumberInput()

  behave like pageWithSubmitButton("Save and continue")
}
