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

package controllers.preTaskList

import base.{AppWithDefaultMockFixtures, SpecBase}
import config.Constants.declarationTypeValues
import controllers.{routes => mainRoutes}
import forms.EnumerableFormProvider
import models.{DeclarationType, NormalMode}
import navigation.PreTaskListNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import pages.preTaskList.DeclarationTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DeclarationTypeService
import views.html.preTaskList.DeclarationTypeView

import scala.concurrent.Future
import generators.Generators

class DeclarationTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val mode                      = NormalMode
  private val dts                       = declarationTypeValues
  private lazy val declarationTypeRoute = routes.DeclarationTypeController.onPageLoad(lrn, mode).url

  private val formProvider           = new EnumerableFormProvider()
  private val form                   = formProvider[DeclarationType]("declarationType")(dts)
  private val mockDeclarationService = mock[DeclarationTypeService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreTaskListNavigatorProvider]).toInstance(fakePreTaskListNavigatorProvider))
      .overrides(bind(classOf[DeclarationTypeService]).toInstance(mockDeclarationService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDeclarationService)

  }

  "DeclarationType Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockDeclarationService.getDeclarationTypeItemLevel(any())(any()))
        .thenReturn(Future.successful(DeclarationType.values(emptyUserAnswers, dts)))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, declarationTypeRoute)
      val view    = injector.instanceOf[DeclarationTypeView]
      val result  = route(app, request).value

      status(result) mustEqual OK

      val res = contentAsString(result)
      val ans = view(form, DeclarationType.values(emptyUserAnswers, dts), lrn, mode)(request, messages).toString

      res mustEqual
        ans
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      when(mockDeclarationService.getDeclarationTypeItemLevel(any())(any()))
        .thenReturn(Future.successful(DeclarationType.values(emptyUserAnswers, dts)))

      val userAnswers = emptyUserAnswers.setValue(DeclarationTypePage, dts.head)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      val ret = status(result)
      ret mustEqual OK

      val filledForm = form.bind(Map("value" -> dts.head.toString))

      val view = injector.instanceOf[DeclarationTypeView]

      val res2 = contentAsString(result)
      val exp2 = view(filledForm, DeclarationType.values(emptyUserAnswers, dts), lrn, mode)(request, messages).toString
      res2 mustEqual
        exp2
    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockDeclarationService.getDeclarationTypeItemLevel(any())(any()))
        .thenReturn(Future.successful(dts))

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val selectedValue = dts.head

      val request = FakeRequest(POST, declarationTypeRoute)
        .withFormUrlEncodedBody(("value", selectedValue.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      when(mockDeclarationService.getDeclarationTypeItemLevel(any())(any()))
        .thenReturn(Future.successful(DeclarationType.values(emptyUserAnswers, dts)))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, declarationTypeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val result    = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[DeclarationTypeView]

      val res = contentAsString(result)
      val ans = view(boundForm, DeclarationType.values(emptyUserAnswers, dts), lrn, mode)(request, messages).toString

      res mustEqual
        ans
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, declarationTypeRoute)
        .withFormUrlEncodedBody(("value", dts.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
