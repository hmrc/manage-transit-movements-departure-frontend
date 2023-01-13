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

package views.transport.authorisationsAndLimit.limit

import forms.DateFormProvider
import models.NormalMode
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DateInputViewBehaviours
import views.html.transport.authorisationsAndLimit.limit.LimitDateView

import java.time.LocalDate

class LimitDateViewSpec extends DateInputViewBehaviours {

  private val date = arbitrary[LocalDate].sample.value

  private val maxDate = "15 08 2022"

  override def form: Form[LocalDate] = new DateFormProvider()(prefix, date, date)

  override def applyView(form: Form[LocalDate]): HtmlFormat.Appendable =
    injector.instanceOf[LimitDateView].apply(form, lrn, NormalMode, maxDate)(fakeRequest, messages)

  override val prefix: String = "transport.authorisationsAndLimit.limit.limitDate"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Transport details - Authorisations")

  behave like pageWithHeading()

  behave like pageWithContent("p", "This has to be before 15 08 2022.")

  behave like pageWithoutHint()

  behave like pageWithDateInput()

  behave like pageWithSubmitButton("Save and continue")
}
