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

package controllers.transport.supplyChainActors.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.transport.supplyChainActors.index.routes
import forms.EnumerableFormProvider
import models.NormalMode
import models.transport.supplyChainActors.SupplyChainActorType
import navigation.transport.TransportNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.transport.supplyChainActors.index.SupplyChainActorTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.supplyChainActors.index.SupplyChainActorTypeView

import scala.concurrent.Future

class SupplyChainActorTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                   = new EnumerableFormProvider()
  private val form                           = formProvider[SupplyChainActorType]("transport.supplyChainActors.index.supplyChainActorType")
  private val mode                           = NormalMode
  private lazy val supplyChainActorTypeRoute = routes.SupplyChainActorTypeController.onPageLoad(lrn, mode, actorIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportNavigatorProvider]).toInstance(fakeTransportNavigatorProvider)) //TODO: Switch to correct navigator when created

  "SupplyChainActorType Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, supplyChainActorTypeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[SupplyChainActorTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, SupplyChainActorType.radioItems, mode, actorIndex)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(SupplyChainActorTypePage(actorIndex), SupplyChainActorType.values.head)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, supplyChainActorTypeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> SupplyChainActorType.values.head.toString))

      val view = injector.instanceOf[SupplyChainActorTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, SupplyChainActorType.radioItems, mode, actorIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, supplyChainActorTypeRoute)
        .withFormUrlEncodedBody(("value", SupplyChainActorType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, supplyChainActorTypeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[SupplyChainActorTypeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, SupplyChainActorType.radioItems, mode, actorIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, supplyChainActorTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, supplyChainActorTypeRoute)
        .withFormUrlEncodedBody(("value", SupplyChainActorType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
