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

package views.traderDetails.representative

import forms.TelephoneNumberFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.TelephoneNumberViewBehaviours
import views.html.traderDetails.representative.TelephoneNumberView

class TelephoneNumberViewSpec extends TelephoneNumberViewBehaviours {

  override val prefix: String = "traderDetails.representative.telephoneNumber"

  override def form: Form[String] = new TelephoneNumberFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[TelephoneNumberView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Trader details - Representative")

  behave like pageWithHeading()

  behave like pageWithTelephoneNumberInput()

  behave like pageWithSubmitButton("Save and continue")
}
