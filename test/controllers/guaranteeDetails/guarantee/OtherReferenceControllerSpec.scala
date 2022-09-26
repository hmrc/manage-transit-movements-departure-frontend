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

package controllers.guaranteeDetails.guarantee

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.OtherReferenceFormProvider
import generators.Generators
import models.{GuaranteeType, NormalMode}
import models.GuaranteeType.{CashDepositGuarantee, GuaranteeNotRequiredExemptPublicBody}
import navigation.GuaranteeNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.guaranteeDetails.guarantee.{GuaranteeTypePage, OtherReferencePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.guaranteeDetails.guarantee.OtherReferenceView

import scala.concurrent.Future

class OtherReferenceControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val prefix3                  = "guaranteeDetails.guarantee.otherReference.option3"
  private val prefix8                  = "guaranteeDetails.guarantee.otherReference.option8"
  private val formProvider             = new OtherReferenceFormProvider()
  private val form3                    = formProvider(prefix3)
  private val form8                    = formProvider(prefix8)
  private val mode                     = NormalMode
  private lazy val otherReferenceRoute = routes.OtherReferenceController.onPageLoad(lrn, mode, index).url

  private val validAnswer   = "test string"
  private val invalidAnswer = ""

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[GuaranteeNavigatorProvider]).toInstance(fakeGuaranteeNavigatorProvider))

  "OtherReference Controller" - {

    "must return OK and the correct view for a GET" - {

      "when Guarantee is type 3" in {
        val userAnswers = emptyUserAnswers.setValue(GuaranteeTypePage(index), CashDepositGuarantee)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, otherReferenceRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[OtherReferenceView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form3, lrn, mode, index, prefix3)(request, messages).toString
      }

      "when Guarantee is type 8" in {
        val userAnswers = emptyUserAnswers.setValue(GuaranteeTypePage(index), GuaranteeNotRequiredExemptPublicBody)
        setExistingUserAnswers(userAnswers)

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
        val userAnswers = emptyUserAnswers
          .setValue(GuaranteeTypePage(index), CashDepositGuarantee)
          .setValue(OtherReferencePage(index), validAnswer)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, otherReferenceRoute)

        val result = route(app, request).value

        val filledForm = form3.bind(Map("value" -> validAnswer))

        val view = injector.instanceOf[OtherReferenceView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, index, prefix3)(request, messages).toString
      }

      "when Guarantee is type8" in {
        val userAnswers = emptyUserAnswers
          .setValue(GuaranteeTypePage(index), GuaranteeNotRequiredExemptPublicBody)
          .setValue(OtherReferencePage(index), validAnswer)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, otherReferenceRoute)

        val result = route(app, request).value

        val filledForm = form8.bind(Map("value" -> validAnswer))

        val view = injector.instanceOf[OtherReferenceView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, index, prefix8)(request, messages).toString
      }
    }

    "must redirect to guarantee type page if not option 3 or option 8" - {
      "when GET" in {
        val guaranteeType = arbitrary[GuaranteeType](arbitraryNonOption3Or8GuaranteeType).sample.value
        val userAnswers   = emptyUserAnswers.setValue(GuaranteeTypePage(index), guaranteeType)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, otherReferenceRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.GuaranteeTypeController.onPageLoad(lrn, mode, index).url
      }

      "when POST" in {
        val guaranteeType = arbitrary[GuaranteeType](arbitraryNonOption3Or8GuaranteeType).sample.value
        val userAnswers   = emptyUserAnswers.setValue(GuaranteeTypePage(index), guaranteeType)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(POST, otherReferenceRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.GuaranteeTypeController.onPageLoad(lrn, mode, index).url
      }
    }

    "must redirect to the next page when valid data is submitted" in {
      val guaranteeType = Gen.oneOf(CashDepositGuarantee, GuaranteeNotRequiredExemptPublicBody).sample.value
      val userAnswers   = emptyUserAnswers.setValue(GuaranteeTypePage(index), guaranteeType)
      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, otherReferenceRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" - {
      "when guarantee is type 3" in {

        setExistingUserAnswers(emptyUserAnswers.setValue(GuaranteeTypePage(index), CashDepositGuarantee))

        val request    = FakeRequest(POST, otherReferenceRoute).withFormUrlEncodedBody(("value", invalidAnswer))
        val filledForm = form3.bind(Map("value" -> invalidAnswer))

        val result = route(app, request).value

        status(result) mustEqual BAD_REQUEST

        val view = injector.instanceOf[OtherReferenceView]

        contentAsString(result) mustEqual
          view(filledForm, lrn, mode, index, prefix3)(request, messages).toString

      }

      "when guarantee is type 8" in {

        setExistingUserAnswers(emptyUserAnswers.setValue(GuaranteeTypePage(index), GuaranteeNotRequiredExemptPublicBody))

        val request    = FakeRequest(POST, otherReferenceRoute).withFormUrlEncodedBody(("value", invalidAnswer))
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

      val request = FakeRequest(POST, otherReferenceRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
