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

package views.routeDetails.transit.index

import forms.DateTimeFormProvider
import generators.Generators
import models.{DateTime, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DateTimeInputViewBehaviours
import views.html.routeDetails.transit.index.OfficeOfTransitETAView

import java.time.LocalDate

class OfficeOfTransitETAViewSpec extends DateTimeInputViewBehaviours with Generators {

  private val transitCountry       = arbitraryCountry.arbitrary.sample.get
  private val transitCustomsOffice = arbitraryCustomsOffice.arbitrary.sample.get

  private val localDate  = LocalDate.now()
  private val dateBefore = localDate.minusDays(1)
  private val dateAfter  = localDate.plusDays(1)

  override def form: Form[DateTime] = new DateTimeFormProvider()(prefix, dateBefore, dateAfter)

  override def applyView(form: Form[DateTime]): HtmlFormat.Appendable =
    injector
      .instanceOf[OfficeOfTransitETAView]
      .apply(form, lrn, transitCountry.description, transitCustomsOffice.name, NormalMode, index)(fakeRequest, messages)

  override val prefix: String = "routeDetails.transit.index.officeOfTransitETA"

  behave like pageWithTitle(transitCustomsOffice.name, transitCountry.description)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Route details - Office of transit")

  behave like pageWithHeading(transitCustomsOffice.name, transitCountry.description)

  behave like pageWithDateTimeInput()

  behave like pageWithSubmitButton("Save and continue")
}
