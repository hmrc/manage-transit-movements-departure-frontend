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

package views.routeDetails.transit

import config.FrontendAppConfig
import forms.AddAnotherFormProvider
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.routeDetails.transit.AddAnotherOfficeOfTransitViewModel
import views.behaviours.ListWithActionsViewBehaviours
import views.html.routeDetails.transit.AddAnotherOfficeOfTransitView

class AddAnotherOfficeOfTransitViewSpec extends ListWithActionsViewBehaviours {

  implicit override def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  override def maxNumber: Int = frontendAppConfig.maxOfficesOfTransit

  private def formProvider(viewModel: AddAnotherOfficeOfTransitViewModel) =
    new AddAnotherFormProvider()(viewModel.prefix, viewModel.allowMore)

  private val viewModel            = arbitrary[AddAnotherOfficeOfTransitViewModel].sample.value
  private val notMaxedOutViewModel = viewModel.copy(listItems = listItems)
  private val maxedOutViewModel    = viewModel.copy(listItems = maxedOutListItems)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherOfficeOfTransitView]
      .apply(form, lrn, notMaxedOutViewModel)(fakeRequest, messages, frontendAppConfig)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherOfficeOfTransitView]
      .apply(formProvider(maxedOutViewModel), lrn, maxedOutViewModel)(fakeRequest, messages, frontendAppConfig)

  override val prefix: String = "routeDetails.transit.addAnotherOfficeOfTransit"

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Route details - Office of transit")

  behave like pageWithMoreItemsAllowed(notMaxedOutViewModel.count)()

  behave like pageWithItemsMaxedOut(maxedOutViewModel.count)

  behave like pageWithSubmitButton("Continue")
}
