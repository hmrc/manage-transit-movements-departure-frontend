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

package controllers.routeDetails.loading

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.UnLocodeFormProvider
import generators.Generators
import models.{NormalMode, UnLocodeList}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.routeDetails.loading.PlaceOfLoadingUnLocodePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UnLocodesService
import views.html.routeDetails.loading.PlaceOfLoadingUnLocodeView

import scala.concurrent.Future

class PlaceOfLoadingUnLocodeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val unLocode1    = arbitraryUnLocode.arbitrary.sample.get
  private val unLocode2    = arbitraryUnLocode.arbitrary.sample.get
  private val unLocodeList = UnLocodeList(Seq(unLocode1, unLocode2))

  private val formProvider = new UnLocodeFormProvider()
  private val form         = formProvider("routeDetails.loading.placeOfLoadingUnLocode", unLocodeList)
  private val mode         = NormalMode

  private val mockUnLocodesService: UnLocodesService = mock[UnLocodesService]
  private lazy val placeOfLoadingUnLocodeRoute       = routes.PlaceOfLoadingUnLocodeController.onPageLoad(lrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[PreTaskListDetails]).toInstance(fakeNavigator))
      .overrides(bind(classOf[UnLocodesService]).toInstance(mockUnLocodesService))

  "PlaceOfLoadingUnLocode Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockUnLocodesService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, placeOfLoadingUnLocodeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[PlaceOfLoadingUnLocodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, unLocodeList.unLocodes, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockUnLocodesService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      val userAnswers = emptyUserAnswers.setValue(PlaceOfLoadingUnLocodePage, unLocode1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, placeOfLoadingUnLocodeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> unLocode1.unLocodeExtendedCode))

      val view = injector.instanceOf[PlaceOfLoadingUnLocodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, lrn, unLocodeList.unLocodes, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockUnLocodesService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, placeOfLoadingUnLocodeRoute)
        .withFormUrlEncodedBody(("value", unLocode1.unLocodeExtendedCode))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockUnLocodesService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, placeOfLoadingUnLocodeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[PlaceOfLoadingUnLocodeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, unLocodeList.unLocodes, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, placeOfLoadingUnLocodeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, placeOfLoadingUnLocodeRoute)
        .withFormUrlEncodedBody(("value", unLocode1.unLocodeExtendedCode))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
