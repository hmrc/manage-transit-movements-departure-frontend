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
import forms.preTaskList.LocalReferenceNumberFormProvider
import generators.Generators
import models.SubmissionState.SubmissionState
import models.UserAnswersResponse.Answers
import models.{LocalReferenceNumber, UserAnswersResponse}
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.DuplicateService
import views.html.preTaskList.LocalReferenceNumberView

import scala.concurrent.Future

class LocalReferenceNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider = new LocalReferenceNumberFormProvider()
  private val prefix       = "localReferenceNumber"

  private val form: Form[LocalReferenceNumber] = formProvider(prefix)

  private lazy val localReferenceNumberRoute: String      = routes.LocalReferenceNumberController.onPageLoad().url
  private lazy val mockDuplicateService: DuplicateService = mock[DuplicateService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDuplicateService)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[DuplicateService]).toInstance(mockDuplicateService))

  "LocalReferenceNumber Controller" - {

    "must return OK and the correct view for a GET" in {
      val request = FakeRequest(GET, localReferenceNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[LocalReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val request = FakeRequest(GET, routes.LocalReferenceNumberController.onPageReload(lrn).url)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> lrn.value))

      val view = injector.instanceOf[LocalReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm)(request, messages).toString
    }

    "must return a Bad Request and errors when a duplicate local reference number is submitted" - {
      "when user answers found" in {
        val submissionState = arbitrary[SubmissionState](arbitrarySubmittedSubmissionState).sample.value

        when(mockDuplicateService.doesDraftOrSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(true))
        when(mockSessionRepository.get(any())(any())).thenReturn(Future.successful(Answers(emptyUserAnswers.copy(status = submissionState))))

        val invalidAnswer = "ABC123"

        val request = FakeRequest(POST, localReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", invalidAnswer))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) must include(
          "The Local Reference Number must be unique"
        )
      }

      "when user answers not found" in {
        when(mockDuplicateService.doesDraftOrSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(true))
        when(mockSessionRepository.get(any())(any())).thenReturn(Future.successful(UserAnswersResponse.NoAnswers))

        val invalidAnswer = "ABC123"

        val request = FakeRequest(POST, localReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", invalidAnswer))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) must include(
          "The Local Reference Number must be unique"
        )
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val invalidAnswer = ""

      val request = FakeRequest(POST, localReferenceNumberRoute)
        .withFormUrlEncodedBody(("value", invalidAnswer))

      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      val view = injector.instanceOf[LocalReferenceNumberView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(filledForm)(request, messages).toString

      verifyNoInteractions(mockDuplicateService)
    }

    "must create new user answers and redirect with NormalMode" - {
      "when there are no existing user answers" in {

        val app = guiceApplicationBuilder()
          .configure("features.isPreLodgeEnabled" -> true)
          .build()

        running(app) {
          when(mockDuplicateService.doesDraftOrSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(false))
          when(mockSessionRepository.get(any())(any())).thenReturn(Future.successful(Answers(emptyUserAnswers)))
          when(mockSessionRepository.put(any())(any())).thenReturn(Future.successful(true))

          val request = FakeRequest(POST, localReferenceNumberRoute)
            .withFormUrlEncodedBody(("value", lrn.toString))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual
            s"/manage-transit-movements/departures/$lrn/pre-task-list/standard-prelodged-declaration"

          verify(mockSessionRepository).get(eqTo(lrn))(any())
          verify(mockSessionRepository).put(eqTo(lrn))(any())
        }

      }
    }

    "must not create user answers and redirect with CheckMode" - {
      "when there are existing user answers" in {

        val app = guiceApplicationBuilder()
          .configure("features.isPreLodgeEnabled" -> true)
          .build()

        running(app) {
          when(mockDuplicateService.doesDraftOrSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(true))
          when(mockSessionRepository.get(any())(any())).thenReturn(Future.successful(Answers(emptyUserAnswers)))

          val request = FakeRequest(POST, localReferenceNumberRoute)
            .withFormUrlEncodedBody(("value", lrn.toString))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual
            s"/manage-transit-movements/departures/$lrn/pre-task-list/change-standard-prelodged-declaration"

          verify(mockSessionRepository).get(eqTo(lrn))(any())
          verify(mockSessionRepository, never()).put(any())(any())
        }

      }
    }

    "must redirect" - {
      "to technical difficulties when both GETs return a None" in {

        when(mockDuplicateService.doesDraftOrSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(false))
        when(mockSessionRepository.get(any())(any())).thenReturn(Future.successful(UserAnswersResponse.NoAnswers))
        when(mockSessionRepository.put(any())(any())).thenReturn(Future.successful(true))

        val request = FakeRequest(POST, localReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", lrn.toString))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.ErrorController.technicalDifficulties().url

        verify(mockSessionRepository).get(eqTo(lrn))(any())
        verify(mockSessionRepository).put(eqTo(lrn))(any())
      }

      "to draft no longer available when session repository returns bad request" in {

        when(mockDuplicateService.doesDraftOrSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(false))
        when(mockSessionRepository.get(any())(any())).thenReturn(Future.successful(UserAnswersResponse.BadRequest))
        when(mockSessionRepository.put(any())(any())).thenReturn(Future.successful(true))

        val request = FakeRequest(POST, localReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", lrn.toString))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.DraftNoLongerAvailableController.onPageLoad().url

        verify(mockSessionRepository).get(eqTo(lrn))(any())
        verify(mockSessionRepository).put(eqTo(lrn))(any())
      }
    }
  }
}
