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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.TaskListViewModel
import viewModels.taskList.Task
import views.html.TaskListView

class TaskListControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private lazy val mockViewModel = mock[TaskListViewModel]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[TaskListViewModel].toInstance(mockViewModel))

  "Task List Controller" - {

    "must return OK and the correct view for a GET" in {
      val sampleTasks = listWithMaxLength[Task]()(arbitraryTask).sample.value

      when(mockViewModel.apply(any())(any())).thenReturn(sampleTasks)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      val view = injector.instanceOf[TaskListView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn, sampleTasks)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to ???" ignore {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual ???
    }
  }
}
