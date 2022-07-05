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

package views.guaranteeDetails

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.guaranteeDetails.GuaranteeAddedTIRView

class GuaranteeAddedTIRViewSpec extends ViewBehaviours {

  override val urlContainsLrn: Boolean = true

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[GuaranteeAddedTIRView].apply(lrn)(fakeRequest, messages)

  override val prefix: String = "guaranteeDetails.guaranteeAddedTIR"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Guarantee details")

  behave like pageWithHeading()

  behave like pageWithContent("p", "We’ve added the following guarantee to your declaration:")
  behave like pageWithInsetText("(B) Guarantee for goods dispatched under TIR procedure")
  behave like pageWithContent("p", "This guarantee is required for all TIR declarations (goods moving under the cover of TIR carnet).")
  behave like pageWithContent("p", "You don’t need to do anything here – this is just for your reference.")

  behave like pageWithSubmitButton("Continue")

}
