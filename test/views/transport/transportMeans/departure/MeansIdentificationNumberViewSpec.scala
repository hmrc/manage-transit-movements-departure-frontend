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

package views.transport.transportMeans.departure

import forms.MeansIdentificationNumberProvider
import models.NormalMode
import models.transport.transportMeans.departure.Identification
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.transport.transportMeans.departure.MeansIdentificationNumberView

class MeansIdentificationNumberViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String = "transport.transportMeans.departure.meansIdentificationNumber"

  private val identificationType = arbitrary[Identification].sample.value

  override def form: Form[String] = new MeansIdentificationNumberProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[MeansIdentificationNumberView].apply(form, lrn, NormalMode, identificationType)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle(identificationType.arg)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Transport details - Departure means of transport")

  behave like pageWithHeading(identificationType.arg)

  behave like pageWithHint(
    "This can be up to 35 characters long and include both letters and numbers."
  )

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
