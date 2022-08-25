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

package controllers.routeDetails.officeOfExit

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{Index, NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.PreTaskListDetails
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.transit.index.{AddOfficeOfTransitETAYesNoPage, OfficeOfTransitCountryPage, OfficeOfTransitPage}
import pages.sections.routeDetails.OfficeOfExitForTransitSection
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.routeDetails.officeOfExit.ConfirmRemoveOfficeOfExitView

import scala.concurrent.Future

class ConfirmRemoveOfficeOfExitControllerSpec
    extends SpecBase
    with AppWithDefaultMockFixtures
    with MockitoSugar
    with Generators
    with RouteDetailsUserAnswersGenerator
    with ScalaCheckPropertyChecks {

  private val formProvider                        = new YesNoFormProvider()
  private val form                                = formProvider("routeDetails.officeOfExit.confirmRemoveOfficeOfExit")
  private val mode                                = NormalMode
  private lazy val confirmRemoveOfficeOfExitRoute = routes.ConfirmRemoveOfficeOfExitController.onPageLoad(lrn, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[PreTaskListDetails]).toInstance(fakeNavigator))

  "ConfirmRemoveOfficeOfExit Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, confirmRemoveOfficeOfExitRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[ConfirmRemoveOfficeOfExitView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, lrn, index)(request, messages).toString
    }

    // TODO: Update tests to use the correct page and controllers.
    "must redirect to the next page when valid data is submitted" ignore {

      "when yes submitted" - {
        "must redirect to add another office of transit and remove office of transit at specified index" in {
          val countryCode   = arbitrary[CountryCode].sample.value
          val customsOffice = arbitrary[CustomsOffice].sample.value
          val answers = emptyUserAnswers
            .setValue(OfficeOfTransitCountryPage(Index(0)), Country(countryCode, "France"))
            .setValue(OfficeOfTransitPage(Index(0)), customsOffice)
            .setValue(AddOfficeOfTransitETAYesNoPage(Index(0)), false)
          reset(mockSessionRepository)
          when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

          setExistingUserAnswers(answers)

          val request = FakeRequest(POST, confirmRemoveOfficeOfExitRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            controllers.routeDetails.transit.routes.AddAnotherOfficeOfTransitController.onPageLoad(lrn).url

          val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(mockSessionRepository).set(userAnswersCaptor.capture())
          userAnswersCaptor.getValue.get(OfficeOfExitForTransitSection(index)) mustNot be(defined)
        }
      }

      "when no submitted" - {
        "must redirect to add another office of transit and not remove office of transit at specified index" in {
          val countryCode   = arbitrary[CountryCode].sample.value
          val customsOffice = arbitrary[CustomsOffice].sample.value
          val answers = emptyUserAnswers
            .setValue(OfficeOfTransitCountryPage(Index(0)), Country(countryCode, "France"))
            .setValue(OfficeOfTransitPage(Index(0)), customsOffice)
            .setValue(AddOfficeOfTransitETAYesNoPage(Index(0)), false)
          reset(mockSessionRepository)

          setExistingUserAnswers(answers)

          val request = FakeRequest(POST, confirmRemoveOfficeOfExitRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            controllers.routeDetails.transit.routes.AddAnotherOfficeOfTransitController.onPageLoad(lrn).url

          verify(mockSessionRepository, never()).set(any())
        }
      }

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, confirmRemoveOfficeOfExitRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ConfirmRemoveOfficeOfExitView]

      contentAsString(result) mustEqual
        view(boundForm, lrn, index)(request, messages).toString
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
