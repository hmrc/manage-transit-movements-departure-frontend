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
import forms.LocationOfGoodsCoordinatesFormProvider
import generators.Generators
import models.NormalMode
import navigation.routeDetails.LocationOfGoodsNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import pages.routeDetails.locationOfGoods.LocationOfGoodsCoordinatesPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CountriesService
import views.html.routeDetails.locationOfGoods.LocationOfGoodsCoordinatesView

import scala.concurrent.Future

class LocationOfGoodsCoordinatesControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val testCoordinates = arbitraryCoordinates.arbitrary.sample.value

  private val formProvider = new LocationOfGoodsCoordinatesFormProvider()
  private val form         = formProvider("routeDetails.locationOfGoods.locationOfGoodsCoordinates")

  private val mode                                 = NormalMode
  private lazy val locationOfGoodsCoordinatesRoute = routes.LocationOfGoodsCoordinatesController.onPageLoad(lrn, mode).url

  private lazy val mockCountriesService: CountriesService = mock[CountriesService]

  override def beforeEach(): Unit = {
    reset(mockCountriesService)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[LocationOfGoodsNavigatorProvider]).toInstance(fakeLocationOfGoodsNavigatorProvider))

  "LocationOfGoodsCoordinates Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, locationOfGoodsCoordinatesRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[LocationOfGoodsCoordinatesView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(LocationOfGoodsCoordinatesPage, testCoordinates)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, locationOfGoodsCoordinatesRoute)

      val result = route(app, request).value

      val filledForm = form.bind(
        Map(
          "latitude"  -> testCoordinates.latitude,
          "longitude" -> testCoordinates.longitude
        )
      )

      val view = injector.instanceOf[LocationOfGoodsCoordinatesView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, locationOfGoodsCoordinatesRoute)
        .withFormUrlEncodedBody(
          ("latitude", testCoordinates.latitude),
          ("longitude", testCoordinates.longitude)
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, locationOfGoodsCoordinatesRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[LocationOfGoodsCoordinatesView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, locationOfGoodsCoordinatesRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, locationOfGoodsCoordinatesRoute)
        .withFormUrlEncodedBody(
          ("latitude", testCoordinates.latitude),
          ("longitude", testCoordinates.longitude)
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}