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

package controllers.transport.equipment

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.AddAnotherFormProvider
import models.NormalMode
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import pages.transport.preRequisites.ContainerIndicatorPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.inject.bind
import viewModels.ListItem
import play.api.inject.guice.GuiceApplicationBuilder
import viewModels.transport.equipment.AddAnotherEquipmentViewModel
import viewModels.transport.equipment.AddAnotherEquipmentViewModel.AddAnotherEquipmentViewModelProvider
import views.html.transport.equipment.AddAnotherEquipmentView
import controllers.transport.equipment.index.{routes => indexRoutes}

class AddAnotherEquipmentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with Generators {

  private val formProvider = new AddAnotherFormProvider()

  private def form(viewModel: AddAnotherEquipmentViewModel) =
    formProvider(viewModel.prefix, viewModel.allowMoreEquipments(frontendAppConfig))

  private val mode = NormalMode

  private lazy val addAnotherEquipmentRoute = routes.AddAnotherEquipmentController.onPageLoad(lrn, mode).url

  private val mockViewModelProvider = mock[AddAnotherEquipmentViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AddAnotherEquipmentViewModelProvider]).toInstance(mockViewModelProvider))
  // TODO bind appropriate navigator provider

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  private val listItem          = arbitrary[ListItem].sample.value
  private val listItems         = Seq.fill(Gen.choose(1, frontendAppConfig.maxEquipmentNumbers - 1).sample.value)(listItem)
  private val maxedOutListItems = Seq.fill(frontendAppConfig.maxEquipmentNumbers)(listItem)

  private val viewModel = arbitrary[AddAnotherEquipmentViewModel].sample.value

  private val viewModelWithNoEquipments          = viewModel.copy(listItems = Nil)
  private val viewModelWithEquipmentsNotMaxedOut = viewModel.copy(listItems = listItems)
  private val viewModelWithEquipmentsMaxedOut    = viewModel.copy(listItems = maxedOutListItems)

  "AddAnotherEquipment Controller" - {

    "when 0 equipment" - {
      "redirect to containerIdentification controller" - {

        when(mockViewModelProvider.apply(any(), any())(any()))
          .thenReturn(viewModelWithNoEquipments)

        val userAnswers = emptyUserAnswers
          .setValue(ContainerIndicatorPage, true)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addAnotherEquipmentRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          indexRoutes.ContainerIdentificationNumberController.onPageLoad(lrn, mode, equipmentIndex).url
      }

      "redirect to add equipment yes/no page" - {
        when(mockViewModelProvider.apply(any(), any())(any()))
          .thenReturn(viewModelWithNoEquipments)

        val userAnswers = emptyUserAnswers
          .setValue(ContainerIndicatorPage, false)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addAnotherEquipmentRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.AddTransportEquipmentYesNoController.onPageLoad(lrn, mode).url
      }
    }

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {
        when(mockViewModelProvider.apply(any(), any())(any()))
          .thenReturn(viewModelWithEquipmentsNotMaxedOut)

        val userAnswers = emptyUserAnswers
          .setValue(ContainerIndicatorPage, true)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addAnotherEquipmentRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherEquipmentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(viewModelWithEquipmentsNotMaxedOut), lrn, mode, viewModelWithEquipmentsNotMaxedOut, allowMoreEquipments = true)(request, messages).toString
      }

      "when max limit reached" in {
        when(mockViewModelProvider.apply(any(), any())(any()))
          .thenReturn(viewModelWithEquipmentsMaxedOut)

        val userAnswers = emptyUserAnswers
          .setValue(ContainerIndicatorPage, true)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addAnotherEquipmentRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherEquipmentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(viewModelWithEquipmentsMaxedOut), lrn, mode, viewModelWithEquipmentsMaxedOut, allowMoreEquipments = false)(request, messages).toString
      }
    }

    "when max limit not reached" - {
      "when yes submitted" ignore {
        // TODO Add tests based on different outcome of pages to redirect to
      }

      "when no submitted" - {
        "must redirect to next page" ignore {
          when(mockViewModelProvider.apply(any(), any())(any()))
            .thenReturn(viewModelWithEquipmentsNotMaxedOut)

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherEquipmentRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url // TODO update controller with appropriate navigator
        }
      }
    }

    "when max limit reached" - {
      "must redirect to next page" ignore {
        when(mockViewModelProvider.apply(any(), any())(any()))
          .thenReturn(viewModelWithEquipmentsMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherEquipmentRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {
        when(mockViewModelProvider.apply(any(), any())(any()))
          .thenReturn(viewModelWithEquipmentsNotMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherEquipmentRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(viewModelWithEquipmentsNotMaxedOut).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherEquipmentView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, lrn, mode, viewModelWithEquipmentsNotMaxedOut, allowMoreEquipments = true)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addAnotherEquipmentRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addAnotherEquipmentRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
