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

package controllers.transport.transportMeans.active

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.CustomsOfficeFormProvider
import generators.Generators
import models.reference.CustomsOffice
import models.{CustomsOfficeList, NormalMode}
import navigation.transport.TransportMeansActiveNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.transport.transportMeans.active.CustomsOfficeActiveBorderPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CustomsOfficesService
import views.html.transport.transportMeans.active.CustomsOfficeActiveBorderView

import scala.concurrent.Future

class CustomsOfficeActiveBorderControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val exitOffice        = arbitrary[CustomsOffice].sample.value
  private val transitOffice     = arbitrary[CustomsOffice].sample.value
  private val destinationOffice = arbitrary[CustomsOffice].sample.value

  private val allCustomOfficesList = CustomsOfficeList(List(exitOffice, transitOffice, destinationOffice))

  private val formProvider                                     = new CustomsOfficeFormProvider()
  private val form                                             = formProvider("transport.transportMeans.active.customsOfficeActiveBorder", allCustomOfficesList)
  private val mode                                             = NormalMode
  private val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]

  private lazy val customsOfficeActiveBorderRoute = routes.CustomsOfficeActiveBorderController.onPageLoad(lrn, mode, activeIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[TransportMeansActiveNavigatorProvider]).toInstance(fakeTransportMeansActiveNavigatorProvider))
      .overrides(bind(classOf[CustomsOfficesService]).toInstance(mockCustomsOfficesService))

  "ActiveBorderOfficeTransit Controller" - {

    "must return OK and the correct view for a GET" in {
      when(
        mockCustomsOfficesService.getCustomsOffices(any())
      ).thenReturn(allCustomOfficesList)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CustomsOfficeActiveBorderView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, allCustomOfficesList, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(
        mockCustomsOfficesService.getCustomsOffices(any())
      ).thenReturn(allCustomOfficesList)

      val userAnswers = emptyUserAnswers
        .setValue(CustomsOfficeActiveBorderPage(index), destinationOffice)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> destinationOffice.id))

      val view = injector.instanceOf[CustomsOfficeActiveBorderView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, allCustomOfficesList, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(
        mockCustomsOfficesService.getCustomsOffices(any())
      ).thenReturn(allCustomOfficesList)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, customsOfficeActiveBorderRoute)
        .withFormUrlEncodedBody(("value", destinationOffice.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(
        mockCustomsOfficesService.getCustomsOffices(any())
      ).thenReturn(allCustomOfficesList)

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, customsOfficeActiveBorderRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[CustomsOfficeActiveBorderView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, allCustomOfficesList, mode, index)(request, messages).toString
    }
  }

  "must redirect to Session Expired for a GET if no existing data is found" in {

    setNoExistingUserAnswers()

    val request = FakeRequest(GET, customsOfficeActiveBorderRoute)

    val result = route(app, request).value

    status(result) mustEqual SEE_OTHER
    redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
  }

  "must redirect to Session Expired for a POST if no existing data is found" in {

    setNoExistingUserAnswers()

    val request = FakeRequest(POST, customsOfficeActiveBorderRoute)
      .withFormUrlEncodedBody(("value", destinationOffice.id))

    val result = route(app, request).value

    status(result) mustEqual SEE_OTHER

    redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
  }

}
