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

import forms.CountryFormProvider
import generators.Generators
import models.reference.Country
import models.{CountryList, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.routeDetails.transit.index.OfficeOfTransitCountryView

class OfficeOfTransitCountryViewSpec extends InputSelectViewBehaviours[Country] with Generators {

  private lazy val country1 = arbitraryCountry.arbitrary.sample.get
  private lazy val country2 = arbitraryCountry.arbitrary.sample.get
  private lazy val country3 = arbitraryCountry.arbitrary.sample.get

  override def values: Seq[Country] =
    Seq(
      country1,
      country2,
      country3
    )

  override def form: Form[Country] = new CountryFormProvider()(prefix, CountryList(values))

  override def applyView(form: Form[Country]): HtmlFormat.Appendable =
    injector.instanceOf[OfficeOfTransitCountryView].apply(form, lrn, values, NormalMode, index)(fakeRequest, messages)

  override val prefix: String = "routeDetails.transit.index.officeOfTransitCountry"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Route details")

  behave like pageWithHeading()

  behave like pageWithSelect

  behave like pageWithHint("Enter the country, like France or Portugal.")

  behave like pageWithSubmitButton("Save and continue")
}
