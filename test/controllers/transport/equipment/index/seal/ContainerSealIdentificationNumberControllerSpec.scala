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

package controllers.transport.equipment.index.seal

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.ContainerSealIdentificationNumberFormProvider
import models.NormalMode
import navigation.transport.TransportNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Gen
import pages.transport.equipment.index.ContainerIdentificationNumberPage
import pages.transport.equipment.index.seal.ContainerSealIdentificationNumberPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.equipment.index.seal.ContainerSealIdentificationNumberView

import scala.concurrent.Future

class ContainerSealIdentificationNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val withContainerPrefix    = "transport.equipment.index.seal.containerSealIdentificationNumber.withContainer"
  private val withoutContainerPrefix = "transport.equipment.index.seal.containerSealIdentificationNumber.withoutContainer"

  private val formProvider                                      = new ContainerSealIdentificationNumberFormProvider()
  private val containerNumber: String                           = Gen.alphaNumStr.sample.value
  private def formWithContainer(otherIds: Seq[String] = Nil)    = formProvider(withContainerPrefix, otherIds, containerNumber)
  private def formWithoutContainer(otherIds: Seq[String] = Nil) = formProvider(withoutContainerPrefix, otherIds)

  private val mode = NormalMode

  private lazy val containerSealIdentificationNumberRoute =
    routes.ContainerSealIdentificationNumberController.onPageLoad(lrn, mode, equipmentIndex, sealIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportNavigatorProvider]).toInstance(fakeTransportNavigatorProvider))

  "ContainerSealIdentificationNumber Controller" - {

    "must return OK and the correct view for a GET" - {
      "when using a container" in {

        val userAnswers = emptyUserAnswers
          .setValue(ContainerIdentificationNumberPage(equipmentIndex), containerNumber)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, containerSealIdentificationNumberRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[ContainerSealIdentificationNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(formWithContainer(), lrn, mode, equipmentIndex, sealIndex, withContainerPrefix, containerNumber)(request, messages).toString
      }

      "when not using a container" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, containerSealIdentificationNumberRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[ContainerSealIdentificationNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(formWithoutContainer(), lrn, mode, equipmentIndex, sealIndex, withoutContainerPrefix)(request, messages).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" - {
      "when using a container" in {

        val userAnswers = emptyUserAnswers
          .setValue(ContainerIdentificationNumberPage(equipmentIndex), containerNumber)
          .setValue(ContainerSealIdentificationNumberPage(equipmentIndex, sealIndex), "test")

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, containerSealIdentificationNumberRoute)

        val result = route(app, request).value

        val filledForm = formWithContainer().bind(Map("value" -> "test"))

        val view = injector.instanceOf[ContainerSealIdentificationNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, equipmentIndex, sealIndex, withContainerPrefix, containerNumber)(request, messages).toString
      }

      "when not using a container" in {

        val userAnswers = emptyUserAnswers.setValue(ContainerSealIdentificationNumberPage(equipmentIndex, sealIndex), "test")

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, containerSealIdentificationNumberRoute)

        val result = route(app, request).value

        val filledForm = formWithoutContainer().bind(Map("value" -> "test"))

        val view = injector.instanceOf[ContainerSealIdentificationNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, equipmentIndex, sealIndex, withoutContainerPrefix)(request, messages).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, containerSealIdentificationNumberRoute)
        .withFormUrlEncodedBody(("value", "test"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" - {
      "when using a container" in {
        val userAnswers = emptyUserAnswers
          .setValue(ContainerIdentificationNumberPage(equipmentIndex), containerNumber)

        setExistingUserAnswers(userAnswers)

        val invalidAnswer = ""

        val request    = FakeRequest(POST, containerSealIdentificationNumberRoute).withFormUrlEncodedBody(("value", ""))
        val filledForm = formWithContainer().bind(Map("value" -> invalidAnswer))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST

        val view = injector.instanceOf[ContainerSealIdentificationNumberView]

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, equipmentIndex, sealIndex, withContainerPrefix, containerNumber)(request, messages).toString
      }

      "when not using a container" in {

        setExistingUserAnswers(emptyUserAnswers)

        val invalidAnswer = ""

        val request    = FakeRequest(POST, containerSealIdentificationNumberRoute).withFormUrlEncodedBody(("value", ""))
        val filledForm = formWithoutContainer().bind(Map("value" -> invalidAnswer))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST

        val view = injector.instanceOf[ContainerSealIdentificationNumberView]

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, equipmentIndex, sealIndex, withoutContainerPrefix)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, containerSealIdentificationNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, containerSealIdentificationNumberRoute)
        .withFormUrlEncodedBody(("value", "test"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
