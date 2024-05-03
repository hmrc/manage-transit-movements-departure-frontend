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
import models.reference.{CustomsOffice, DeclarationType}
import models.{NormalMode, ProcedureType}
import navigation.PreTaskListNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import pages.preTaskList.{DeclarationTypePage, OfficeOfDeparturePage, ProcedureTypePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DeclarationTypesService
import views.html.preTaskList.DeclarationTypeView

import scala.concurrent.Future

class DeclarationTypeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private lazy val declarationTypeRoute = routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url

  private val dts = arbitrary[Seq[DeclarationType]].sample.value
  private val dt1 = dts.head

  private val formProvider = new EnumerableFormProvider()
  private val form         = formProvider[DeclarationType]("declarationType", dts)
  private val mode         = NormalMode

  private val mockDeclarationTypesService: DeclarationTypesService = mock[DeclarationTypesService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreTaskListNavigatorProvider]).toInstance(fakePreTaskListNavigatorProvider))
      .overrides(bind(classOf[DeclarationTypesService]).toInstance(mockDeclarationTypesService))

  private val officeOfDeparture = arbitrary[CustomsOffice].sample.value
  private val procedureType     = arbitrary[ProcedureType].sample.value

  private val baseAnswers = emptyUserAnswers
    .setValue(OfficeOfDeparturePage, officeOfDeparture)
    .setValue(ProcedureTypePage, procedureType)

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDeclarationTypesService)
    when(mockDeclarationTypesService.getDeclarationTypes(any(), any())(any())).thenReturn(Future.successful(dts))
  }

  "DeclarationType Controller" - {

    "must return OK and the correct view for a GET" in {
      setExistingUserAnswers(baseAnswers)

      val request = FakeRequest(GET, declarationTypeRoute)
      val view    = injector.instanceOf[DeclarationTypeView]
      val result  = route(app, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, dts, lrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = baseAnswers.setValue(DeclarationTypePage, dt1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual OK

      val filledForm = form.bind(Map("value" -> dt1.code))

      val view = injector.instanceOf[DeclarationTypeView]

      contentAsString(result) mustEqual
        view(filledForm, dts, lrn, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {
      setExistingUserAnswers(baseAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, declarationTypeRoute)
        .withFormUrlEncodedBody(("value", dt1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setExistingUserAnswers(baseAnswers)

      val request   = FakeRequest(POST, declarationTypeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val result    = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[DeclarationTypeView]

      contentAsString(result) mustEqual
        view(boundForm, dts, lrn, mode)(request, messages).toString
    }

    "must redirect to Technical Difficulties for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(lrn).url
    }

    "must redirect to Technical Difficulties for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, declarationTypeRoute)
        .withFormUrlEncodedBody(("value", dt1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(lrn).url
    }
  }
}
