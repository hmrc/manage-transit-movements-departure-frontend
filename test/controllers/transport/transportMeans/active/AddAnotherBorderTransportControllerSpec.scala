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

package controllers.transport.transportMeans.active

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.AddAnotherFormProvider
import generators.Generators
import models.{Index, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.ListItem
import viewModels.transport.transportMeans.active.AddAnotherBorderTransportViewModel
import viewModels.transport.transportMeans.active.AddAnotherBorderTransportViewModel.AddAnotherBorderTransportViewModelProvider
import views.html.transport.transportMeans.active.AddAnotherBorderTransportView

class AddAnotherBorderTransportControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val formProvider = new AddAnotherFormProvider()

  private def form(allowMoreActiveBorderTransports: Boolean) =
    formProvider("transport.transportMeans.active.addAnotherBorderTransport", allowMoreActiveBorderTransports)
  private val mode                                = NormalMode
  private lazy val addAnotherBorderTransportRoute = routes.AddAnotherBorderTransportController.onPageLoad(lrn, mode).url

  private val mockViewModelProvider = mock[AddAnotherBorderTransportViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AddAnotherBorderTransportViewModelProvider]).toInstance(mockViewModelProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  private val listItem          = arbitrary[ListItem].sample.value
  private val listItems         = Seq.fill(Gen.choose(1, frontendAppConfig.maxActiveBorderTransports - 1).sample.value)(listItem)
  private val maxedOutListItems = Seq.fill(frontendAppConfig.maxActiveBorderTransports)(listItem)

  "AddAnotherBorderTransport Controller" - {

    "redirect to add another vehicle crossing page" - {
      "when 0 active border transports" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherBorderTransportViewModel(Nil))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherBorderTransportRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.transport.transportMeans.routes.AnotherVehicleCrossingYesNoController.onPageLoad(lrn, mode).url
      }
    }

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {

        val allowMoreActiveBorderTransports = true

        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherBorderTransportViewModel(listItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherBorderTransportRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherBorderTransportView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreActiveBorderTransports), lrn, mode, listItems, allowMoreActiveBorderTransports)(request, messages).toString
      }

      "when max limit reached" in {

        val allowMoreActiveBorderTransports = false

        val listItems = maxedOutListItems

        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherBorderTransportViewModel(listItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherBorderTransportRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherBorderTransportView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreActiveBorderTransports), lrn, mode, listItems, allowMoreActiveBorderTransports)(request, messages).toString
      }
    }

    "when max limit not reached" - {
      "when yes submitted" - {
        "must redirect to identification type page at next index" in {
          when(mockViewModelProvider.apply(any())(any()))
            .thenReturn(AddAnotherBorderTransportViewModel(listItems))

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherBorderTransportRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            routes.IdentificationController.onPageLoad(lrn, NormalMode, Index(listItems.length)).url
        }
      }

      // TODO - Redirect to cya page once implemented
      "when no submitted" - {
        "must redirect to CYA" ignore {
          when(mockViewModelProvider.apply(any())(any()))
            .thenReturn(AddAnotherBorderTransportViewModel(listItems))

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherBorderTransportRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            ???
        }
      }
    }

    "when max limit reached" - {
      "must redirect to CYA" ignore {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherBorderTransportViewModel(maxedOutListItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherBorderTransportRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          ???
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherBorderTransportViewModel(listItems))

        val allowMoreActiveBorderTransports = true

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherBorderTransportRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(allowMoreActiveBorderTransports).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherBorderTransportView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, lrn, mode, listItems, allowMoreActiveBorderTransports)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addAnotherBorderTransportRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addAnotherBorderTransportRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
