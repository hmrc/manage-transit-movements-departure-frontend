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

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.traderDetails.representative.RepresentativeCapacityFormProvider
import models.NormalMode
import models.traderDetails.representative.RepresentativeCapacity
import navigation.Navigator
import navigation.annotations.traderDetails.Representative
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.traderDetails.representative.CapacityPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.traderDetails.representative.CapacityView

import scala.concurrent.Future

class CapacityControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private lazy val representativeCapacityRoute = routes.CapacityController.onPageLoad(lrn, mode).url

  private val formProvider = new RepresentativeCapacityFormProvider()
  private val form         = formProvider()
  private val mode         = NormalMode

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[Representative]).toInstance(fakeNavigator))

  "RepresentativeCapacity Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, representativeCapacityRoute)
      val view    = injector.instanceOf[CapacityView]
      val result  = route(app, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, RepresentativeCapacity.radioItems, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(CapacityPage, RepresentativeCapacity.values.head)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, representativeCapacityRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> RepresentativeCapacity.values.head.toString))

      val view = injector.instanceOf[CapacityView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, RepresentativeCapacity.radioItems, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, representativeCapacityRoute)
          .withFormUrlEncodedBody(("value", RepresentativeCapacity.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, representativeCapacityRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[CapacityView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, RepresentativeCapacity.radioItems, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, representativeCapacityRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, representativeCapacityRoute)
          .withFormUrlEncodedBody(("value", RepresentativeCapacity.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
