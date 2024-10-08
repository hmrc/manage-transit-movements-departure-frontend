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
import forms.preTaskList.LocalReferenceNumberFormProvider
import models.LocalReferenceNumber
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito._
import play.api.data.Form
import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DuplicateService
import views.html.NewLocalReferenceNumberView

import scala.concurrent.Future

class NewLocalReferenceNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider = new LocalReferenceNumberFormProvider()
  private val prefix       = "newLocalReferenceNumber"

  private val form: Form[LocalReferenceNumber] = formProvider(prefix)

  private val oldLrn: LocalReferenceNumber = LocalReferenceNumber("ABCD1234567890123").value
  private val newLrn: LocalReferenceNumber = LocalReferenceNumber("DCBA0987654321321").value

  private lazy val localReferenceNumberOnPageLoad: String = routes.NewLocalReferenceNumberController.onPageLoad(oldLrn).url
  private lazy val localReferenceNumberOnSubmit: String   = routes.NewLocalReferenceNumberController.onSubmit(oldLrn).url

  private lazy val mockDuplicateService: DuplicateService = mock[DuplicateService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDuplicateService)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(inject.bind(classOf[DuplicateService]).toInstance(mockDuplicateService))

  "NewLocalReferenceNumber Controller" - {

    "must return OK and the correct view for a GET when old local reference number has been submitted" in {

      when(mockDuplicateService.doesIE028ExistForLrn(eqTo(oldLrn))(any())).thenReturn(Future.successful(true))

      val request = FakeRequest(GET, localReferenceNumberOnPageLoad)

      val result = route(app, request).value

      val view = injector.instanceOf[NewLocalReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, oldLrn)(request, messages).toString
    }

    "must redirect to bad request page when old local reference number is not in the API" in {

      when(mockDuplicateService.doesIE028ExistForLrn(eqTo(oldLrn))(any())).thenReturn(Future.successful(false))

      val request = FakeRequest(GET, localReferenceNumberOnPageLoad)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.ErrorController.badRequest().url

    }

    "must create new user answers with old lrn's data" - {
      "and redirect to taskList" in {

        when(mockDuplicateService.doesDraftOrSubmissionExistForLrn(eqTo(newLrn))(any())).thenReturn(Future.successful(false))
        when(mockDuplicateService.copyUserAnswers(eqTo(oldLrn), eqTo(newLrn))(any())).thenReturn(Future.successful(true))

        val request = FakeRequest(POST, localReferenceNumberOnSubmit)
          .withFormUrlEncodedBody(("value", newLrn.toString))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.TaskListController.onPageLoad(newLrn).url

        verify(mockDuplicateService).copyUserAnswers(eqTo(oldLrn), eqTo(newLrn))(any())
      }
    }

    "must return a Bad Request and errors when invalid data that cannot convert to a lrn is submitted" in {

      val invalidAnswer = ""

      val request = FakeRequest(POST, localReferenceNumberOnSubmit)
        .withFormUrlEncodedBody(("value", invalidAnswer))

      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      val view = injector.instanceOf[NewLocalReferenceNumberView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(filledForm, oldLrn)(request, messages).toString

      verifyNoInteractions(mockDuplicateService)
    }

    "must return a Bad Request and errors when a duplicate lrn is submitted" in {

      when(mockDuplicateService.doesDraftOrSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(true))

      val request = FakeRequest(POST, localReferenceNumberOnSubmit)
        .withFormUrlEncodedBody(("value", oldLrn.toString))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) must include(
        "The Local Reference Number must be unique"
      )

      verify(mockDuplicateService).doesDraftOrSubmissionExistForLrn(eqTo(oldLrn))(any())
      verify(mockDuplicateService, never()).copyUserAnswers(any(), any())(any())
    }

    "must redirect to technical difficulties when" - {
      "POST of userAnswers returns false" in {
        when(mockDuplicateService.doesDraftOrSubmissionExistForLrn(any())(any())).thenReturn(Future.successful(false))
        when(mockDuplicateService.copyUserAnswers(any(), any())(any())).thenReturn(Future.successful(false))

        val request = FakeRequest(POST, localReferenceNumberOnSubmit)
          .withFormUrlEncodedBody(("value", newLrn.toString))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.ErrorController.technicalDifficulties().url
      }
    }
  }
}
