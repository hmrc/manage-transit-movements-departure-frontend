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

package views.routeDetails.loading

import forms.Constants.locationOfGoodsMaxLength
import forms.PlaceOfLoadingLocationFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.routeDetails.loading.PlaceOfLoadingLocationView
import org.scalacheck.{Arbitrary, Gen}

class PlaceOfLoadingLocationViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String = "routeDetails.loading.placeOfLoadingLocation"

  private val countryName = Gen.alphaNumStr.sample.value.take(locationOfGoodsMaxLength)

  override def form: Form[String] = new PlaceOfLoadingLocationFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[PlaceOfLoadingLocationView].apply(form, lrn, countryName, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle(countryName)

  behave like pageWithBackLink

  behave like pageWithHeading(countryName)

  behave like pageWithSectionCaption("Route details")

  behave like pageWithHint("Describe the specific location of loading. This can be up to 35 characters long.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
