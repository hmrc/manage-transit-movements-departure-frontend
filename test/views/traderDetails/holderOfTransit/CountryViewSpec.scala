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

package views.traderDetails.holderOfTransit

import forms.CountryFormProvider
import generators.Generators
import views.behaviours.InputSelectViewBehaviours
import models.NormalMode
import models.reference.Country
import models.CountryList
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.traderDetails.holderOfTransit.CountryView

class CountryViewSpec extends InputSelectViewBehaviours[Country] with Generators {

  private lazy val country1 = arbitraryCountry.arbitrary.sample.get
  private lazy val country2 = arbitraryCountry.arbitrary.sample.get
  private lazy val country3 = arbitraryCountry.arbitrary.sample.get
  private lazy val name     = Gen.alphaNumStr.sample.value

  override def values: Seq[Country] =
    Seq(
      country1,
      country2,
      country3
    )

  override def form: Form[Country] = new CountryFormProvider()(prefix, CountryList(values))

  override def applyView(form: Form[Country]): HtmlFormat.Appendable =
    injector.instanceOf[CountryView].apply(form, lrn, values, NormalMode, name)(fakeRequest, messages)

  override val prefix: String = "traderDetails.holderOfTransit.country"

  behave like pageWithTitle(name)

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Trader details - Transit holder")

  behave like pageWithHeading(name)

  behave like pageWithSelect

  behave like pageWithHint("Enter the country, like Italy or Spain.")

  behave like pageWithSubmitButton("Save and continue")
}
