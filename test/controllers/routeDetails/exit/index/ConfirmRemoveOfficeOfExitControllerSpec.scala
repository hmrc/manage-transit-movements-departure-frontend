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

package controllers.routeDetails.exit.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import models.UserAnswers
import models.reference.CustomsOffice
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.exit.index.OfficeOfExitPage
import pages.sections.routeDetails.exit.OfficeOfExitSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.routeDetails.exit.index.ConfirmRemoveOfficeOfExitView

import scala.concurrent.Future

class ConfirmRemoveOfficeOfExitControllerSpec
    extends SpecBase
    with AppWithDefaultMockFixtures
    with Generators
    with RouteDetailsUserAnswersGenerator
    with ScalaCheckPropertyChecks {

  private val formProvider                       = new YesNoFormProvider()
  private def form(customsOffice: CustomsOffice) = formProvider("routeDetails.officeOfExit.confirmRemoveOfficeOfExit", customsOffice.name)

  private lazy val confirmRemoveOfficeOfExitRoute = routes.ConfirmRemoveOfficeOfExitController.onPageLoad(lrn, index).url

  "ConfirmRemoveOfficeOfExit Controller" - {

    "must return OK and the correct view for a GET" in {
      forAll(arbitraryOfficeOfExitAnswers(emptyUserAnswers, index)) {
        answers =>
          setExistingUserAnswers(answers)
          val customsOffice = answers.getValue(OfficeOfExitPage(index))

          val request = FakeRequest(GET, confirmRemoveOfficeOfExitRoute)
          val result  = route(app, request).value

          val view = injector.instanceOf[ConfirmRemoveOfficeOfExitView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form(customsOffice), lrn, index, customsOffice.name)(request, messages).toString
      }
    }

    "when yes submitted" - {
      "must redirect to add another office of exit and remove office of exit at specified index" in {
        forAll(arbitraryOfficeOfExitAnswers(emptyUserAnswers, index)) {
          answers =>
            reset(mockSessionRepository)
            when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

            setExistingUserAnswers(answers)

            val request = FakeRequest(POST, confirmRemoveOfficeOfExitRoute)
              .withFormUrlEncodedBody(("value", "true"))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual
              controllers.routeDetails.exit.routes.AddAnotherOfficeOfExitController.onPageLoad(lrn).url

            val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(mockSessionRepository).set(userAnswersCaptor.capture())
            userAnswersCaptor.getValue.get(OfficeOfExitSection(index)) mustNot be(defined)
        }
      }
    }

    "when no submitted" - {
      "must redirect to add another office of exit and not remove office of exit at specified index" in {
        forAll(arbitraryOfficeOfExitAnswers(emptyUserAnswers, index)) {
          answers =>
            reset(mockSessionRepository)

            setExistingUserAnswers(answers)

            val request = FakeRequest(POST, confirmRemoveOfficeOfExitRoute)
              .withFormUrlEncodedBody(("value", "false"))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual
              controllers.routeDetails.exit.routes.AddAnotherOfficeOfExitController.onPageLoad(lrn).url

            verify(mockSessionRepository, never()).set(any())
        }
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      forAll(arbitraryOfficeOfExitAnswers(emptyUserAnswers, index)) {
        answers =>
          setExistingUserAnswers(answers)
          val customsOffice = answers.getValue(OfficeOfExitPage(index))

          val request   = FakeRequest(POST, confirmRemoveOfficeOfExitRoute).withFormUrlEncodedBody(("value", ""))
          val boundForm = form(customsOffice).bind(Map("value" -> ""))

          val result = route(app, request).value

          status(result) mustEqual BAD_REQUEST

          val view = injector.instanceOf[ConfirmRemoveOfficeOfExitView]

          contentAsString(result) mustEqual
            view(boundForm, lrn, index, customsOffice.name)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, confirmRemoveOfficeOfExitRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, confirmRemoveOfficeOfExitRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
