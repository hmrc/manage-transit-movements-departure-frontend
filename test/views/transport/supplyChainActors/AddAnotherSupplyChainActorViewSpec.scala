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

package views.transport.supplyChainActors

import config.FrontendAppConfig
import forms.AddAnotherFormProvider
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.transport.supplyChainActors.AddAnotherSupplyChainActorViewModel
import views.behaviours.ListWithActionsViewBehaviours
import views.html.transport.supplyChainActors.AddAnotherSupplyChainActorView

class AddAnotherSupplyChainActorViewSpec extends ListWithActionsViewBehaviours {

  implicit override def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  override def maxNumber: Int = frontendAppConfig.maxSupplyChainActors

  private def formProvider(viewModel: AddAnotherSupplyChainActorViewModel) =
    new AddAnotherFormProvider()(viewModel.prefix, viewModel.allowMore)

  private val viewModel                     = arbitrary[AddAnotherSupplyChainActorViewModel].sample.value
  private val viewModelWithItemsNotMaxedOut = viewModel.copy(listItems = listItems)
  private val viewModelWithItemsMaxedOut    = viewModel.copy(listItems = maxedOutListItems)

  override def form: Form[Boolean] = formProvider(viewModelWithItemsNotMaxedOut)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherSupplyChainActorView]
      .apply(form, lrn, viewModelWithItemsNotMaxedOut)(fakeRequest, messages, frontendAppConfig)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherSupplyChainActorView]
      .apply(formProvider(viewModelWithItemsMaxedOut), lrn, viewModelWithItemsMaxedOut)(fakeRequest, messages, frontendAppConfig)

  override val prefix: String = "transport.supplyChainActors.addAnotherSupplyChainActor"

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Transport details - Supply chain actor")

  behave like pageWithMoreItemsAllowed(viewModelWithItemsNotMaxedOut.count)()

  behave like pageWithItemsMaxedOut(viewModelWithItemsMaxedOut.count)

  behave like pageWithSubmitButton("Save and continue")
}
