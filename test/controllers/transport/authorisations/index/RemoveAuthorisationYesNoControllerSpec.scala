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

package controllers.transport.authorisations.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.transport.authorisations.{routes => authRoutes}
import forms.YesNoFormProvider
import models.{NormalMode, UserAnswers}
import models.transport.authorisations.AuthorisationType
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import pages.sections.transport.AuthorisationSection
import pages.transport.authorisation.index.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.authorisations.index.RemoveAuthorisationYesNoView

import scala.concurrent.Future

class RemoveAuthorisationYesNoControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar {

  private val formProvider = new YesNoFormProvider()

  private def form(authType: AuthorisationType, authRefNumber: String): Form[Boolean] =
    formProvider("transport.authorisations.index.removeAuthorisationYesNo", authType, authRefNumber)

  private val mode                               = NormalMode
  private lazy val removeAuthorisationYesNoRoute = routes.RemoveAuthorisationYesNoController.onPageLoad(lrn, mode, authorisationIndex).url

  private val authType      = Gen.oneOf(AuthorisationType.values).sample.value
  private val authRefNumber = arbitrary[String].sample.value

  "RemoveAuthorisationYesNo Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .setValue(AuthorisationTypePage(authorisationIndex), authType)
        .setValue(AuthorisationReferenceNumberPage(authorisationIndex), authRefNumber)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, removeAuthorisationYesNoRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemoveAuthorisationYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form(authType, authRefNumber), lrn, mode, authorisationIndex, authType.toString, authRefNumber)(request, messages).toString
    }

    "when yes submitted" - {
      "must redirect to add another country of routing and remove country at specified index" in {

        reset(mockSessionRepository)
        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

        val userAnswers = emptyUserAnswers
          .setValue(AuthorisationTypePage(authorisationIndex), authType)
          .setValue(AuthorisationReferenceNumberPage(authorisationIndex), authRefNumber)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeAuthorisationYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          authRoutes.AddAnotherAuthorisationController.onPageLoad(lrn, mode).url

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.get(AuthorisationSection(index)) mustNot be(defined)
      }
    }

    "when no submitted" - {
      "must redirect to add another country and not remove country at specified index" in {
        reset(mockSessionRepository)
        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

        val userAnswers = emptyUserAnswers
          .setValue(AuthorisationTypePage(authorisationIndex), authType)
          .setValue(AuthorisationReferenceNumberPage(authorisationIndex), authRefNumber)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeAuthorisationYesNoRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          authRoutes.AddAnotherAuthorisationController.onPageLoad(lrn, mode).url

        verify(mockSessionRepository, never()).set(any())(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .setValue(AuthorisationTypePage(authorisationIndex), authType)
        .setValue(AuthorisationReferenceNumberPage(authorisationIndex), authRefNumber)

      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, removeAuthorisationYesNoRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form(authType, authRefNumber).bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[RemoveAuthorisationYesNoView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, authorisationIndex, authType.toString, authRefNumber)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, removeAuthorisationYesNoRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, removeAuthorisationYesNoRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
