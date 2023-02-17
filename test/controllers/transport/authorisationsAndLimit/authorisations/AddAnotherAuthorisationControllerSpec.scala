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

package controllers.transport.authorisationsAndLimit.authorisations

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.transport.authorisationsAndLimit.authorisations.index.{routes => indexRoutes}
import forms.AddAnotherFormProvider
import generators.Generators
import models.NormalMode
import navigation.transport.TransportNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpVerbs.GET
import viewModels.ListItem
import viewModels.transport.authorisationsAndLimit.authorisations.AddAnotherAuthorisationViewModel
import viewModels.transport.authorisationsAndLimit.authorisations.AddAnotherAuthorisationViewModel.AddAnotherAuthorisationViewModelProvider
import views.html.transport.authorisationsAndLimit.authorisations.AddAnotherAuthorisationView

class AddAnotherAuthorisationControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with Generators {

  private val formProvider                      = new AddAnotherFormProvider()
  private val mode                              = NormalMode
  private lazy val addAnotherAuthorisationRoute = routes.AddAnotherAuthorisationController.onPageLoad(lrn, mode).url

  private def form(viewModel: AddAnotherAuthorisationViewModel) =
    formProvider(viewModel.prefix, viewModel.allowMore(frontendAppConfig))

  private val mockViewModelProvider = mock[AddAnotherAuthorisationViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AddAnotherAuthorisationViewModelProvider]).toInstance(mockViewModelProvider))
      .overrides(bind(classOf[TransportNavigatorProvider]).toInstance(fakeTransportNavigatorProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  private val listItem          = arbitrary[ListItem].sample.value
  private val listItems         = Seq.fill(Gen.choose(1, frontendAppConfig.maxAuthorisations - 1).sample.value)(listItem)
  private val maxedOutListItems = Seq.fill(frontendAppConfig.maxAuthorisations)(listItem)

  private val viewModel = arbitrary[AddAnotherAuthorisationViewModel].sample.value

  private val viewModelWithNoItems = viewModel.copy(listItems = Nil)
  private val notMaxedOutViewModel = viewModel.copy(listItems = listItems)
  private val maxedOutViewModel    = viewModel.copy(listItems = maxedOutListItems)

  "AddAnotherSupplyChainActor Controller" - {

    "onPageLoad" - {

      "when no authorisations" - {
        "must redirect to AddAuthorisationYesNoController" in {
          when(mockViewModelProvider.apply(any(), any())(any()))
            .thenReturn(viewModelWithNoItems)

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(GET, addAnotherAuthorisationRoute)

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }

      "when max number of authorisations" - {
        "must return OK and the correct view" in {

          when(mockViewModelProvider.apply(any(), any())(any()))
            .thenReturn(maxedOutViewModel)

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(GET, addAnotherAuthorisationRoute)

          val result = route(app, request).value

          val view = injector.instanceOf[AddAnotherAuthorisationView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(maxedOutViewModel), lrn, maxedOutViewModel)(request, messages, frontendAppConfig).toString
        }
      }

      "when less than max number of authorisations" - {
        "must return OK and the correct view" in {

          when(mockViewModelProvider.apply(any(), any())(any()))
            .thenReturn(notMaxedOutViewModel)

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(GET, addAnotherAuthorisationRoute)

          val result = route(app, request).value

          val view = injector.instanceOf[AddAnotherAuthorisationView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(notMaxedOutViewModel), lrn, notMaxedOutViewModel)(request, messages, frontendAppConfig).toString
        }
      }

      "when errors" - {

        "must redirect to session expired when data is missing" in {

          setNoExistingUserAnswers()

          val request = FakeRequest(GET, addAnotherAuthorisationRoute)

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
        }
      }
    }

    "onSubmit" - {

      "when max number of authorisations" - {
        "must redirect to carrier details" in {

          when(mockViewModelProvider.apply(any(), any())(any()))
            .thenReturn(maxedOutViewModel)

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherAuthorisationRoute)
            .withFormUrlEncodedBody(("value", ""))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url

        }
      }

      "when less than max number of authorisations" - {
        "and user selects Yes" - {
          "must redirect to authorisation type with next index" in {

            when(mockViewModelProvider.apply(any(), any())(any()))
              .thenReturn(notMaxedOutViewModel)

            setExistingUserAnswers(emptyUserAnswers)

            val request = FakeRequest(POST, addAnotherAuthorisationRoute)
              .withFormUrlEncodedBody(("value", "true"))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual
              indexRoutes.AuthorisationTypeController.onPageLoad(lrn, mode, notMaxedOutViewModel.nextIndex).url

          }
        }
        "and user selects No" - {
          "must redirect to carrier details section" in {

            when(mockViewModelProvider.apply(any(), any())(any()))
              .thenReturn(notMaxedOutViewModel)

            setExistingUserAnswers(emptyUserAnswers)

            val request = FakeRequest(POST, addAnotherAuthorisationRoute)
              .withFormUrlEncodedBody(("value", "false"))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual onwardRoute.url

          }
        }
      }

      "when errors" - {

        "must return bad request when invalid data is submitted" in {

          when(mockViewModelProvider.apply(any(), any())(any()))
            .thenReturn(notMaxedOutViewModel)

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherAuthorisationRoute)
            .withFormUrlEncodedBody(("value", ""))

          val boundForm = form(notMaxedOutViewModel).bind(Map("value" -> ""))

          val result = route(app, request).value

          val view = injector.instanceOf[AddAnotherAuthorisationView]

          status(result) mustEqual BAD_REQUEST

          contentAsString(result) mustEqual
            view(boundForm, lrn, notMaxedOutViewModel)(request, messages, frontendAppConfig).toString
        }

        "must redirect to session expired when no data is found" in {

          setNoExistingUserAnswers()

          val request = FakeRequest(POST, addAnotherAuthorisationRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
        }
      }
    }
  }
}
