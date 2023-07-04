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
import connectors.CacheConnector
import forms.NewLocalReferenceNumberFormProvider
import models.LocalReferenceNumber
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import play.api.data.Form
import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DuplicateService
import viewModels.taskList.TaskStatus
import views.html.NewLocalReferenceNumberView

import scala.concurrent.Future

class NewLocalReferenceNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                                                     = new NewLocalReferenceNumberFormProvider()
  private def form(alreadyExists: Boolean = false): Form[LocalReferenceNumber] = formProvider(alreadyExists)

  private val oldLrn: LocalReferenceNumber = LocalReferenceNumber("ABCD1234567890123").value
  private val newLrn: LocalReferenceNumber = LocalReferenceNumber("DCBA0987654321321").value

  private lazy val localReferenceNumberOnPageLoad: String = routes.NewLocalReferenceNumberController.onPageLoad(oldLrn).url
  private lazy val localReferenceNumberOnSubmit: String   = routes.NewLocalReferenceNumberController.onSubmit(oldLrn).url

  private lazy val mockDuplicateService: DuplicateService = mock[DuplicateService]
  private lazy val mockCacheConnector: CacheConnector     = mock[CacheConnector]

  override def beforeEach(): Unit = {
    reset(mockDuplicateService)
    reset(mockCacheConnector)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(inject.bind(classOf[DuplicateService]).toInstance(mockDuplicateService))
      .overrides(inject.bind(classOf[CacheConnector]).toInstance(mockCacheConnector))

  "NewLocalReferenceNumber Controller" - {

    "must return OK and the correct view for a GET when old local reference number has been submitted" in {

      when(mockDuplicateService.apiLRNCheck(eqTo(oldLrn))(any())).thenReturn(Future.successful(true))
      when(mockCacheConnector.apiLRNCheck(eqTo(oldLrn))(any())).thenReturn(Future.successful(true))

      val request = FakeRequest(GET, localReferenceNumberOnPageLoad)

      val result = route(app, request).value

      val view = injector.instanceOf[NewLocalReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form(), oldLrn)(request, messages).toString
    }

    "must redirect to bad request page when old local reference number is not in the API" in {

      when(mockDuplicateService.apiLRNCheck(eqTo(oldLrn))(any())).thenReturn(Future.successful(false))
      when(mockCacheConnector.apiLRNCheck(eqTo(oldLrn))(any())).thenReturn(Future.successful(false))

      val request = FakeRequest(GET, localReferenceNumberOnPageLoad)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.ErrorController.badRequest().url

    }

    "must create new user answers with old lrn's data" - {
      "and redirect to taskList" in {

        val oldLrnData    = emptyUserAnswers.copy(lrn = oldLrn, tasks = Map("task1" -> TaskStatus.Error))
        val newDataToSend = oldLrnData.copy(lrn = newLrn)

        when(mockDuplicateService.isDuplicateLRN(eqTo(newLrn))(any())).thenReturn(Future.successful(false))
        when(mockCacheConnector.isDuplicateLRN(eqTo(newLrn))(any())).thenReturn(Future.successful(false))

        when(mockCacheConnector.get(eqTo(oldLrn))(any())) thenReturn Future.successful(Some(oldLrnData))
        when(mockCacheConnector.post(eqTo(newDataToSend))(any())) thenReturn Future.successful(true)
        when(mockDuplicateService.copyUserAnswers(eqTo(oldLrn), eqTo(newLrn))(any())).thenReturn(Future.successful(true))
        when(mockDuplicateService.populateForm(any())(any())).thenReturn(Future.successful(form()))

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
      val isDuplicate   = false

      when(mockCacheConnector.isDuplicateLRN(any())(any())).thenReturn(Future.successful(isDuplicate))

      when(mockDuplicateService.isDuplicateLRN(any())(any())).thenReturn(Future.successful(isDuplicate))
      when(mockDuplicateService.copyUserAnswers(any(), any())(any())).thenReturn(Future.successful(false))
      when(mockDuplicateService.populateForm(any())(any())).thenReturn(Future.successful(form()))

      val request = FakeRequest(POST, localReferenceNumberOnSubmit)
        .withFormUrlEncodedBody(("value", invalidAnswer))

      val filledForm = form(isDuplicate).bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      val view = injector.instanceOf[NewLocalReferenceNumberView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(filledForm, oldLrn)(request, messages).toString
    }

    "must return a Bad Request and errors when a duplicate lrn is submitted" in {

      val isDuplicate = true

      when(mockCacheConnector.isDuplicateLRN(any())(any())).thenReturn(Future.successful(isDuplicate))

      when(mockDuplicateService.isDuplicateLRN(any())(any())).thenReturn(Future.successful(isDuplicate))
      when(mockDuplicateService.copyUserAnswers(any(), any())(any())).thenReturn(Future.successful(false))
      when(mockDuplicateService.populateForm(any())(any())).thenReturn(Future.successful(form(alreadyExists = true)))

      val request = FakeRequest(POST, localReferenceNumberOnSubmit)
        .withFormUrlEncodedBody(("value", oldLrn.toString))

      val filledForm = form(isDuplicate).bind(Map("value" -> oldLrn.toString))

      val result = route(app, request).value

      val view = injector.instanceOf[NewLocalReferenceNumberView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(filledForm, oldLrn)(request, messages).toString
    }

    "must redirect to technical difficulties when" - {
      "GET of userAnswers returns None" in {

        val isDuplicate = false

        when(mockCacheConnector.isDuplicateLRN(any())(any())).thenReturn(Future.successful(isDuplicate))
        when(mockCacheConnector.get(any())(any())).thenReturn(Future.successful(None))
        when(mockDuplicateService.isDuplicateLRN(any())(any())).thenReturn(Future.successful(isDuplicate))
        when(mockDuplicateService.populateForm(any())(any())).thenReturn(Future.successful(form()))
        when(mockDuplicateService.copyUserAnswers(any(), any())(any())).thenReturn(Future.successful(false))

        val request = FakeRequest(POST, localReferenceNumberOnSubmit)
          .withFormUrlEncodedBody(("value", newLrn.toString))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.ErrorController.technicalDifficulties().url

      }
      "POST of userAnswers returns false" in {
        val isDuplicate = false

        when(mockCacheConnector.isDuplicateLRN(any())(any())).thenReturn(Future.successful(isDuplicate))
        when(mockCacheConnector.get(any())(any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
        when(mockCacheConnector.post(any())(any())).thenReturn(Future.successful(false))
        when(mockDuplicateService.isDuplicateLRN(any())(any())).thenReturn(Future.successful(isDuplicate))
        when(mockDuplicateService.populateForm(any())(any())).thenReturn(Future.successful(form()))
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
