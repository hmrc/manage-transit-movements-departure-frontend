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

package views.routeDetails.locationOfGoods

import forms.locationOfGoods.AuthorisationNumberFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.routeDetails.locationOfGoods.AuthorisationNumberView
import org.scalacheck.{Arbitrary, Gen}

class AuthorisationNumberViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String = "routeDetails.locationOfGoods.authorisationNumber"

  override def form: Form[String] = new AuthorisationNumberFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[AuthorisationNumberView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Route details - Location of goods")

  behave like pageWithHeading()

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithHint("This can be up to 35 characters long and include both letters and numbers.")

  behave like pageWithSubmitButton("Save and continue")
}
