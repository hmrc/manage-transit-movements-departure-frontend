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

package controllers.guaranteeDetails

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.AddAnotherFormProvider
import generators.{Generators, GuaranteeDetailsUserAnswersGenerator}
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
import viewModels.guaranteeDetails.AddAnotherGuaranteeViewModel
import viewModels.guaranteeDetails.AddAnotherGuaranteeViewModel.AddAnotherGuaranteeViewModelProvider
import views.html.guaranteeDetails.AddAnotherGuaranteeView

class AddAnotherGuaranteeControllerSpec
    extends SpecBase
    with AppWithDefaultMockFixtures
    with ScalaCheckPropertyChecks
    with Generators
    with GuaranteeDetailsUserAnswersGenerator {

  private val formProvider                   = new AddAnotherFormProvider()
  private def form(allowMoreEvents: Boolean) = formProvider("guaranteeDetails.addAnotherGuarantee", allowMoreEvents)

  private lazy val addAnotherGuaranteeRoute = routes.AddAnotherGuaranteeController.onPageLoad(lrn).url

  private val mockViewModelProvider = mock[AddAnotherGuaranteeViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AddAnotherGuaranteeViewModelProvider]).toInstance(mockViewModelProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  private val listItem          = arbitrary[ListItem].sample.value
  private val listItems         = Seq.fill(Gen.choose(1: Int, 8: Int).sample.value)(listItem)
  private val maxedOutListItems = Seq.fill(9: Int)(listItem)

  "AddAnotherGuaranteeController" - {

    "redirect to add guarantee yes/no page" - {
      "when 0 guarantees" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherGuaranteeViewModel(Nil))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherGuaranteeRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.AddGuaranteeYesNoController.onPageLoad(lrn).url
      }
    }

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {

        val allowMoreEvents = true

        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherGuaranteeViewModel(listItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherGuaranteeRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherGuaranteeView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreEvents), lrn, listItems, allowMoreEvents)(request, messages).toString
      }

      "when max limit reached" in {

        val allowMoreEvents = false

        val listItems = maxedOutListItems

        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherGuaranteeViewModel(listItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherGuaranteeRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherGuaranteeView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreEvents), lrn, listItems, allowMoreEvents)(request, messages).toString
      }
    }

    "when max limit not reached" - {
      "when yes submitted" - {
        "must redirect to guarantee type page at next index" in {
          when(mockViewModelProvider.apply(any())(any()))
            .thenReturn(AddAnotherGuaranteeViewModel(listItems))

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherGuaranteeRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            routes.GuaranteeTypeController.onPageLoad(lrn, NormalMode, Index(listItems.length)).url
        }
      }

      "when no submitted" - {
        "must redirect to task list" in {
          when(mockViewModelProvider.apply(any())(any()))
            .thenReturn(AddAnotherGuaranteeViewModel(listItems))

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherGuaranteeRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            controllers.routes.TaskListController.onPageLoad(lrn).url
        }
      }
    }

    "when max limit reached" - {
      "must redirect to task list" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherGuaranteeViewModel(maxedOutListItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherGuaranteeRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.routes.TaskListController.onPageLoad(lrn).url
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherGuaranteeViewModel(listItems))

        val allowMoreEvents = true

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherGuaranteeRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(allowMoreEvents).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherGuaranteeView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, lrn, listItems, allowMoreEvents)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addAnotherGuaranteeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addAnotherGuaranteeRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
