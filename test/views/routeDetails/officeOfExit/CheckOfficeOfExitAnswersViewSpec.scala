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

package views.routeDetails.officeOfExit

import play.twirl.api.HtmlFormat
import viewModels.sections.Section
import views.behaviours.CheckYourAnswersViewBehaviours
import views.html.routeDetails.officeOfExit.CheckOfficeOfExitAnswersView

class CheckOfficeOfExitAnswersViewSpec extends CheckYourAnswersViewBehaviours {

  override def view: HtmlFormat.Appendable = viewWithSections(sections)

  override def viewWithSections(sections: Seq[Section]): HtmlFormat.Appendable =
    injector.instanceOf[CheckOfficeOfExitAnswersView].apply(lrn, index, sections)(fakeRequest, messages)

  override val urlContainsLrn: Boolean = true

  override val prefix: String = "routeDetails.officeOfExit.checkOfficeOfExitAnswers"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()
}
