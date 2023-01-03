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

package controllers.guaranteeDetails.guarantee

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.AccessCodeFormProvider
import models.NormalMode
import navigation.GuaranteeNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.guaranteeDetails.guarantee.AccessCodePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.guaranteeDetails.guarantee.AccessCodeView

import scala.concurrent.Future

class AccessCodeControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider         = new AccessCodeFormProvider()
  private val form                 = formProvider("guaranteeDetails.guarantee.accessCode")
  private val mode                 = NormalMode
  private lazy val accessCodeRoute = routes.AccessCodeController.onPageLoad(lrn, mode, index).url
  private val validAccessCode      = "1234"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[GuaranteeNavigatorProvider]).toInstance(fakeGuaranteeNavigatorProvider))

  "AccessCode Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, accessCodeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[AccessCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, index)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(AccessCodePage(index), validAccessCode)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, accessCodeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> validAccessCode))

      val view = injector.instanceOf[AccessCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, index)(request, messages).toString

    }

    "must redirect to the next page when valid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, accessCodeRoute)
          .withFormUrlEncodedBody(("value", validAccessCode))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, accessCodeRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[AccessCodeView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, index)(request, messages).toString

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, accessCodeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, accessCodeRoute)
          .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
