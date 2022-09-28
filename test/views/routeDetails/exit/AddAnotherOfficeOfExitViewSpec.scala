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

package views.routeDetails.exit

import forms.AddAnotherFormProvider
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ListWithActionsViewBehaviours
import views.html.routeDetails.exit.AddAnotherOfficeOfExitView

class AddAnotherOfficeOfExitViewSpec extends ListWithActionsViewBehaviours {

  override def maxNumber: Int = frontendAppConfig.maxOfficesOfExit

  private def formProvider = new AddAnotherFormProvider()

  override def form: Form[Boolean] = formProvider(prefix, allowMore = true)

  private val mode = arbitrary[Mode].sample.value

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherOfficeOfExitView]
      .apply(form, lrn, mode, listItems, allowMoreOfficesOfExit = true)(fakeRequest, messages)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherOfficeOfExitView]
      .apply(formProvider(prefix, allowMore = false), lrn, mode, maxedOutListItems, allowMoreOfficesOfExit = false)(fakeRequest, messages)

  override val prefix: String = "routeDetails.exit.addAnotherOfficeOfExit"

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Route details - Office of exit")

  behave like pageWithHint("You can add up to 9 offices.")

  behave like pageWithMoreItemsAllowed()

  behave like pageWithItemsMaxedOut()

  behave like pageWithSubmitButton("Continue")
}
