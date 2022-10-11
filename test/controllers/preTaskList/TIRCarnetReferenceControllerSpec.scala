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

package controllers.preTaskList

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.preTaskList.TIRCarnetReferenceFormProvider
import generators.Generators
import models.{DeclarationType, NormalMode, ProcedureType}
import navigation.PreTaskListNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.{DeclarationTypePage, ProcedureTypePage, TIRCarnetReferencePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.preTaskList.TirCarnetReferenceView

import scala.concurrent.Future

class TIRCarnetReferenceControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val formProvider = new TIRCarnetReferenceFormProvider()
  private val form         = formProvider()
  private val mode         = NormalMode

  private lazy val tirCarnetReferenceRoute = routes.TIRCarnetReferenceController.onPageLoad(lrn, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreTaskListNavigatorProvider]).toInstance(fakePreTaskListNavigatorProvider))

  private val baseAnswers = emptyUserAnswers
    .setValue(ProcedureTypePage, ProcedureType.Normal)
    .setValue(DeclarationTypePage, DeclarationType.Option4)

  "TIRCarnetReference Controller" - {

    "must return OK and the correct view for a GET" in {
      setExistingUserAnswers(baseAnswers)

      val request = FakeRequest(GET, tirCarnetReferenceRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TirCarnetReferenceView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = baseAnswers.setValue(TIRCarnetReferencePage, "1234567890")
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, tirCarnetReferenceRoute)

      val result = route(app, request).value

      status(result) mustEqual OK

      val filledForm = form.bind(Map("value" -> "1234567890"))

      val view = injector.instanceOf[TirCarnetReferenceView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode)(request, messages).toString
    }

    "must redirect to local reference number page" - {
      "when non-Normal or non-Option4 procedure and declaration types" in {
        forAll(arbitrary[(ProcedureType, DeclarationType)].retryUntil {
          case (procedureType, declarationType) =>
            procedureType != ProcedureType.Normal || declarationType != DeclarationType.Option4
        }) {
          case (procedureType, declarationType) =>
            val userAnswers = emptyUserAnswers
              .setValue(ProcedureTypePage, procedureType)
              .setValue(DeclarationTypePage, declarationType)
            setExistingUserAnswers(userAnswers)

            val request = FakeRequest(GET, tirCarnetReferenceRoute)

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual routes.LocalReferenceNumberController.onPageLoad().url
        }
      }
    }

    "must redirect to session expired" - {
      "when procedure type and/or declaration type undefined" in {
        forAll(arbitrary[(Option[ProcedureType], Option[DeclarationType])].retryUntil {
          case (None, _) | (_, None) => true
          case _                     => false
        }) {
          case (procedureType, declarationType) =>
            val userAnswers = emptyUserAnswers
              .setValue(ProcedureTypePage, procedureType)
              .setValue(DeclarationTypePage, declarationType)
            setExistingUserAnswers(userAnswers)

            val request = FakeRequest(GET, tirCarnetReferenceRoute)

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
        }
      }
    }

    "must redirect to the next page when valid data is submitted" in {
      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, tirCarnetReferenceRoute)
        .withFormUrlEncodedBody(("value", "1234567890"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, tirCarnetReferenceRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[TirCarnetReferenceView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, tirCarnetReferenceRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, tirCarnetReferenceRoute)
        .withFormUrlEncodedBody(("value", "1234567890"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
