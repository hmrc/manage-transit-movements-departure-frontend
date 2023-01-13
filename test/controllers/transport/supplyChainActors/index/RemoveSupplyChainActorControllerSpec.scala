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
import controllers.transport.supplyChainActors.index.{routes => indexRoutes}
import controllers.transport.supplyChainActors.{routes => actorRoutes}
import forms.YesNoFormProvider
import generators.Generators
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.sections.transport.supplyChainActors
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.supplyChainActors.index.RemoveSupplyChainActorView

import scala.concurrent.Future

class RemoveSupplyChainActorControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val form = new YesNoFormProvider()("transport.supplyChainActors.index.removeSupplyChainActor")

  private val mode = NormalMode

  private lazy val removeSupplyChainActorRoute = indexRoutes.RemoveSupplyChainActorController.onPageLoad(lrn, mode, index).url

  "RemoveSupplyChainActor" - {

    "must return OK and the correct view for a GET" in {
      forAll(arbitrarySupplyChainActorAnswers(emptyUserAnswers, index)) {
        answers =>
          setExistingUserAnswers(answers)

          val request = FakeRequest(GET, removeSupplyChainActorRoute)
          val result  = route(app, request).value

          val view = injector.instanceOf[RemoveSupplyChainActorView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form, lrn, mode, index)(request, messages).toString
      }
    }

    "when yes submitted" - {
      "must redirect to add another supply chain actor and remove supply chain actor at specified index" in {
        forAll(arbitrarySupplyChainActorAnswers(emptyUserAnswers, index)) {
          answers =>
            reset(mockSessionRepository)
            when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

            setExistingUserAnswers(answers)

            val request = FakeRequest(POST, removeSupplyChainActorRoute)
              .withFormUrlEncodedBody(("value", "true"))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual
              actorRoutes.AddAnotherSupplyChainActorController.onPageLoad(lrn, mode).url

            val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
            userAnswersCaptor.getValue.get(supplyChainActors.SupplyChainActorSection(index)) mustNot be(defined)
        }
      }
    }

    "when no submitted" - {
      "must redirect to add another supply chain actor and not remove supply chain actor at specified index" in {
        forAll(arbitrarySupplyChainActorAnswers(emptyUserAnswers, index)) {
          answers =>
            reset(mockSessionRepository)

            setExistingUserAnswers(answers)

            val request = FakeRequest(POST, removeSupplyChainActorRoute)
              .withFormUrlEncodedBody(("value", "false"))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual
              actorRoutes.AddAnotherSupplyChainActorController.onPageLoad(lrn, mode).url

            verify(mockSessionRepository, never()).set(any())(any())
        }

      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      forAll(arbitrarySupplyChainActorAnswers(emptyUserAnswers, index)) {
        answers =>
          setExistingUserAnswers(answers)

          val request   = FakeRequest(POST, removeSupplyChainActorRoute).withFormUrlEncodedBody(("value", ""))
          val boundForm = form.bind(Map("value" -> ""))

          val result = route(app, request).value

          status(result) mustEqual BAD_REQUEST

          val view = injector.instanceOf[RemoveSupplyChainActorView]

          contentAsString(result) mustEqual
            view(boundForm, lrn, mode, index)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET" - {
      "when no existing data found" in {
        setNoExistingUserAnswers()

        val request = FakeRequest(GET, removeSupplyChainActorRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
      }
    }

    "must redirect to Session Expired for a POST if no existing data is found" - {
      "when no existing data found" in {
        setNoExistingUserAnswers()

        val request = FakeRequest(POST, removeSupplyChainActorRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
      }
    }
  }
}
