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

package views.transport.equipment.index

import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.transport.equipment.index.AddSealYesNoView

class AddSealYesNoViewSpec extends YesNoViewBehaviours {

  private val containerNumber = Arbitrary(Gen.alphaStr).arbitrary.sample.value

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[AddSealYesNoView].apply(form, lrn, NormalMode, equipmentIndex, containerNumber)(fakeRequest, messages)

  override val prefix: String = "transport.equipment.index.addSealYesNo"

  behave like pageWithTitle(containerNumber)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Transport details - Equipment")

  behave like pageWithHeading(containerNumber)

  behave like pageWithRadioItems(args = Seq(containerNumber))

  behave like pageWithSubmitButton("Save and continue")
}
