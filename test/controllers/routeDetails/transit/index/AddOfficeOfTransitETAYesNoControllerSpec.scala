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

package controllers.routeDetails.transit.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.Generators
import models.NormalMode
import navigation.routeDetails.OfficeOfTransitNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.routeDetails.transit.index.{AddOfficeOfTransitETAYesNoPage, OfficeOfTransitPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.routeDetails.transit.index.AddOfficeOfTransitETAYesNoView

import scala.concurrent.Future

class AddOfficeOfTransitETAYesNoControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar with Generators {

  private val formProvider                         = new YesNoFormProvider()
  private val officeOfTransit                      = arbitraryCustomsOffice.arbitrary.sample.get
  private val form                                 = formProvider("routeDetails.transit.addOfficeOfTransitETAYesNo")
  private val mode                                 = NormalMode
  private lazy val addOfficeOfTransitETAYesNoRoute = routes.AddOfficeOfTransitETAYesNoController.onPageLoad(lrn, mode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[OfficeOfTransitNavigatorProvider]).toInstance(fakeOfficeOfTransitNavigatorProvider))

  "AddOfficeOfTransitETAYesNo Controller" - {

    "must return OK and the correct view for a GET" in {

      val updatedUserAnswers = emptyUserAnswers.setValue(OfficeOfTransitPage(index), officeOfTransit)

      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(GET, addOfficeOfTransitETAYesNoRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[AddOfficeOfTransitETAYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode, officeOfTransit, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(OfficeOfTransitPage(index), officeOfTransit)
        .setValue(AddOfficeOfTransitETAYesNoPage(index), true)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, addOfficeOfTransitETAYesNoRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "true"))

      val view = injector.instanceOf[AddOfficeOfTransitETAYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode, officeOfTransit, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val updatedUserAnswers = emptyUserAnswers.setValue(OfficeOfTransitPage(index), officeOfTransit)

      setExistingUserAnswers(updatedUserAnswers)

      val request = FakeRequest(POST, addOfficeOfTransitETAYesNoRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val updatedUserAnswers = emptyUserAnswers.setValue(OfficeOfTransitPage(index), officeOfTransit)
      setExistingUserAnswers(updatedUserAnswers)

      val request   = FakeRequest(POST, addOfficeOfTransitETAYesNoRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[AddOfficeOfTransitETAYesNoView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode, officeOfTransit, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addOfficeOfTransitETAYesNoRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addOfficeOfTransitETAYesNoRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}