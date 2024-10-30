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

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.DraftNoLongerAvailableView

class DraftNoLongerAvailableViewSpec extends ViewBehaviours {

  override def view: HtmlFormat.Appendable = applyView()

  private def applyView(): HtmlFormat.Appendable =
    injector.instanceOf[DraftNoLongerAvailableView].apply()(fakeRequest, messages)

  override val prefix: String = "draftNoLongerAvailable"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithContent(
    "p",
    "You cannot access any drafts you created before the NCTS 5 update on 1 February. These will soon be deleted from your draft declarations."
  )

  behave like pageWithLink(
    "make-new-departure",
    "Make a new departure declaration",
    "/manage-transit-movements/departures"
  )

  behave like pageWithButton("Return to drafts") {
    assertElementContainsHref(_, frontendAppConfig.manageTransitMovementsDraftDeparturesUrl)
  }
}
