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
import forms.EnumerableFormProvider
import generators.Generators
import models.NormalMode
import models.reference.SecurityType
import navigation.PreTaskListNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import pages.preTaskList.SecurityDetailsTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SecurityTypesService
import views.html.preTaskList.SecurityDetailsTypeView

import scala.concurrent.Future

class SecurityDetailsTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val sts = arbitrary[Seq[SecurityType]].sample.value
  private val st1 = sts.head

  private val formProvider                  = new EnumerableFormProvider()
  private val form                          = formProvider[SecurityType]("securityDetailsType", sts)
  private val mode                          = NormalMode
  private lazy val securityDetailsTypeRoute = routes.SecurityDetailsTypeController.onPageLoad(lrn, mode).url

  private val mockSecurityTypesService: SecurityTypesService = mock[SecurityTypesService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreTaskListNavigatorProvider]).toInstance(fakePreTaskListNavigatorProvider))
      .overrides(bind(classOf[SecurityTypesService]).toInstance(mockSecurityTypesService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSecurityTypesService)
    when(mockSecurityTypesService.getSecurityTypes()(any())).thenReturn(Future.successful(sts))
  }

  "SecurityDetailsType Controller" - {

    "must return OK and the correct view for a GET" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, securityDetailsTypeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[SecurityDetailsTypeView]
      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, sts, lrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.setValue(SecurityDetailsTypePage, st1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, securityDetailsTypeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> st1.code))

      val view = injector.instanceOf[SecurityDetailsTypeView]
      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, sts, lrn, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {
      setExistingUserAnswers(emptyUserAnswers)
      when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

      val request = FakeRequest(POST, securityDetailsTypeRoute)
        .withFormUrlEncodedBody(("value", st1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, securityDetailsTypeRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[SecurityDetailsTypeView]

      contentAsString(result) mustEqual
        view(boundForm, sts, lrn, mode)(request, messages).toString
    }

    "must redirect to Technical Difficulties for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, securityDetailsTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.ErrorController.technicalDifficulties().url
    }

    "must redirect to Technical Difficulties for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, securityDetailsTypeRoute)
        .withFormUrlEncodedBody(("value", st1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.ErrorController.technicalDifficulties().url
    }
  }
}
