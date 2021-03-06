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

package controllers.routeDetails.routing

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import models.UserAnswers
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.sections.routeDetails.CountryOfRoutingSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.routeDetails.routing.RemoveCountryOfRoutingYesNoView

import scala.concurrent.Future

class RemoveCountryOfRoutingYesNoControllerSpec
    extends SpecBase
    with AppWithDefaultMockFixtures
    with ScalaCheckPropertyChecks
    with Generators
    with RouteDetailsUserAnswersGenerator {

  private val formProvider                          = new YesNoFormProvider()
  private val form                                  = formProvider("routeDetails.routing.removeCountryOfRoutingYesNo")
  private lazy val removeCountryOfROutingYesNoRoute = routes.RemoveCountryOfRoutingYesNoController.onPageLoad(lrn, index).url

  "RemoveCountryOfRoutingYesNoController" - {

    "must return OK and the correct view for a GET" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, removeCountryOfROutingYesNoRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[RemoveCountryOfRoutingYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, index)(request, messages).toString
    }

    "when yes submitted" - {
      "must redirect to add another country of routing and remove country at specified index" in {
        forAll(arbitraryCountryOfRoutingAnswers(emptyUserAnswers, index)) {
          answers =>
            reset(mockSessionRepository)
            when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

            setExistingUserAnswers(answers)

            val request = FakeRequest(POST, removeCountryOfROutingYesNoRoute)
              .withFormUrlEncodedBody(("value", "true"))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual
              routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn).url

            val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(mockSessionRepository).set(userAnswersCaptor.capture())
            userAnswersCaptor.getValue.get(CountryOfRoutingSection(index)) mustNot be(defined)
        }
      }
    }

    "when no submitted" - {
      "must redirect to add another country and not remove country at specified index" in {
        forAll(arbitraryCountryOfRoutingAnswers(emptyUserAnswers, index)) {
          answers =>
            reset(mockSessionRepository)

            setExistingUserAnswers(answers)

            val request = FakeRequest(POST, removeCountryOfROutingYesNoRoute)
              .withFormUrlEncodedBody(("value", "false"))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual
              routes.AddAnotherCountryOfRoutingController.onPageLoad(lrn).url

            verify(mockSessionRepository, never()).set(any())
        }
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, removeCountryOfROutingYesNoRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[RemoveCountryOfRoutingYesNoView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, removeCountryOfROutingYesNoRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, removeCountryOfROutingYesNoRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
