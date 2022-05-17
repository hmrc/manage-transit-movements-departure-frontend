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

package controllers.addItems.documents

import base.{AppWithDefaultMockFixtures, SpecBase}
import commonTestUtils.UserAnswersSpecHelper
import forms.preTaskList.TIRCarnetReferenceFormProvider
import models.DeclarationType.{Option1, Option4}
import models.NormalMode
import navigation.Navigator
import navigation.annotations.addItems.AddItemsDocument
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.addItems.TIRCarnetReferencePage
import pages.preTaskList.DeclarationTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TirCarnetReferenceView

import scala.concurrent.Future

class TIRCarnetReferenceControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with UserAnswersSpecHelper {

  private val formProvider = new TIRCarnetReferenceFormProvider()
  private val form         = formProvider()
  private val mode         = NormalMode

  private lazy val tirCarnetReferenceRoute = controllers.addItems.documents.routes.TIRCarnetReferenceController.onPageLoad(lrn, index, index, NormalMode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[AddItemsDocument]).toInstance(fakeNavigator))

  "TIRCarnetReference Controller" - {

    "must return OK and the correct view for a GET" in {

      setUserAnswers(Some(emptyUserAnswers))

      val request = FakeRequest(GET, tirCarnetReferenceRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TirCarnetReferenceView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, itemIndex, documentIndex)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(TIRCarnetReferencePage(index, index), "1234567890").success.value
      setUserAnswers(Some(userAnswers))

      val request = FakeRequest(GET, tirCarnetReferenceRoute)

      val result = route(app, request).value

      status(result) mustEqual OK

      val filledForm = form.bind(Map("value" -> "1234567890"))

      val view = injector.instanceOf[TirCarnetReferenceView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, itemIndex, documentIndex)(request, messages).toString

    }

    "must redirect to the next page when valid data is submitted when DeclarationType is TIR" in {

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(DeclarationTypePage)(Option4)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setUserAnswers(Some(userAnswers))

      val request =
        FakeRequest(POST, tirCarnetReferenceRoute)
          .withFormUrlEncodedBody(("value", "1234567890"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must redirect Session Expired when DeclarationType is not TIR" in {

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(DeclarationTypePage)(Option1)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setUserAnswers(Some(userAnswers))

      val request =
        FakeRequest(POST, tirCarnetReferenceRoute)
          .withFormUrlEncodedBody(("value", "1234567890"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect Session Expired when DeclarationType is not defined" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setUserAnswers(Some(emptyUserAnswers))

      val request =
        FakeRequest(POST, tirCarnetReferenceRoute)
          .withFormUrlEncodedBody(("value", "1234567890"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .unsafeSetVal(DeclarationTypePage)(Option4)

      setUserAnswers(Some(userAnswers))

      val request   = FakeRequest(POST, tirCarnetReferenceRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[TirCarnetReferenceView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, itemIndex, documentIndex)(request, messages).toString

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, tirCarnetReferenceRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, tirCarnetReferenceRoute)
          .withFormUrlEncodedBody(("value", "1234567890"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
