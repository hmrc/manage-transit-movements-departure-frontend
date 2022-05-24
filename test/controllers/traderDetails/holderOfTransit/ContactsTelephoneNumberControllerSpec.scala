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

package controllers.traderDetails.holderOfTransit

import models.{NormalMode, TelephoneNumber, UserAnswers}
import navigation.Navigator
import navigation.annotations.TraderDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import forms.TelephoneNumberFormProvider
import views.html.traderDetails.holderOfTransit.ContactsTelephoneNumberView
import services.UserAnswersService
import pages.traderDetails.holderOfTransit.{ContactNamePage, ContactsTelephoneNumberPage}
import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators

import scala.concurrent.Future

class ContactsTelephoneNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider                      = new TelephoneNumberFormProvider()
  private val holderName                        = "Test Holder Name"
  private val form                              = formProvider("traderDetails.holderOfTransit.contactsTelephoneNumber", holderName)
  private val mode                              = NormalMode
  private lazy val contactsTelephoneNumberRoute = routes.ContactsTelephoneNumberController.onPageLoad(lrn, mode).url
  private lazy val mockUserAnswersService       = mock[UserAnswersService]

  private val validAnswer: TelephoneNumber = arbitraryTelephoneNumber.arbitrary.sample.get

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[TraderDetails]).toInstance(fakeNavigator))
      .overrides(bind[UserAnswersService].toInstance(mockUserAnswersService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswersService)
  }

  "traderDetails.holderOfTransit.ContactsTelephoneNumber Controller" - {

    "must return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.setValue(ContactNamePage, holderName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, contactsTelephoneNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ContactsTelephoneNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, holderName)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = UserAnswers(lrn, eoriNumber)
        .setValue(ContactNamePage, holderName)
        .set(ContactsTelephoneNumberPage, validAnswer)
        .success
        .value

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, contactsTelephoneNumberRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> validAnswer.value))

      val view = injector.instanceOf[ContactsTelephoneNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, holderName)(request, messages).toString

    }

    "must redirect to the next page when valid data is submitted" in {
      val userAnswers = emptyUserAnswers.setValue(ContactNamePage, holderName)
      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, contactsTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", validAnswer.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val userAnswers = emptyUserAnswers.setValue(ContactNamePage, holderName)
      setExistingUserAnswers(userAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, contactsTelephoneNumberRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ContactsTelephoneNumberView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, holderName)(request, messages).toString

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, contactsTelephoneNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, contactsTelephoneNumberRoute)
          .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
