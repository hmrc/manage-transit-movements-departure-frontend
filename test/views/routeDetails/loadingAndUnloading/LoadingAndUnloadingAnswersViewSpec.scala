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

package views.routeDetails.loadingAndUnloading

import controllers.routeDetails.loadingAndUnloading.routes
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import play.twirl.api.HtmlFormat
import viewModels.sections.Section
import views.behaviours.CheckYourAnswersViewBehaviours
import views.html.routeDetails.loadingAndUnloading.LoadingAndUnloadingAnswersView

class LoadingAndUnloadingAnswersViewSpec extends CheckYourAnswersViewBehaviours {

  override val prefix: String = "routeDetails.loadingAndUnloading.checkYourAnswers"

  override def view: HtmlFormat.Appendable = viewWithSections(sections)

  private val mode = arbitrary[Mode].sample.value

  override def viewWithSections(sections: Seq[Section]): HtmlFormat.Appendable =
    injector.instanceOf[LoadingAndUnloadingAnswersView].apply(lrn, mode, sections)(fakeRequest, messages)

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Route details - Places of loading and unloading")

  behave like pageWithHeading()

  behave like pageWithCheckYourAnswers()

  behave like pageWithFormAction(routes.LoadingAndUnloadingAnswersController.onSubmit(lrn, mode).url)

  behave like pageWithSubmitButton("Save and continue")
}
