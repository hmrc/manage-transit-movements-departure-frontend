/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.preTaskList

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.EnumerableFormProvider
import models.{NormalMode, ProcedureType}
import navigation.PreTaskListNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.preTaskList.ProcedureTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.preTaskList.ProcedureTypeView

import scala.concurrent.Future

class ProcedureTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val mode = NormalMode

  private lazy val procedureTypeRoute =
    routes.ProcedureTypeController.onPageLoad(lrn, mode).url

  private val formProvider = new EnumerableFormProvider()
  private val form         = formProvider[ProcedureType]("procedureType")
  private val validAnswer  = ProcedureType.values.head

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreTaskListNavigatorProvider]).toInstance(fakePreTaskListNavigatorProvider))

  "ProcedureType Controller" - {

    "must return OK and the correct view for a GET" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, procedureTypeRoute)

      val view = injector.instanceOf[ProcedureTypeView]

      val result = route(app, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, ProcedureType.values, lrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.setValue(ProcedureTypePage, validAnswer)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, procedureTypeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ProcedureTypeView]

      status(result) mustEqual OK

      val filledForm = form.bind(Map("value" -> ProcedureType.values.head.toString))

      contentAsString(result) mustEqual
        view(filledForm, ProcedureType.values, lrn, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {
      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      val request = FakeRequest(POST, procedureTypeRoute)
        .withFormUrlEncodedBody(("value", ProcedureType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, procedureTypeRoute)
        .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[ProcedureTypeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, ProcedureType.values, lrn, mode)(request, messages).toString
    }

    "must redirect to Technical Difficulties for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, procedureTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(lrn).url
    }

    "must redirect to Technical Difficulties for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, procedureTypeRoute)
        .withFormUrlEncodedBody(("value", ProcedureType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(lrn).url
    }
  }
}
