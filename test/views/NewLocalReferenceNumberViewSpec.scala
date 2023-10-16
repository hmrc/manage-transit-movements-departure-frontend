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

package views

import forms.preTaskList.LocalReferenceNumberFormProvider
import models.LocalReferenceNumber
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.NewLocalReferenceNumberView

class NewLocalReferenceNumberViewSpec extends InputTextViewBehaviours[LocalReferenceNumber] {

  override def form: Form[LocalReferenceNumber] = new LocalReferenceNumberFormProvider().apply(prefix)

  override def applyView(form: Form[LocalReferenceNumber]): HtmlFormat.Appendable =
    injector.instanceOf[NewLocalReferenceNumberView].apply(form, LocalReferenceNumber("ABC123").value)(fakeRequest, messages)

  override val prefix: String = "newLocalReferenceNumber"

  implicit override val arbitraryT: Arbitrary[LocalReferenceNumber] = arbitraryLocalReferenceNumber

  override val urlContainsLrn: Boolean = false

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithContent(
    "p",
    "You cannot use the same Local Reference Number (LRN) more than once. To amend this declaration, you need to enter a new one."
  )

  behave like pageWithHint("This can be up to 22 characters long and include letters, numbers, hyphens and underscores. For example, ABCD123456EF-789.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
