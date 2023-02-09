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

package controllers.transport.equipment

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.EnumerableFormProvider
import models.NormalMode
import models.transport.equipment.PaymentMethod
import navigation.transport.TransportNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.transport.equipment.PaymentMethodPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.equipment.PaymentMethodView

import scala.concurrent.Future

class PaymentMethodControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider            = new EnumerableFormProvider()
  private val form                    = formProvider[PaymentMethod]("transport.equipment.paymentMethod")
  private val mode                    = NormalMode
  private lazy val paymentMethodRoute = routes.PaymentMethodController.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportNavigatorProvider]).toInstance(fakeTransportNavigatorProvider))

  "PaymentMethod Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, paymentMethodRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[PaymentMethodView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, PaymentMethod.radioItems, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(PaymentMethodPage, PaymentMethod.values.head)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, paymentMethodRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> PaymentMethod.values.head.toString))

      val view = injector.instanceOf[PaymentMethodView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, PaymentMethod.radioItems, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, paymentMethodRoute)
        .withFormUrlEncodedBody(("value", PaymentMethod.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, paymentMethodRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[PaymentMethodView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, PaymentMethod.radioItems, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, paymentMethodRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, paymentMethodRoute)
        .withFormUrlEncodedBody(("value", PaymentMethod.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
