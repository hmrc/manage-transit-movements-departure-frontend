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

package views.transport.equipment.index.seal

import forms.ContainerSealIdentificationNumberFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.transport.equipment.index.seal.ContainerSealIdentificationNumberView

class ContainerSealIdentificationNumberViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String     = "transport.equipment.index.seal.containerSealIdentificationNumber.withContainer"
  private val containerNumber     = nonEmptyString.sample.value
  override def form: Form[String] = new ContainerSealIdentificationNumberFormProvider()(prefix, Nil, containerNumber)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector
      .instanceOf[ContainerSealIdentificationNumberView]
      .apply(form, lrn, NormalMode, equipmentIndex, sealIndex, prefix, containerNumber)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle(args = containerNumber)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Transport details - Equipment")

  behave like pageWithHeading(args = containerNumber)

  behave like pageWithHint("This can be up to 20 characters long and include both letters and numbers.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")

  "when not using a container" - {
    val prefix: String = "transport.equipment.index.seal.containerSealIdentificationNumber.withoutContainer"

    def form: Form[String] = new ContainerSealIdentificationNumberFormProvider()(prefix, Nil)

    val view: HtmlFormat.Appendable =
      injector
        .instanceOf[ContainerSealIdentificationNumberView]
        .apply(form, lrn, NormalMode, equipmentIndex, sealIndex, prefix)(fakeRequest, messages)

    val doc = parseView(view)

    behave like pageWithTitle(doc, prefix)

    behave like pageWithHeading(doc, prefix)
  }
}
