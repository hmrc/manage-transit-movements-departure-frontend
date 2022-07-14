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

package controllers.guaranteeDetails.guarantee

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.guaranteeDetails.GuaranteeTypeFormProvider
import models.guaranteeDetails.GuaranteeType
import models.{NormalMode, UserAnswers}
import navigation.GuaranteeNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.guaranteeDetails.guarantee
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.guaranteeDetails.guarantee.GuaranteeTypeView

import scala.concurrent.Future

class GuaranteeTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider            = new GuaranteeTypeFormProvider()
  private val form                    = formProvider()
  private val mode                    = NormalMode
  private lazy val guaranteeTypeRoute = routes.GuaranteeTypeController.onPageLoad(lrn, mode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[GuaranteeNavigatorProvider]).toInstance(fakeGuaranteeNavigatorProvider))

  "GuaranteeType Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, guaranteeTypeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[GuaranteeTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, GuaranteeType.radioItemsU(userAnswers), mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(lrn, eoriNumber).set(guarantee.GuaranteeTypePage(index), GuaranteeType.values.head).success.value
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, guaranteeTypeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> GuaranteeType.values.head.toString))

      val view = injector.instanceOf[GuaranteeTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, GuaranteeType.radioItemsU(userAnswers), mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, guaranteeTypeRoute)
          .withFormUrlEncodedBody(("value", GuaranteeType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, guaranteeTypeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[GuaranteeTypeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, GuaranteeType.radioItems, mode, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, guaranteeTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, guaranteeTypeRoute)
          .withFormUrlEncodedBody(("value", GuaranteeType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
