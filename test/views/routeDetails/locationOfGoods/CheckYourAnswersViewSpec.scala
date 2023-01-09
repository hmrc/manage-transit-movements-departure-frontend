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

package views.routeDetails.locationOfGoods

import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import play.twirl.api.HtmlFormat
import viewModels.sections.Section
import views.behaviours.CheckYourAnswersViewBehaviours
import views.html.routeDetails.locationOfGoods.CheckYourAnswersView

class CheckYourAnswersViewSpec extends CheckYourAnswersViewBehaviours {

  override def view: HtmlFormat.Appendable = viewWithSections(sections)

  private val mode = arbitrary[Mode].sample.value

  override def viewWithSections(sections: Seq[Section]): HtmlFormat.Appendable =
    injector.instanceOf[CheckYourAnswersView].apply(lrn, mode, sections)(fakeRequest, messages)

  override val prefix: String = "routeDetails.locationOfGoods.checkYourAnswers"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Route details - Location of goods")

  behave like pageWithCheckYourAnswers()

  behave like pageWithFormAction(controllers.routeDetails.locationOfGoods.routes.CheckYourAnswersController.onSubmit(lrn, mode).url)

  behave like pageWithHeading()
}
