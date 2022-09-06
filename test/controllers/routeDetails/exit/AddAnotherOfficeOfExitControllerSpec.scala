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

package controllers.routeDetails.exit

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.AddAnotherFormProvider
import generators.Generators
import models.{Index, NormalMode}
import navigation.routeDetails.RouteDetailsNavigatorProvider
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
import viewModels.routeDetails.exit.AddAnotherOfficeOfExitViewModel
import viewModels.routeDetails.exit.AddAnotherOfficeOfExitViewModel.AddAnotherOfficeOfExitViewModelProvider
import views.html.routeDetails.exit.AddAnotherOfficeOfExitView

class AddAnotherOfficeOfExitControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val formProvider                          = new AddAnotherFormProvider()
  private def form(allowMoreOfficesOfExit: Boolean) = formProvider("routeDetails.exit.addAnotherOfficeOfExit", allowMoreOfficesOfExit)
  private lazy val addAnotherOfficeOfExitRoute      = routes.AddAnotherOfficeOfExitController.onPageLoad(lrn).url
  private val mockViewModelProvider                 = mock[AddAnotherOfficeOfExitViewModelProvider]
  private val listItem                              = arbitrary[ListItem].sample.value
  private val listItems                             = Seq.fill(Gen.choose(1, frontendAppConfig.maxOfficesOfExit - 1).sample.value)(listItem)
  private val maxedOutListItems                     = Seq.fill(frontendAppConfig.maxOfficesOfExit)(listItem)

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[RouteDetailsNavigatorProvider]).toInstance(fakeRouteDetailsNavigatorProvider))
      .overrides(bind(classOf[AddAnotherOfficeOfExitViewModelProvider]).toInstance(mockViewModelProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  "AddAnotherOfficeOfExitController" - {

    "redirect to correct start page in this sub-section" - {
      "when 0 offices of exit" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherOfficeOfExitViewModel(Nil))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherOfficeOfExitRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {

        val allowMoreOfficesOfExit = true

        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherOfficeOfExitViewModel(listItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherOfficeOfExitRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherOfficeOfExitView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreOfficesOfExit), lrn, listItems, allowMoreOfficesOfExit)(request, messages).toString
      }

      "when max limit reached" in {

        val allowMoreOfficesOfExit = false

        val listItems = maxedOutListItems

        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherOfficeOfExitViewModel(listItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherOfficeOfExitRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherOfficeOfExitView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreOfficesOfExit), lrn, listItems, allowMoreOfficesOfExit)(request, messages).toString
      }
    }

    "when max limit not reached" - {
      "when yes submitted" - {
        "must redirect to office of exit country page at next index" in {
          when(mockViewModelProvider.apply(any())(any()))
            .thenReturn(AddAnotherOfficeOfExitViewModel(listItems))

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherOfficeOfExitRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            controllers.routeDetails.exit.index.routes.OfficeOfExitCountryController.onPageLoad(lrn, Index(listItems.length), NormalMode).url
        }
      }

      "when no submitted" - {
        "must redirect to the next page" in {
          when(mockViewModelProvider.apply(any())(any()))
            .thenReturn(AddAnotherOfficeOfExitViewModel(listItems))

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherOfficeOfExitRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }
    }

    "when max limit reached" - {
      "must redirect to the next page" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherOfficeOfExitViewModel(maxedOutListItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherOfficeOfExitRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherOfficeOfExitViewModel(listItems))

        val allowMoreOfficesOfExit = true

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherOfficeOfExitRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(allowMoreOfficesOfExit).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherOfficeOfExitView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, lrn, listItems, allowMoreOfficesOfExit)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addAnotherOfficeOfExitRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addAnotherOfficeOfExitRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }

}
