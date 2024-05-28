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
import models.reference.AdditionalDeclarationType
import navigation.PreTaskListNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import pages.preTaskList.AdditionalDeclarationTypePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AdditionalDeclarationTypesService
import views.html.preTaskList.AdditionalDeclarationTypeView

import scala.concurrent.Future

class AdditionalDeclarationTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val adts = arbitrary[Seq[AdditionalDeclarationType]].sample.value
  private val adt1 = adts.head

  private val formProvider = new EnumerableFormProvider()
  private val form         = formProvider[AdditionalDeclarationType]("additionalDeclarationType", adts)
  private val mode         = NormalMode

  private lazy val additionalDeclarationTypeRoute = routes.AdditionalDeclarationTypeController.onPageLoad(lrn, mode).url

  private val mockAdditionalDeclarationTypesService: AdditionalDeclarationTypesService = mock[AdditionalDeclarationTypesService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreTaskListNavigatorProvider]).toInstance(fakePreTaskListNavigatorProvider))
      .overrides(bind(classOf[AdditionalDeclarationTypesService]).toInstance(mockAdditionalDeclarationTypesService))
      .configure("features.isPreLodgeEnabled" -> true)

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAdditionalDeclarationTypesService)
    when(mockAdditionalDeclarationTypesService.getAdditionalDeclarationTypes()(any())).thenReturn(Future.successful(adts))
  }

  "AdditionalDeclarationType Controller when preLodge is true" - {
    val app = super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreTaskListNavigatorProvider]).toInstance(fakePreTaskListNavigatorProvider))
      .overrides(bind(classOf[AdditionalDeclarationTypesService]).toInstance(mockAdditionalDeclarationTypesService))
      .configure("features.isPreLodgeEnabled" -> true)
      .build()

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, additionalDeclarationTypeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[AdditionalDeclarationTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, adts, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(AdditionalDeclarationTypePage, adt1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, additionalDeclarationTypeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> adt1.code))

      val view = injector.instanceOf[AdditionalDeclarationTypeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, adts, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {
      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, additionalDeclarationTypeRoute)
        .withFormUrlEncodedBody(("value", adt1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, additionalDeclarationTypeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = app.injector.instanceOf[AdditionalDeclarationTypeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, adts, mode)(request, messages).toString

    }

    "must redirect to Technical Difficulties for a GET if no existing data is found" in {

      setNoExistingUserAnswers()
      val request = FakeRequest(GET, additionalDeclarationTypeRoute)
      val result  = route(app, request).value
      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(lrn).url

    }

    "must redirect to Technical Difficulties for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, additionalDeclarationTypeRoute)
        .withFormUrlEncodedBody(("value", adt1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(lrn).url
    }
  }

  "AdditionalDeclarationType Controller when preLodge is false" - {
    val app = super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreTaskListNavigatorProvider]).toInstance(fakePreTaskListNavigatorProvider))
      .overrides(bind(classOf[AdditionalDeclarationTypesService]).toInstance(mockAdditionalDeclarationTypesService))
      .configure("features.isPreLodgeEnabled" -> false)
      .build()

    "must redirect to the standard declaration page when preLodge is false" in {
      running(app) {
        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, additionalDeclarationTypeRoute)
          .withFormUrlEncodedBody(("value", adt1.code))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.preTaskList.routes.StandardDeclarationController.onPageLoad(lrn).url
      }

    }

  }
}
