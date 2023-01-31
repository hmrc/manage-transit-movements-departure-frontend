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

package controllers.transport.equipment.index.seals

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.Generators
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify}
import pages.sections
import pages.sections.SealSection
import pages.transport.equipment.index.seals.IdentificationNumberPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.equipment.index.seals.RemoveSealYesNoView

class RemoveSealYesNoControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val sealIdNumber              = nonEmptyString.sample.value
  private val formProvider              = new YesNoFormProvider()
  private val form                      = formProvider("transport.equipment.index.seals.removeSealYesNo", sealIdNumber)
  private val mode                      = NormalMode
  private lazy val removeSealYesNoRoute = routes.RemoveSealYesNoController.onPageLoad(lrn, mode, equipmentIndex, sealIndex).url

  "RemoveSealYesNo Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.setValue(IdentificationNumberPage(equipmentIndex, sealIndex), sealIdNumber)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, removeSealYesNoRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemoveSealYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, equipmentIndex, sealIndex, sealIdNumber)(request, messages).toString
    }

    "must redirect to the next page" - {
      "when yes is submitted" in {
        val userAnswers = emptyUserAnswers.setValue(IdentificationNumberPage(equipmentIndex, sealIndex), sealIdNumber)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeSealYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "#"

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.get(sections.SealSection(equipmentIndex, sealIndex)) mustNot be(defined)
      }

      "when no is submitted" in {
        val userAnswers = emptyUserAnswers.setValue(IdentificationNumberPage(equipmentIndex, sealIndex), sealIdNumber)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, removeSealYesNoRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "#"

        verify(mockSessionRepository, never()).set(any())(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, removeSealYesNoRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[RemoveSealYesNoView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, equipmentIndex, sealIndex, sealIdNumber)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, removeSealYesNoRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, removeSealYesNoRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
