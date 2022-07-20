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

package views.routeDetails.routing

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.routeDetails.routing.BindingItineraryView

class BindingItineraryViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[BindingItineraryView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "routeDetails.bindingItinerary"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Route details")

  behave like pageWithHeading()

  behave like pageWithContent("p", "This means transporting goods to the office of departure by an economically justifiable itinerary.")

  behave like pageWithContent("p", "This diverts the route around countries that represent a higher risk to the goods in transit.")

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
