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

package views.preTaskList

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.preTaskList.StandardDeclarationView

class StandardDeclarationViewSpec extends ViewBehaviours {

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[StandardDeclarationView].apply(lrn)(fakeRequest, messages)
  override val prefix: String = "preTaskList.standardDeclaration"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithContent("p", "This means you can only make a declaration for goods which have already boarded at a UK port or airport.")

  behave like pageWithContent(
    "p",
    "Pre-lodge declarations will become available later in the year. This will then allow you to send a declaration before the goods have boarded, up to 2 weeks before they are released for transit."
  )

  behave like pageWithSubmitButton("Continue")
}
