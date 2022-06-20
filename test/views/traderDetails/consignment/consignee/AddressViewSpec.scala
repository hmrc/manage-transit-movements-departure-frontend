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

package views.traderDetails.consignment.consignee

import forms.AddressFormProvider
import generators.Generators
import models.{Address, CountryList, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.AddressViewBehaviours
import views.html.traderDetails.consignment.consignee.AddressView

class AddressViewSpec extends AddressViewBehaviours with Generators {

  private val addressHolderName = Gen.alphaNumStr.sample.value

  private val countryList = arbitrary[CountryList].sample.value

  override def form: Form[Address] = new AddressFormProvider()(prefix, addressHolderName, countryList)

  override def applyView(form: Form[Address]): HtmlFormat.Appendable =
    injector.instanceOf[AddressView].apply(form, lrn, NormalMode, countryList.countries, addressHolderName)(fakeRequest, messages)

  override val prefix: String = "traderDetails.consignment.consignee.address"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Trader details")

  behave like pageWithHeading(addressHolderName)

  behave like pageWithAddressInput()

  behave like pageWithSubmitButton("Save and continue")
}
