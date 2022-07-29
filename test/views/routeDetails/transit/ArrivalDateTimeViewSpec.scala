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

package views.routeDetails.transit

import forms.DateTimeFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DateTimeInputViewBehaviours
import views.html.routeDetails.transit.ArrivalDateTimeView

import java.time.LocalDateTime

class ArrivalDateTimeViewSpec extends DateTimeInputViewBehaviours {

  override def form: Form[LocalDateTime] = new DateTimeFormProvider()(prefix)

  override def applyView(form: Form[LocalDateTime]): HtmlFormat.Appendable =
    injector.instanceOf[ArrivalDateTimeView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "routeDetails.transit.arrivalDateTime"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithDateInput

  behave like pageWithSubmitButton("Save and continue")
}
