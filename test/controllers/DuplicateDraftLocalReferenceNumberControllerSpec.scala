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
import forms.preTaskList.LocalReferenceNumberFormProvider
import models.LocalReferenceNumber
import models.SubmissionState.NotSubmitted
import navigation.PreTaskListNavigatorProvider
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DuplicateService
import views.html.DuplicateDraftLocalReferenceNumberView

import scala.concurrent.Future

class DuplicateDraftLocalReferenceNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                                                     = new LocalReferenceNumberFormProvider()
  private val prefix                                                           = "duplicateDraftLocalReferenceNumber"
  private def form(alreadyExists: Boolean = false): Form[LocalReferenceNumber] = formProvider(alreadyExists, prefix)

  private lazy val localReferenceNumberRoute: String      = routes.DuplicateDraftLocalReferenceNumberController.onPageLoad(lrn).url
  private lazy val mockDuplicateService: DuplicateService = mock[DuplicateService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[DuplicateService]).toInstance(mockDuplicateService))
      .overrides(bind(classOf[PreTaskListNavigatorProvider]).toInstance(fakePreTaskListNavigatorProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDuplicateService)
  }

  "LocalReferenceNumber Controller" - {

    "must return OK and the correct view for a GET" in {
      val request = FakeRequest(GET, localReferenceNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[DuplicateDraftLocalReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form(), lrn)(request, messages).toString
    }

    "must return a Bad Request and errors when a duplicate local reference number is submitted" in {

      val alreadyExists: Boolean = true

      when(mockDuplicateService.doesSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(alreadyExists))
      when(mockDuplicateService.alreadySubmitted(any())(any())).thenReturn(Future.successful(alreadyExists))

      val invalidAnswer = "ABC123"

      val request = FakeRequest(POST, localReferenceNumberRoute)
        .withFormUrlEncodedBody(("value", invalidAnswer))

      val filledForm = form(alreadyExists).bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      val view = injector.instanceOf[DuplicateDraftLocalReferenceNumberView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(filledForm, lrn)(request, messages).toString
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val alreadyExists: Boolean = false

      when(mockDuplicateService.doesSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(alreadyExists))
      when(mockDuplicateService.alreadySubmitted(any())(any())).thenReturn(Future.successful(alreadyExists))

      val invalidAnswer = ""

      val request = FakeRequest(POST, localReferenceNumberRoute)
        .withFormUrlEncodedBody(("value", invalidAnswer))

      val filledForm = form().bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      val view = injector.instanceOf[DuplicateDraftLocalReferenceNumberView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(filledForm, lrn)(request, messages).toString
    }

    "must create new user answers" - {
      "with old LRN data" in {

        val alreadyExists: Boolean = false
        when(mockDuplicateService.doesSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(alreadyExists))
        when(mockDuplicateService.alreadySubmitted(any())(any())).thenReturn(Future.successful(alreadyExists))
        when(mockDuplicateService.copyUserAnswers(any(), any(), eqTo(NotSubmitted))(any())).thenReturn(Future.successful(true))
        when(mockSessionRepository.get(any())(any())) thenReturn Future.successful(Some(emptyUserAnswers))

        val request = FakeRequest(POST, localReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", lrn.toString))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url

        verify(mockSessionRepository).get(eqTo(lrn))(any())
      }
    }

    "must redirect to technicalDifficulties" - {
      "when get users answers returns None" in {

        val alreadyExists: Boolean = false
        when(mockDuplicateService.doesSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(alreadyExists))
        when(mockDuplicateService.alreadySubmitted(any())(any())).thenReturn(Future.successful(alreadyExists))
        when(mockDuplicateService.copyUserAnswers(any(), any(), eqTo(NotSubmitted))(any())).thenReturn(Future.successful(true))
        when(mockSessionRepository.get(any())(any())) thenReturn Future.successful(None)

        val request = FakeRequest(POST, localReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", lrn.toString))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.ErrorController.technicalDifficulties().url

        verify(mockSessionRepository).get(eqTo(lrn))(any())
      }
    }

    "must redirect to technical difficulties" - {
      "when copy user answers fails" in {

        val alreadyExists: Boolean = false
        when(mockDuplicateService.doesSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(alreadyExists))
        when(mockDuplicateService.alreadySubmitted(any())(any())).thenReturn(Future.successful(alreadyExists))
        when(mockDuplicateService.copyUserAnswers(any(), any(), eqTo(NotSubmitted))(any())).thenReturn(Future.successful(false))

        val request = FakeRequest(POST, localReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", lrn.toString))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.ErrorController.technicalDifficulties().url

      }
    }
  }
}
