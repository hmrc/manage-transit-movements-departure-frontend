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

package views.guaranteeDetails

import forms.AddAnotherFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ListWithActionsViewBehaviours
import views.html.guaranteeDetails.AddAnotherGuaranteeView

class AddAnotherGuaranteeViewSpec extends ListWithActionsViewBehaviours {

  override def maxNumber: Int = frontendAppConfig.maxGuarantees

  private def formProvider = new AddAnotherFormProvider()

  override def form: Form[Boolean] = formProvider(prefix, allowMore = true)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherGuaranteeView]
      .apply(form, lrn, listItems, allowMoreGuarantees = true)(fakeRequest, messages)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherGuaranteeView]
      .apply(formProvider(prefix, allowMore = false), lrn, maxedOutListItems, allowMoreGuarantees = false)(fakeRequest, messages)

  override val prefix: String = "guaranteeDetails.addAnotherGuarantee"

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Guarantee details")

  behave like pageWithMoreItemsAllowed()

  behave like pageWithItemsMaxedOut()

  behave like pageWithSubmitButton("Save and continue")
}
