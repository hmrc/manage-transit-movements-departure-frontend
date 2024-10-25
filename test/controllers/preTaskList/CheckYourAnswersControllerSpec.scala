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

package controllers.preTaskList

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.preTaskList.PreTaskListViewModel
import viewModels.preTaskList.PreTaskListViewModel.PreTaskListViewModelProvider
import viewModels.sections.Section
import viewModels.taskList.TaskStatus
import views.html.preTaskList.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private lazy val mockViewModelProvider = mock[PreTaskListViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[PreTaskListViewModelProvider].toInstance(mockViewModelProvider))

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {
      forAll(arbitraryPreTaskListAnswers(emptyUserAnswers), arbitrary[Section]) {
        (userAnswers, section) =>
          setExistingUserAnswers(userAnswers)

          when(mockViewModelProvider.apply(any())(any()))
            .thenReturn(PreTaskListViewModel(section))

          val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn).url)

          val result = route(app, request).value

          val view = injector.instanceOf[CheckYourAnswersView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(userAnswers.lrn, Seq(section))(request, messages).toString
      }
    }

    "must redirect to Technical Difficulties Page for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(lrn)).url
    }

    "must redirect back into journey if answers in invalid state" in {
      val app = super
        .guiceApplicationBuilder()
        .overrides(bind[PreTaskListViewModelProvider].toInstance(mockViewModelProvider))
        .configure("features.isPreLodgeEnabled" -> true)
        .build()
      running(app) {
        val userAnswers = emptyUserAnswers
        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(userAnswers.lrn).url)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.AdditionalDeclarationTypeController.onPageLoad(userAnswers.lrn, NormalMode).url
      }

    }

    "must redirect to task list / declaration summary" in {
      forAll(arbitraryPreTaskListAnswers(emptyUserAnswers)) {
        userAnswers =>
          beforeEach()

          setExistingUserAnswers(userAnswers)

          when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

          val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(lrn).url)

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual controllers.routes.TaskListController.onPageLoad(lrn).url

          val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
          userAnswersCaptor.getValue.tasks.apply(".preTaskList") mustBe TaskStatus.Completed
      }
    }
  }
}
