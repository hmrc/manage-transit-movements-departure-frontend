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
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import models.reference.CustomsOffice
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.transit.index.OfficeOfTransitPage
import pages.sections.routeDetails.transit.OfficeOfTransitSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.routeDetails.transit.index.ConfirmRemoveOfficeOfTransitView

import scala.concurrent.Future

class ConfirmRemoveOfficeOfTransitControllerSpec
    extends SpecBase
    with AppWithDefaultMockFixtures
    with Generators
    with RouteDetailsUserAnswersGenerator
    with ScalaCheckPropertyChecks {

  private val formProvider                       = new YesNoFormProvider()
  private def form(customsOffice: CustomsOffice) = formProvider("routeDetails.transit.index.confirmRemoveOfficeOfTransit", customsOffice.name)

  private val mode = NormalMode

  private lazy val confirmRemoveOfficeOfTransitRoute = routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, mode, index).url

  "ConfirmRemoveOfficeOfTransit Controller" - {

    "must return OK and the correct view for a GET" in {
      forAll(arbitraryOfficeOfTransitAnswers(emptyUserAnswers, index)) {
        answers =>
          setExistingUserAnswers(answers)
          val customsOffice = answers.getValue(OfficeOfTransitPage(index))

          val request = FakeRequest(GET, confirmRemoveOfficeOfTransitRoute)
          val result  = route(app, request).value

          val view = injector.instanceOf[ConfirmRemoveOfficeOfTransitView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(customsOffice), lrn, mode, index, customsOffice.name)(request, messages).toString
      }
    }

    "when yes submitted" - {
      "must redirect to add another office of transit and remove office of transit at specified index" in {
        forAll(arbitraryOfficeOfTransitAnswers(emptyUserAnswers, index)) {
          answers =>
            reset(mockSessionRepository)
            when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

            setExistingUserAnswers(answers)

            val request = FakeRequest(POST, confirmRemoveOfficeOfTransitRoute)
              .withFormUrlEncodedBody(("value", "true"))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual
              controllers.routeDetails.transit.routes.AddAnotherOfficeOfTransitController.onPageLoad(lrn, mode).url

            val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
            userAnswersCaptor.getValue.get(OfficeOfTransitSection(index)) mustNot be(defined)
        }
      }
    }

    "when no submitted" - {
      "must redirect to add another office of transit and not remove office of transit at specified index" in {
        forAll(arbitraryOfficeOfTransitAnswers(emptyUserAnswers, index)) {
          answers =>
            reset(mockSessionRepository)

            setExistingUserAnswers(answers)

            val request = FakeRequest(POST, confirmRemoveOfficeOfTransitRoute)
              .withFormUrlEncodedBody(("value", "false"))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual
              controllers.routeDetails.transit.routes.AddAnotherOfficeOfTransitController.onPageLoad(lrn, mode).url

            verify(mockSessionRepository, never()).set(any())(any())
        }
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      forAll(arbitraryOfficeOfTransitAnswers(emptyUserAnswers, index)) {
        answers =>
          setExistingUserAnswers(answers)
          val customsOffice = answers.getValue(OfficeOfTransitPage(index))

          setExistingUserAnswers(answers)

          val request   = FakeRequest(POST, confirmRemoveOfficeOfTransitRoute).withFormUrlEncodedBody(("value", ""))
          val boundForm = form(customsOffice).bind(Map("value" -> ""))

          val result = route(app, request).value

          status(result) mustEqual BAD_REQUEST

          val view = injector.instanceOf[ConfirmRemoveOfficeOfTransitView]

          contentAsString(result) mustEqual
            view(boundForm, lrn, mode, index, customsOffice.name)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, confirmRemoveOfficeOfTransitRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, confirmRemoveOfficeOfTransitRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
