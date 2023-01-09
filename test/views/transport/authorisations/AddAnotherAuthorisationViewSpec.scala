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

package views.transport.authorisations

import config.FrontendAppConfig
import forms.AddAnotherFormProvider
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.transport.authorisations.AddAnotherAuthorisationViewModel
import views.behaviours.ListWithActionsViewBehaviours
import views.html.transport.authorisations.AddAnotherAuthorisationView

class AddAnotherAuthorisationViewSpec extends ListWithActionsViewBehaviours {

  implicit override def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  override def maxNumber: Int = frontendAppConfig.maxAuthorisations

  private def formProvider(viewModel: AddAnotherAuthorisationViewModel) =
    new AddAnotherFormProvider()(viewModel.prefix, viewModel.allowMoreAuthorisations)

  private val viewModel                     = arbitrary[AddAnotherAuthorisationViewModel].sample.value
  private val viewModelWithItemsNotMaxedOut = viewModel.copy(listItems = listItems)
  private val viewModelWithItemsMaxedOut    = viewModel.copy(listItems = maxedOutListItems)

  override def form: Form[Boolean] = formProvider(viewModelWithItemsNotMaxedOut)

  private val mode = arbitrary[Mode].sample.value

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherAuthorisationView]
      .apply(form, lrn, mode, viewModelWithItemsNotMaxedOut, true)(fakeRequest, messages)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherAuthorisationView]
      .apply(formProvider(viewModelWithItemsMaxedOut), lrn, mode, viewModelWithItemsMaxedOut, false)(fakeRequest, messages)

  override val prefix: String = "transport.authorisations.addAnotherAuthorisation"

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Transport details - Authorisations")

  behave like pageWithMoreItemsAllowed(viewModelWithItemsNotMaxedOut.authorisations)()

  behave like pageWithItemsMaxedOut(viewModelWithItemsMaxedOut.authorisations)

  behave like pageWithSubmitButton("Save and continue")
}
