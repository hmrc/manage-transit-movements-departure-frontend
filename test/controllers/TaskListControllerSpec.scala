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
import generators.{Generators, UserAnswersGenerator}
import models.UserAnswers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.ApiService
import viewModels.taskList.{TaskListTask, TaskListViewModel}
import views.html.TaskListView

class TaskListControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators with UserAnswersGenerator {

  private lazy val mockViewModel         = mock[TaskListViewModel]
  private val mockApiService: ApiService = mock[ApiService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[TaskListViewModel].toInstance(mockViewModel))
      .overrides(bind(classOf[ApiService]).toInstance(mockApiService))

  override def beforeEach(): Unit = {
    reset(mockViewModel); reset(mockApiService)
    super.beforeEach()
  }

  "Task List Controller" - {

    "must return OK and the correct view for a GET" in {
      val sampleTasks = arbitrary[List[TaskListTask]](arbitraryTasks(arbitraryTask)).sample.value

      when(mockViewModel.apply(any())).thenReturn(sampleTasks)

      val userAnswers = arbitraryDepartureAnswers(emptyUserAnswers).sample.value
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      val view = injector.instanceOf[TaskListView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn, sampleTasks)(request, messages).toString
    }

    "must redirect to LRN page if pre- task list section is incomplete" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.preTaskList.routes.LocalReferenceNumberController.onPageLoad().url
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to confirmation page when submission success" in {
      when(mockApiService.submitDeclaration(any())(any()))
        .thenReturn(response(OK))

      val userAnswers: UserAnswers = arbitraryDepartureAnswers(emptyUserAnswers).sample.value

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.DeclarationSubmittedController.onPageLoad().url
    }

    "must return a bad request for a 400" in {
      when(mockApiService.submitDeclaration(any())(any()))
        .thenReturn(response(BAD_REQUEST))

      val userAnswers: UserAnswers = arbitraryDepartureAnswers(emptyUserAnswers).sample.value

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }

    "must return a internal server error for a 500" in {
      when(mockApiService.submitDeclaration(any())(any()))
        .thenReturn(response(INTERNAL_SERVER_ERROR))

      val userAnswers: UserAnswers = arbitraryDepartureAnswers(emptyUserAnswers).sample.value

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
    }
  }
}
