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

package views.transport.authorisations.index

import base.SpecBase
import forms.AuthorisationReferenceNumberFormProvider
import generators.Generators
import models.NormalMode
import models.transport.authorisations.AuthorisationType
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.transport.authorisations.index.AuthorisationReferenceNumberView

class AuthorisationReferenceNumberViewSpec extends InputTextViewBehaviours[String] with Generators with SpecBase {

  override val prefix: String = "transport.authorisations.authorisationReferenceNumber"

  private val authorisationType = Arbitrary.arbitrary[AuthorisationType].sample.value

  private val dynamicText = s"$prefix.${authorisationType.toString}"

  override def form: Form[String] = new AuthorisationReferenceNumberFormProvider()(prefix, dynamicText)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[AuthorisationReferenceNumberView].apply(form, lrn, dynamicText, NormalMode, authorisationIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle(messages(dynamicText))

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Transport details - Authorisations")

  behave like pageWithHeading(messages(dynamicText))

  behave like pageWithHint("This can be up to 35 characters long and include both letters and numbers.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
