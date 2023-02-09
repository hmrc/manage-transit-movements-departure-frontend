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

package controllers.transport.equipment.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.Generators
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import pages.sections.transport.equipment.EquipmentSection
import pages.transport.equipment.index.ContainerIdentificationNumberPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.equipment.index.RemoveTransportEquipmentView

import scala.concurrent.Future

class RemoveTransportEquipmentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val containerIdNumber                  = nonEmptyString.sample.value
  private val equipmentIdNumber                  = equipmentIndex.display
  private val formProvider                       = new YesNoFormProvider()
  private val form                               = formProvider("transport.equipment.index.removeTransportEquipment", equipmentIdNumber)
  private val mode                               = NormalMode
  private lazy val removeTransportEquipmentRoute = routes.RemoveTransportEquipmentController.onPageLoad(lrn, mode, equipmentIndex).url

  "RemoveTransportEquipment Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, removeTransportEquipmentRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemoveTransportEquipmentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, equipmentIndex)(request, messages).toString
    }

    "must redirect to the next page" - {
      "when yes is submitted" in {

        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

        val userAnswers = emptyUserAnswers.setValue(ContainerIdentificationNumberPage(equipmentIndex), containerIdNumber)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeTransportEquipmentRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "#" //TODO: update to add another page

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.get(EquipmentSection(equipmentIndex)) mustNot be(defined)

      }

      "when no is submitted" in {

        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

        val userAnswers = emptyUserAnswers.setValue(ContainerIdentificationNumberPage(equipmentIndex), containerIdNumber)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeTransportEquipmentRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "#" //TODO: update to add another page

        verify(mockSessionRepository, never()).set(any())(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, removeTransportEquipmentRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[RemoveTransportEquipmentView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, equipmentIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, removeTransportEquipmentRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, removeTransportEquipmentRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
