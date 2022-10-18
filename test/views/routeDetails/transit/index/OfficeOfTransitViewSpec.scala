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

import forms.CustomsOfficeForCountryFormProvider
import models.reference.CustomsOffice
import models.{CustomsOfficeList, NormalMode}
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.routeDetails.transit.index.OfficeOfTransitView

class OfficeOfTransitViewSpec extends InputSelectViewBehaviours[CustomsOffice] {

  private lazy val countryName = arbitraryCountry.arbitrary.sample.value.description

  override def form: Form[CustomsOffice] = new CustomsOfficeForCountryFormProvider()(prefix, CustomsOfficeList(values), countryName)

  override def applyView(form: Form[CustomsOffice]): HtmlFormat.Appendable =
    injector.instanceOf[OfficeOfTransitView].apply(form, lrn, values, countryName, NormalMode, index)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[CustomsOffice] = arbitraryCustomsOffice

  override val prefix: String = "routeDetails.transit.index.officeOfTransit"

  behave like pageWithTitle(countryName)

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Route details")

  behave like pageWithHeading(countryName)

  behave like pageWithSelect

  behave like pageWithHint("Enter the office location or code, like Calais or FR620001.")

  behave like pageWithSubmitButton("Save and continue")
}
