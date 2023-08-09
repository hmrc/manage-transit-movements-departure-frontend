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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.SubmissionConnector
import generators.{Generators, UserAnswersGenerator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.taskList.{PreTaskListTask, TaskListTask, TaskListViewModel, TaskStatus}
import views.html.TaskListView

class TaskListControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators with UserAnswersGenerator {

  private lazy val mockViewModel: TaskListViewModel        = mock[TaskListViewModel]
  private val mockSubmissionConnector: SubmissionConnector = mock[SubmissionConnector]
  private val expiryInDays                                 = 30

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[TaskListViewModel].toInstance(mockViewModel))
      .overrides(bind(classOf[SubmissionConnector]).toInstance(mockSubmissionConnector))

  override def beforeEach(): Unit = {
    reset(mockViewModel); reset(mockSubmissionConnector)
    super.beforeEach()
  }

  "Task List Controller" - {

    "must return OK and the correct view for a GET" in {
      val sampleTasks       = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryTask)).sample.value
      val isErrors: Boolean = sampleTasks.exists(_.isError)

      when(mockViewModel.apply(any())).thenReturn(sampleTasks)

      val userAnswers = emptyUserAnswers.copy(tasks = Map(PreTaskListTask.section -> TaskStatus.Completed))
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      val view = injector.instanceOf[TaskListView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn, sampleTasks, isErrors, Some(expiryInDays))(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to confirmation page when submission success" in {
      when(mockSubmissionConnector.post(any())(any()))
        .thenReturn(response(OK))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.DeclarationSubmittedController.onPageLoad(lrn).url
    }

    "must return a bad request for a 400" in {
      when(mockSubmissionConnector.post(any())(any()))
        .thenReturn(response(BAD_REQUEST))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }

    "must return a internal server error for a 500" in {
      when(mockSubmissionConnector.post(any())(any()))
        .thenReturn(response(INTERNAL_SERVER_ERROR))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
    }
  }
}
