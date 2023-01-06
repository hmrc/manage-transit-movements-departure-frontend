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

package views.guaranteeDetails.guarantee

import forms.MoneyFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.CurrencyInputViewBehaviours
import views.html.guaranteeDetails.guarantee.LiabilityAmountView

class LiabilityAmountViewSpec extends CurrencyInputViewBehaviours {

  override val prefix: String = "guaranteeDetails.guarantee.liabilityAmount"

  override def form: Form[BigDecimal] = new MoneyFormProvider()(prefix)

  override def applyView(form: Form[BigDecimal]): HtmlFormat.Appendable =
    injector.instanceOf[LiabilityAmountView].apply(form, lrn, NormalMode, index)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[BigDecimal] = Arbitrary(Gen.double.map(BigDecimal(_)))

  override val inputPrefix: Option[String] = Some("£")

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Guarantee details")

  behave like pageWithHeading()

  behave like pageWithHint("This can have up to 2 decimal places, for example 999.99.")

  behave like pageWithCurrencyInput()

  behave like pageWithSubmitButton("Save and continue")
}
