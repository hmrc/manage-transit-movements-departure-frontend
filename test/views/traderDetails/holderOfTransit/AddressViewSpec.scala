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

package views.traderDetails.holderOfTransit

import forms.AddressFormProvider
import models.{Address, NormalMode}
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.IndividualAddressViewBehaviours
import views.html.traderDetails.holderOfTransit.AddressView

class AddressViewSpec extends IndividualAddressViewBehaviours {

  private val addressHolderName = Gen.alphaNumStr.sample.value

  override def form: Form[Address] = new AddressFormProvider()(prefix, addressHolderName)

  override def applyView(form: Form[Address]): HtmlFormat.Appendable =
    injector.instanceOf[AddressView].apply(form, lrn, NormalMode, addressHolderName)(fakeRequest, messages)

  override val prefix: String = "traderDetails.holderOfTransit.address"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Trader details")

  behave like pageWithHeading(addressHolderName)

  behave like pageWithAddressInput()

  behave like pageWithSubmitButton("Continue")
}
