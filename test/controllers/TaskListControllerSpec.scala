/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.CacheConnector
import generators.Generators
import models.{DepartureMessages, SubmissionState}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.taskList.TaskListViewModel.TaskListViewModelProvider
import viewModels.taskList.{TaskListTask, TaskListViewModel}
import views.html.TaskListView

import scala.concurrent.Future

class TaskListControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private lazy val mockViewModelProvider: TaskListViewModelProvider = mock[TaskListViewModelProvider]
  private val mockCacheConnector: CacheConnector                    = mock[CacheConnector]
  private val expiryInDays                                          = 30.asInstanceOf[Number].longValue

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind(classOf[TaskListViewModelProvider]).toInstance(mockViewModelProvider),
        bind(classOf[CacheConnector]).toInstance(mockCacheConnector)
      )

  private val sampleTasks = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryTask)).sample.value

  override def beforeEach(): Unit = {
    reset(mockViewModelProvider)
    reset(mockCacheConnector)
    super.beforeEach()
  }

  "Task List Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers
      setExistingUserAnswers(userAnswers)

      val viewModel = TaskListViewModel(sampleTasks, userAnswers.status)

      when(mockViewModelProvider.apply(any())).thenReturn(viewModel)
      when(mockCacheConnector.getExpiryInDays(any())(any())).thenReturn(Future.successful(expiryInDays))

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      val view = injector.instanceOf[TaskListView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn, viewModel, expiryInDays)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if declaration submitted" in {
      val userAnswers = emptyUserAnswers.copy(status = SubmissionState.Submitted)
      setExistingUserAnswers(userAnswers)

      val viewModel = TaskListViewModel(sampleTasks, userAnswers.status)

      when(mockViewModelProvider.apply(any())).thenReturn(viewModel)

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(lrn)).url
    }

    "must redirect to Session Expired for a POST if declaration submitted" in {
      setExistingUserAnswers(emptyUserAnswers.copy(status = SubmissionState.Submitted))

      val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(lrn)).url
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(lrn)).url
    }

    "must redirect to confirmation page when submission success" - {
      "when not previously submitted" in {
        when(mockCacheConnector.submit(any())(any())).thenReturn(response(OK))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.DeclarationSubmittedController.departureDeclarationSubmitted(lrn).url
      }

      "when amending" in {
        forAll(Gen.oneOf(SubmissionState.Amendment, SubmissionState.GuaranteeAmendment)) {
          submissionStatus =>
            setExistingUserAnswers(emptyUserAnswers.copy(status = submissionStatus))

            when(mockCacheConnector.getMessages(any())(any())).thenReturn(Future.successful(DepartureMessages()))

            when(mockCacheConnector.submitAmendment(any())(any())).thenReturn(response(OK))

            val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual controllers.routes.DeclarationSubmittedController.departureAmendmentSubmitted(lrn).url
        }
      }
    }

    "must redirect to technical difficulties for an error" - {
      "when not previously submitted" in {
        forAll(Gen.oneOf(BAD_REQUEST, INTERNAL_SERVER_ERROR)) {
          errorCode =>
            when(mockCacheConnector.submit(any())(any())).thenReturn(response(errorCode))

            setExistingUserAnswers(emptyUserAnswers)

            val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual routes.ErrorController.technicalDifficulties().url
        }
      }

      "when amending" in {
        forAll(Gen.oneOf(SubmissionState.Amendment, SubmissionState.GuaranteeAmendment), Gen.choose(400: Int, 599: Int)) {
          (submissionStatus, errorCode) =>
            setExistingUserAnswers(emptyUserAnswers.copy(status = submissionStatus))

            when(mockCacheConnector.getMessages(any())(any())).thenReturn(Future.successful(DepartureMessages()))

            when(mockCacheConnector.submitAmendment(any())(any())).thenReturn(response(errorCode))

            val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual routes.ErrorController.technicalDifficulties().url
        }
      }
    }
  }
}
