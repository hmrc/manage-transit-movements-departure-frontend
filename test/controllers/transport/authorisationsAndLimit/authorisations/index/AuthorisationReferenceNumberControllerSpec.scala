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

package controllers.transport.authorisationsAndLimit.authorisations.index

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.AuthorisationReferenceNumberFormProvider
import models.ProcedureType.{Normal, Simplified}
import models.transport.authorisations.AuthorisationType
import models.transport.transportMeans.departure.InlandMode
import models.{Index, NormalMode}
import navigation.transport.AuthorisationNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Gen
import pages.preTaskList.ProcedureTypePage
import pages.traderDetails.consignment.ApprovedOperatorPage
import pages.transport.authorisationsAndLimit.authorisations.index.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import pages.transport.transportMeans.departure.InlandModePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.transport.authorisationsAndLimit.authorisations.index.AuthorisationReferenceNumberView

import scala.concurrent.Future

class AuthorisationReferenceNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val prefix                                             = "transport.authorisations.authorisationReferenceNumber"
  private def dynamicTitle(authorisationType: AuthorisationType) = messages(s"$prefix.$authorisationType")

  private val formProvider                               = new AuthorisationReferenceNumberFormProvider()
  private def form(authorisationType: AuthorisationType) = formProvider(prefix, dynamicTitle(authorisationType))
  private val mode                                       = NormalMode
  private val validAnswer                                = "testString"

  private val firstInlandMode = Gen
    .oneOf(
      InlandMode.values
        .filterNot(_ == InlandMode.Maritime)
        .filterNot(_ == InlandMode.Rail)
        .filterNot(_ == InlandMode.Air)
    )
    .sample
    .value

  private lazy val authorisationReferenceNumberRoute = routes.AuthorisationReferenceNumberController.onPageLoad(lrn, mode, authorisationIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AuthorisationNavigatorProvider]).toInstance(fakeAuthorisationNavigatorProvider))

  "AuthorisationReferenceNumber Controller" - {

    "must return OK and the correct view for a GET" - {

      "when using reduced data set and Inland Mode is one of Maritime, Rail or Road" in {
        val authorisationType = AuthorisationType.TRD
        val inlandMode        = Gen.oneOf(Seq(InlandMode.Maritime, InlandMode.Rail, InlandMode.Air)).sample.value

        val userAnswers = emptyUserAnswers
          .setValue(ApprovedOperatorPage, true)
          .setValue(InlandModePage, inlandMode)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, authorisationReferenceNumberRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AuthorisationReferenceNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(authorisationType), lrn, s"$prefix.$authorisationType", mode, authorisationIndex)(request, messages).toString
      }

      "when using reduced data set and Inland Mode is not one of Maritime, Rail or Road and Procedure type is simplified" in {
        val authorisationType = AuthorisationType.ACR

        val userAnswers = emptyUserAnswers
          .setValue(ApprovedOperatorPage, true)
          .setValue(InlandModePage, firstInlandMode)
          .setValue(ProcedureTypePage, Simplified)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, authorisationReferenceNumberRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AuthorisationReferenceNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(authorisationType), lrn, s"$prefix.$authorisationType", mode, authorisationIndex)(request, messages).toString
      }

      "when using reduced data set and Inland Mode is not one of Maritime, Rail or Road and Procedure type is normal" in {
        val authorisationType = AuthorisationType.SSE

        val userAnswers = emptyUserAnswers
          .setValue(ApprovedOperatorPage, true)
          .setValue(InlandModePage, firstInlandMode)
          .setValue(ProcedureTypePage, Normal)
          .setValue(AuthorisationTypePage(authorisationIndex), authorisationType)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, authorisationReferenceNumberRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AuthorisationReferenceNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(authorisationType), lrn, s"$prefix.$authorisationType", mode, authorisationIndex)(request, messages).toString
      }

    }

    "must populate the view correctly on a GET when the question has previously been answered" - {

      "when it is the first authorisation index" in {
        val authorisationType = AuthorisationType.TRD

        val userAnswers = emptyUserAnswers
          .setValue(ApprovedOperatorPage, true)
          .setValue(InlandModePage, firstInlandMode)
          .setValue(ProcedureTypePage, Normal)
          .setValue(AuthorisationTypePage(authorisationIndex), authorisationType)
          .setValue(AuthorisationReferenceNumberPage(authorisationIndex), validAnswer)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, authorisationReferenceNumberRoute)

        val result = route(app, request).value

        val filledForm = form(authorisationType).bind(Map("value" -> validAnswer))

        val view = injector.instanceOf[AuthorisationReferenceNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, s"$prefix.$authorisationType", mode, authorisationIndex)(request, messages).toString
      }

      "when it is not the first authorisation index" in {
        val authorisationType  = AuthorisationType.TRD
        val authorisationType1 = AuthorisationType.SSE

        val userAnswers = emptyUserAnswers
          .setValue(ApprovedOperatorPage, true)
          .setValue(InlandModePage, firstInlandMode)
          .setValue(ProcedureTypePage, Normal)
          .setValue(AuthorisationTypePage(authorisationIndex), authorisationType)
          .setValue(AuthorisationReferenceNumberPage(authorisationIndex), validAnswer)
          .setValue(AuthorisationTypePage(Index(1)), authorisationType1)
          .setValue(AuthorisationReferenceNumberPage(Index(1)), validAnswer)

        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, routes.AuthorisationReferenceNumberController.onPageLoad(lrn, mode, Index(1)).url)

        val result = route(app, request).value

        val filledForm = form(authorisationType).bind(Map("value" -> validAnswer))

        val view = injector.instanceOf[AuthorisationReferenceNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm, lrn, s"$prefix.$authorisationType1", mode, Index(1))(request, messages).toString
      }

    }

    "must redirect to the next page when valid data is submitted" in {
      val inlandMode = Gen.oneOf(Seq(InlandMode.Maritime, InlandMode.Rail, InlandMode.Air)).sample.value

      val userAnswers = emptyUserAnswers
        .setValue(ApprovedOperatorPage, true)
        .setValue(InlandModePage, inlandMode)
      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, authorisationReferenceNumberRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val authorisationType = AuthorisationType.TRD
      val inlandMode        = Gen.oneOf(Seq(InlandMode.Maritime, InlandMode.Rail, InlandMode.Air)).sample.value

      val userAnswers = emptyUserAnswers
        .setValue(ApprovedOperatorPage, true)
        .setValue(InlandModePage, inlandMode)
      setExistingUserAnswers(userAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, authorisationReferenceNumberRoute).withFormUrlEncodedBody(("value", invalidAnswer))
      val filledForm = form(authorisationType).bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[AuthorisationReferenceNumberView]

      contentAsString(result) mustEqual
        view(filledForm, lrn, s"$prefix.$authorisationType", mode, authorisationIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, authorisationReferenceNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, authorisationReferenceNumberRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
