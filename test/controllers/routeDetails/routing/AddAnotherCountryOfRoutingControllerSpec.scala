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

package controllers.routeDetails.routing

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
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import viewModels.routeDetails.routing.AddAnotherCountryOfRoutingViewModel
import viewModels.routeDetails.routing.AddAnotherCountryOfRoutingViewModel.AddAnotherCountryOfRoutingViewModelProvider
import views.html.routeDetails.routing.AddAnotherCountryOfRoutingView

class AddAnotherCountryOfRoutingControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val formProvider                      = new AddAnotherFormProvider()
  private def form(allowMoreCountries: Boolean) = formProvider("routeDetails.routing.addAnotherCountryOfRouting", allowMoreCountries)

  private lazy val addAnotherCountryOfRoutingRoute = routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn).url

  private val mockViewModelProvider = mock[AddAnotherCountryOfRoutingViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AddAnotherCountryOfRoutingViewModelProvider]).toInstance(mockViewModelProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  private val listItem          = arbitrary[ListItem].sample.value
  private val listItems         = Seq.fill(Gen.choose(1: Int, 8: Int).sample.value)(listItem)
  private val maxedOutListItems = Seq.fill(9: Int)(listItem)

  "AddAnotherCountryOfRoutingController" - {

    "redirect to binding itinerary page" - {
      "when 0 countries" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherCountryOfRoutingViewModel(Nil))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherCountryOfRoutingRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.BindingItineraryController.onPageLoad(lrn, NormalMode).url
      }
    }

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {

        val allowMoreCountries = true

        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherCountryOfRoutingViewModel(listItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherCountryOfRoutingRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherCountryOfRoutingView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreCountries), lrn, listItems, allowMoreCountries)(request, messages).toString
      }

      "when max limit reached" in {

        val allowMoreCountries = false

        val listItems = maxedOutListItems

        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherCountryOfRoutingViewModel(listItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherCountryOfRoutingRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherCountryOfRoutingView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreCountries), lrn, listItems, allowMoreCountries)(request, messages).toString
      }
    }

    "when max limit not reached" - {
      "when yes submitted" - {
        "must redirect to guarantee type page at next index" in {
          when(mockViewModelProvider.apply(any())(any()))
            .thenReturn(AddAnotherCountryOfRoutingViewModel(listItems))

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherCountryOfRoutingRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            controllers.routeDetails.routing.routes.CountryOfRoutingController.onPageLoad(lrn, NormalMode, Index(listItems.length)).url
        }
      }

      "when no submitted" - {
        "must redirect to check your answers" in {
          when(mockViewModelProvider.apply(any())(any()))
            .thenReturn(AddAnotherCountryOfRoutingViewModel(listItems))

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherCountryOfRoutingRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            routes.CheckYourAnswersController.onPageLoad(lrn).url
        }
      }
    }

    "when max limit reached" - {
      "must redirect to check your answers" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherCountryOfRoutingViewModel(maxedOutListItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherCountryOfRoutingRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.CheckYourAnswersController.onPageLoad(lrn).url
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherCountryOfRoutingViewModel(listItems))

        val allowMoreCountries = true

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherCountryOfRoutingRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(allowMoreCountries).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherCountryOfRoutingView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, lrn, listItems, allowMoreCountries)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addAnotherCountryOfRoutingRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addAnotherCountryOfRoutingRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
