/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import commonTestUtils.UserAnswersSpecHelper
import controllers.{routes => mainRoutes}
import forms.DeclarationTypeFormProvider
import matchers.JsonMatchers
import models.ProcedureType.Normal
import models.reference.{CountryCode, CustomsOffice}
import models.{DeclarationType, DeclarationTypeViewModel, NormalMode}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.{DeclarationTypePage, OfficeOfDeparturePage, ProcedureTypePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.viewmodels.NunjucksSupport
import views.html.DeclarationTypeView

import scala.concurrent.Future

class DeclarationTypeControllerSpec
    extends SpecBase
    with AppWithDefaultMockFixtures
    with MockitoSugar
    with NunjucksSupport
    with JsonMatchers
    with UserAnswersSpecHelper {

  lazy val declarationTypeRoute = routes.DeclarationTypeController.onPageLoad(lrn, NormalMode).url

  val formProvider    = new DeclarationTypeFormProvider()
  val form            = formProvider()
  val gbCustomsOffice = CustomsOffice("Id", "Name", CountryCode("GB"), None)
  private val mode    = NormalMode

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[PreTaskListDetails]).toInstance(fakeNavigator))

  "DeclarationType Controller" - {

    "must return OK and the correct view for a GET" in {
      setUserAnswers(Some(emptyUserAnswers))

      val request = FakeRequest(GET, declarationTypeRoute)
      val view    = injector.instanceOf[DeclarationTypeView]
      val result  = route(app, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, DeclarationTypeViewModel(emptyUserAnswers).radioItems, lrn, mode)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(DeclarationTypePage, DeclarationType.values.head).success.value
      setUserAnswers(Some(userAnswers))

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual OK

      val filledForm = form.bind(Map("value" -> DeclarationType.values.head.toString))

      val view = injector.instanceOf[DeclarationTypeView]

      contentAsString(result) mustEqual
        view(filledForm, DeclarationTypeViewModel(emptyUserAnswers).radioItems, lrn, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      setUserAnswers(Some(emptyUserAnswers))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val selectedValue = DeclarationTypeViewModel(emptyUserAnswers).values.head

      val request =
        FakeRequest(POST, declarationTypeRoute)
          .withFormUrlEncodedBody(("value", selectedValue.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setUserAnswers(Some(emptyUserAnswers))

      val request   = FakeRequest(POST, declarationTypeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))
      val result    = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[DeclarationTypeView]

      contentAsString(result) mustEqual
        view(boundForm, DeclarationTypeViewModel(emptyUserAnswers).radioItems, lrn, mode)(request, messages).toString

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setUserAnswers(None)

      val request = FakeRequest(GET, declarationTypeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setUserAnswers(None)

      val request =
        FakeRequest(POST, declarationTypeRoute)
          .withFormUrlEncodedBody(("value", DeclarationType.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url

    }
  }
}
