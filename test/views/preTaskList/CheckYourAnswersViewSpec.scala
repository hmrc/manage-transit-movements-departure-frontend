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

package views.preTaskList

import controllers.preTaskList.routes
import play.twirl.api.HtmlFormat
import viewModels.sections.Section
import views.behaviours.CheckYourAnswersViewBehaviours
import views.html.preTaskList.CheckYourAnswersView

class CheckYourAnswersViewSpec extends CheckYourAnswersViewBehaviours {

  override val prefix: String = "preTaskList.checkYourAnswers"

  override def view: HtmlFormat.Appendable = viewWithSections(sections)

  override def viewWithSections(sections: Seq[Section]): HtmlFormat.Appendable =
    injector.instanceOf[CheckYourAnswersView].apply(lrn, sections)(fakeRequest, messages)

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithCheckYourAnswers()

  behave like pageWithFormAction(routes.CheckYourAnswersController.onSubmit(lrn).url)

  behave like pageWithContent("h2", "Before you continue")

  behave like pageWithContent("p", "Once you have confirmed these details you will not be able to change them.")

  behave like pageWithSubmitButton("Confirm and continue")

}
