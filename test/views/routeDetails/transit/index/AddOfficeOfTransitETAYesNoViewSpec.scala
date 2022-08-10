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

import generators.Generators
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.routeDetails.transit.index.AddOfficeOfTransitETAYesNoView

class AddOfficeOfTransitETAYesNoViewSpec extends YesNoViewBehaviours with Generators {

  private val officeOfTransit = arbitraryCustomsOffice.arbitrary.sample.get

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[AddOfficeOfTransitETAYesNoView].apply(form, lrn, NormalMode, officeOfTransit, index)(fakeRequest, messages)

  override val prefix: String = "routeDetails.transit.addOfficeOfTransitETAYesNo"

  behave like pageWithTitle(officeOfTransit)

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Route details")

  behave like pageWithHeading(officeOfTransit)

  behave like pageWithRadioItems(args = Seq(officeOfTransit))

  behave like pageWithSubmitButton("Save and continue")
}
