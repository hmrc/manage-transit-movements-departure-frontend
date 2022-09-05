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

package controllers.routeDetails.locationOfGoods

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.CustomsOfficeFormProvider
import generators.Generators
import models.{CustomsOfficeList, NormalMode}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.preTaskList.OfficeOfDeparturePage
import pages.routeDetails.locationOfGoods.LocationOfGoodsCustomsOfficeIdentifierPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CustomsOfficesService
import views.html.routeDetails.locationOfGoods.LocationOfGoodsCustomsOfficeIdentifierView

import scala.concurrent.Future

class LocationOfGoodsCustomsOfficeIdentifierControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val customsOffice1    = arbitraryCustomsOffice.arbitrary.sample.get
  private val customsOffice2    = arbitraryCustomsOffice.arbitrary.sample.get
  private val customsOfficeList = CustomsOfficeList(Seq(customsOffice1, customsOffice2))

  private val formProvider = new CustomsOfficeFormProvider()
  private val form         = formProvider("routeDetails.locationOfGoods.locationOfGoodsCustomsOfficeIdentifier", customsOfficeList)
  private val mode         = NormalMode

  private val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]
  private lazy val locationOfGoodsCustomsOfficeIdentifierRoute = routes.LocationOfGoodsCustomsOfficeIdentifierController.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[PreTaskListDetails]).toInstance(fakeNavigator))
      .overrides(bind(classOf[CustomsOfficesService]).toInstance(mockCustomsOfficesService))

  "LocationOfGoodsCustomsOfficeIdentifier Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.setValue(OfficeOfDeparturePage, customsOffice1)
      when(mockCustomsOfficesService.getCustomsOfficesOfDepartureForCountry(any())(any())).thenReturn(Future.successful(customsOfficeList))
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, locationOfGoodsCustomsOfficeIdentifierRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[LocationOfGoodsCustomsOfficeIdentifierView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, customsOfficeList.customsOffices, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCustomsOfficesService.getCustomsOfficesOfDepartureForCountry(any())(any())).thenReturn(Future.successful(customsOfficeList))
      val userAnswers = emptyUserAnswers
        .setValue(OfficeOfDeparturePage, customsOffice1)
        .setValue(LocationOfGoodsCustomsOfficeIdentifierPage, customsOffice1)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, locationOfGoodsCustomsOfficeIdentifierRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> customsOffice1.id))

      val view = injector.instanceOf[LocationOfGoodsCustomsOfficeIdentifierView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, customsOfficeList.customsOffices, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(OfficeOfDeparturePage, customsOffice1)
      when(mockCustomsOfficesService.getCustomsOfficesOfDepartureForCountry(any())(any())).thenReturn(Future.successful(customsOfficeList))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, locationOfGoodsCustomsOfficeIdentifierRoute)
        .withFormUrlEncodedBody(("value", customsOffice1.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(OfficeOfDeparturePage, customsOffice1)
      when(mockCustomsOfficesService.getCustomsOfficesOfDepartureForCountry(any())(any())).thenReturn(Future.successful(customsOfficeList))
      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, locationOfGoodsCustomsOfficeIdentifierRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[LocationOfGoodsCustomsOfficeIdentifierView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, customsOfficeList.customsOffices, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, locationOfGoodsCustomsOfficeIdentifierRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, locationOfGoodsCustomsOfficeIdentifierRoute)
        .withFormUrlEncodedBody(("value", customsOffice1.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
