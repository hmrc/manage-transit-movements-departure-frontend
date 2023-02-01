/*
 * Copyright 2023 HM Revenue & Customs
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

import forms.CurrencyCodeFormProvider
import models.{CurrencyCodeList, NormalMode}
import models.reference.CurrencyCode
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.guaranteeDetails.guarantee.CurrencyView

class CurrencyViewSpec extends InputSelectViewBehaviours[CurrencyCode] {

  override def form: Form[CurrencyCode] = new CurrencyCodeFormProvider()(prefix, CurrencyCodeList(values))

  override def applyView(form: Form[CurrencyCode]): HtmlFormat.Appendable =
    injector.instanceOf[CurrencyView].apply(form, lrn, values, NormalMode, index)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[CurrencyCode] = arbitraryCurrencyCode

  override val prefix: String = "guaranteeDetails.guarantee.currency"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithHint("What currency do you want to use for the liability hint")

  behave like pageWithContent("label", "What currency do you want to use for the liability label")

  behave like pageWithSubmitButton("Save and continue")
}
