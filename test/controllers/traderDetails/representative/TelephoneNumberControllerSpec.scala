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

package controllers.traderDetails.representative

import models.{NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.Representative
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import forms.RepresentativePhoneFormProvider
import views.html.traderDetails.representative.TelephoneNumberView
import services.UserAnswersService
import pages.traderDetails.representative.TelephoneNumberPage
import base.{AppWithDefaultMockFixtures, SpecBase}

import scala.concurrent.Future

class TelephoneNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                  = new RepresentativePhoneFormProvider()
  private val form                          = formProvider("traderDetails.representative.phone")
  private val mode                          = NormalMode
  private lazy val representativePhoneRoute = routes.TelephoneNumberController.onPageLoad(lrn, mode).url
  private lazy val mockUserAnswersService   = mock[UserAnswersService]

  private val validAnswer: String = "123123123"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[Representative]).toInstance(fakeNavigator))
      .overrides(bind[UserAnswersService].toInstance(mockUserAnswersService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswersService)
  }

  "RepresentativePhone Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, representativePhoneRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TelephoneNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(lrn, eoriNumber).set(TelephoneNumberPage, validAnswer).success.value
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, representativePhoneRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> validAnswer))

      val view = injector.instanceOf[TelephoneNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode)(request, messages).toString

    }

    "must redirect to the next page when valid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockUserAnswersService.getOrCreateUserAnswers(any(), any())) thenReturn Future.successful(emptyUserAnswers)

      val request =
        FakeRequest(POST, representativePhoneRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, representativePhoneRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[TelephoneNumberView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode)(request, messages).toString

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, representativePhoneRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, representativePhoneRoute)
          .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
