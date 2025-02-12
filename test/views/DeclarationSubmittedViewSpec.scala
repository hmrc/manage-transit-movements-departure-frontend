/*
 * Copyright 2024 HM Revenue & Customs
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

package views

import generators.Generators
import models.reference.CustomsOffice
import org.scalacheck.Arbitrary.arbitrary
import play.twirl.api.HtmlFormat
import views.behaviours.PanelViewBehaviours
import views.html.DeclarationSubmittedView

class DeclarationSubmittedViewSpec extends PanelViewBehaviours with Generators {

  private val officeOfDestination = arbitrary[CustomsOffice].sample.value

  override def view: HtmlFormat.Appendable = applyView(officeOfDestination)

  private def applyView(officeOfDestination: CustomsOffice): HtmlFormat.Appendable =
    injector.instanceOf[DeclarationSubmittedView].apply(lrn, officeOfDestination)(fakeRequest, messages)

  override val prefix: String = "declarationSubmitted"

  override val urlContainsLrn: Boolean = true

  behave like pageWithTitle()

  behave like pageWithoutBackLink()

  behave like pageWithHeading()

  behave like pageWithPanel(s"for Local Reference Number (LRN) $lrn")

  behave like pageWithLink(
    "departure-declarations",
    "Check the status of departure declarations",
    "http://localhost:9485/manage-transit-movements/view-departure-declarations"
  )

  behave like pageWithPartialContent("p", " to find out when goods have been released.")

  "when office of destination has a telephone number" - {
    val officeOfDestination = CustomsOffice("GB123", "Office 123", Some("+123"), "GB")
    val view                = applyView(officeOfDestination)
    val doc                 = parseView(view)

    behave like pageWithContent(
      doc,
      "p",
      "If the goods are not released when expected or you have another problem, contact Customs at Office 123 on +123."
    )
  }

  "when office of destination does not have a telephone number" - {
    val officeOfDestination = CustomsOffice("GB123", "Office 123", None, "GB")
    val view                = applyView(officeOfDestination)
    val doc                 = parseView(view)

    behave like pageWithContent(
      doc,
      "p",
      "If the goods are not released when expected or you have another problem, contact Customs at Office 123."
    )
  }

  behave like pageWithLink(
    "new-departure",
    "Make another departure declaration",
    "/manage-transit-movements/departures"
  )
}
