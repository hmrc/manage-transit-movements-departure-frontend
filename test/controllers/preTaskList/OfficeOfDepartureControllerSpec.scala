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

package controllers.preTaskList

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoutes}
import forms.CustomsOfficeFormProvider
import models.reference.CustomsOffice
import models.{CustomsOfficeList, NormalMode}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import pages.preTaskList.OfficeOfDeparturePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CustomsOfficesService
import views.html.preTaskList.OfficeOfDepartureView

import scala.concurrent.Future

class OfficeOfDepartureControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val customsOffice1: CustomsOffice     = CustomsOffice("GB1", "someName", None)
  private val customsOffice2: CustomsOffice     = CustomsOffice("GB2", "name", None)
  private val customsOffices: CustomsOfficeList = CustomsOfficeList(Seq(customsOffice1, customsOffice2))

  private val gbForm = new CustomsOfficeFormProvider()("officeOfDeparture", customsOffices)
  private val mode   = NormalMode

  private val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]
  private lazy val officeOfDepartureRoute: String              = routes.OfficeOfDepartureController.onPageLoad(lrn, mode).url

  override def beforeEach(): Unit = {
    reset(mockCustomsOfficesService)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[PreTaskListDetails]).toInstance(fakeNavigator))
      .overrides(bind(classOf[CustomsOfficesService]).toInstance(mockCustomsOfficesService))

  "OfficeOfDeparture Controller" - {

    "must return OK and the correct view for a GET" in {
      setExistingUserAnswers(emptyUserAnswers)
      when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))

      val request = FakeRequest(GET, officeOfDepartureRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[OfficeOfDepartureView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(gbForm, lrn, customsOffices.customsOffices, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.setValue(OfficeOfDeparturePage, customsOffice1)
      setExistingUserAnswers(userAnswers)

      when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))

      val request = FakeRequest(GET, officeOfDepartureRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[OfficeOfDepartureView]

      status(result) mustEqual OK

      val filledForm = gbForm.bind(Map("value" -> "GB1"))

      contentAsString(result) mustEqual
        view(filledForm, lrn, customsOffices.customsOffices, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {
      setExistingUserAnswers(emptyUserAnswers)
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))

      val request = FakeRequest(POST, officeOfDepartureRoute)
        .withFormUrlEncodedBody(("value", "GB1"))

      val result: Future[Result] = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setExistingUserAnswers(emptyUserAnswers)
      when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))

      val request   = FakeRequest(POST, officeOfDepartureRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = gbForm.bind(Map("value" -> ""))

      val result = route(app, request).value

      val view = injector.instanceOf[OfficeOfDepartureView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, customsOffices.customsOffices, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, officeOfDepartureRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, officeOfDepartureRoute)
        .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
