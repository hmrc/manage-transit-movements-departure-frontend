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

package controllers.guaranteeDetails

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.OtherReferenceFormProvider
import models.guaranteeDetails.GuaranteeType.{CashDepositGuarantee, GuaranteeNotRequiredExemptPublicBody}
import models.{NormalMode, UserAnswers}
import navigation.GuaranteeNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.guaranteeDetails.{GuaranteeTypePage, OtherReferencePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.guaranteeDetails.OtherReferenceView

import scala.concurrent.Future

class OtherReferenceControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val prefix3                  = "guaranteeDetails.otherReference.option3"
  private val prefix8                  = "guaranteeDetails.otherReference.option8"
  private val formProvider             = new OtherReferenceFormProvider()
  private val form3                    = formProvider(prefix3)
  private val form8                    = formProvider(prefix8)
  private val mode                     = NormalMode
  private lazy val otherReferenceRoute = routes.OtherReferenceController.onPageLoad(lrn, mode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[GuaranteeNavigatorProvider]).toInstance(fakeGuaranteeNavigatorProvider))

  "OtherReference Controller" - {

    "must return OK and the correct view for a GET" - {

      "when Guarantee is type 3" in {

        setExistingUserAnswers(emptyUserAnswers.setValue(GuaranteeTypePage(index), CashDepositGuarantee))

        val request = FakeRequest(GET, otherReferenceRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[OtherReferenceView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form3, lrn, mode, index, prefix3)(request, messages).toString
      }

      "when Guarantee is type 8" in {

        setExistingUserAnswers(emptyUserAnswers.setValue(GuaranteeTypePage(index), GuaranteeNotRequiredExemptPublicBody))

        val request = FakeRequest(GET, otherReferenceRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[OtherReferenceView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form8, lrn, mode, index, prefix8)(request, messages).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" - {
      "when Guarantee is type3" in {
        val userAnswers = UserAnswers(lrn, eoriNumber)
          .setValue(GuaranteeTypePage(index), CashDepositGuarantee)
          .setValue(OtherReferencePage(index), "test string")
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, otherReferenceRoute)

        val result = route(app, request).value

        val filledForm = form3.bind(Map("value" -> "test string"))

        val view = injector.instanceOf[OtherReferenceView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, index, prefix3)(request, messages).toString
      }

      "when Guarantee is type8" in {
        val userAnswers = UserAnswers(lrn, eoriNumber)
          .setValue(GuaranteeTypePage(index), GuaranteeNotRequiredExemptPublicBody)
          .setValue(OtherReferencePage(index), "test string")
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, otherReferenceRoute)

        val result = route(app, request).value

        val filledForm = form8.bind(Map("value" -> "test string"))

        val view = injector.instanceOf[OtherReferenceView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, index, prefix8)(request, messages).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers.setValue(GuaranteeTypePage(index), CashDepositGuarantee))

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, otherReferenceRoute)
          .withFormUrlEncodedBody(("value", "teststring"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" - {
      "when guarantee is type 3" in {

        setExistingUserAnswers(emptyUserAnswers.setValue(GuaranteeTypePage(index), CashDepositGuarantee))

        val invalidAnswer = ""

        val request    = FakeRequest(POST, otherReferenceRoute).withFormUrlEncodedBody(("value", ""))
        val filledForm = form3.bind(Map("value" -> invalidAnswer))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST

        val view = injector.instanceOf[OtherReferenceView]

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, index, prefix3)(request, messages).toString

      }

      "when guarantee is type 8" in {

        setExistingUserAnswers(emptyUserAnswers.setValue(GuaranteeTypePage(index), GuaranteeNotRequiredExemptPublicBody))

        val invalidAnswer = ""

        val request    = FakeRequest(POST, otherReferenceRoute).withFormUrlEncodedBody(("value", ""))
        val filledForm = form8.bind(Map("value" -> invalidAnswer))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST

        val view = injector.instanceOf[OtherReferenceView]

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, index, prefix8)(request, messages).toString

      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, otherReferenceRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, otherReferenceRoute)
          .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
