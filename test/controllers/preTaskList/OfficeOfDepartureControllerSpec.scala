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

package controllers.preTaskList

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.{routes => mainRoutes}
import forms.SelectableFormProvider
import models.reference.{Country, CustomsOffice}
import models.{NormalMode, SelectableList, UserAnswers}
import navigation.PreTaskListNavigatorProvider
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import pages.preTaskList.OfficeOfDeparturePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{CountriesService, CustomsOfficesService}
import views.html.preTaskList.OfficeOfDepartureView

import scala.concurrent.Future

class OfficeOfDepartureControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val customsOffice1: CustomsOffice                 = CustomsOffice("GB1", "someName", None)
  private val customsOffice2: CustomsOffice                 = CustomsOffice("GB2", "name", None)
  private val customsOffices: SelectableList[CustomsOffice] = SelectableList(Seq(customsOffice1, customsOffice2))

  private val gbForm = new SelectableFormProvider()("officeOfDeparture", customsOffices)
  private val mode   = NormalMode

  private val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]
  private val mockCountriesService: CountriesService           = mock[CountriesService]
  private lazy val officeOfDepartureRoute: String              = routes.OfficeOfDepartureController.onPageLoad(lrn, mode).url

  override def beforeEach(): Unit = {
    reset(mockCustomsOfficesService); reset(mockCountriesService)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[PreTaskListNavigatorProvider]).toInstance(fakePreTaskListNavigatorProvider))
      .overrides(bind(classOf[CustomsOfficesService]).toInstance(mockCustomsOfficesService))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))

  "OfficeOfDeparture Controller" - {

    "must return OK and the correct view for a GET" in {
      setExistingUserAnswers(emptyUserAnswers)
      when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))

      val request = FakeRequest(GET, officeOfDepartureRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[OfficeOfDepartureView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(gbForm, lrn, customsOffices.values, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.setValue(OfficeOfDeparturePage, customsOffice1)
      setExistingUserAnswers(userAnswers)

      when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))

      val request = FakeRequest(GET, officeOfDepartureRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[OfficeOfDepartureView]

      status(result) mustEqual OK

      val filledForm = gbForm.bind(Map("value" -> "GB1"))

      contentAsString(result) mustEqual
        view(filledForm, lrn, customsOffices.values, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" - {
      "and office is in CL112 but not CL147 and not CL010" in {
        setExistingUserAnswers(emptyUserAnswers)
        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)
        when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))
        when(mockCountriesService.getCountryCodesCTC()(any())).thenReturn(Future.successful(Seq(Country("GB"))))
        when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(Seq(Country("FR"))))
        when(mockCountriesService.getCommunityCountries()(any())).thenReturn(Future.successful(Seq(Country("IT"))))

        val request = FakeRequest(POST, officeOfDepartureRoute)
          .withFormUrlEncodedBody(("value", "GB1"))

        val result: Future[Result] = route(app, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.data mustBe Json.parse("""
            |{
            |  "preTaskList" : {
            |    "officeOfDeparture" : {
            |      "id" : "GB1",
            |      "name" : "someName",
            |      "isInCL112" : true,
            |      "isInCL147" : false,
            |      "isInCL010" : false
            |    }
            |  }
            |}
            |""".stripMargin)
      }

      "and office is in CL147 but is not in CL112 and not CL010" in {
        setExistingUserAnswers(emptyUserAnswers)
        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)
        when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))
        when(mockCountriesService.getCountryCodesCTC()(any())).thenReturn(Future.successful(Seq(Country("FR"))))
        when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(Seq(Country("GB"))))
        when(mockCountriesService.getCommunityCountries()(any())).thenReturn(Future.successful(Seq(Country("IT"))))

        val request = FakeRequest(POST, officeOfDepartureRoute)
          .withFormUrlEncodedBody(("value", "GB1"))

        val result: Future[Result] = route(app, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.data mustBe Json.parse("""
            |{
            |  "preTaskList" : {
            |    "officeOfDeparture" : {
            |      "id" : "GB1",
            |      "name" : "someName",
            |      "isInCL112" : false,
            |      "isInCL147" : true,
            |      "isInCL010" : false
            |    }
            |  }
            |}
            |""".stripMargin)
      }

      "and office is in CL010 but is not in CL112 and not CL147" in {
        setExistingUserAnswers(emptyUserAnswers)
        when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)
        when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))
        when(mockCountriesService.getCountryCodesCTC()(any())).thenReturn(Future.successful(Seq(Country("FR"))))
        when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(Seq(Country("IT"))))
        when(mockCountriesService.getCommunityCountries()(any())).thenReturn(Future.successful(Seq(Country("GB"))))

        val request = FakeRequest(POST, officeOfDepartureRoute)
          .withFormUrlEncodedBody(("value", "GB1"))

        val result: Future[Result] = route(app, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url

        val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
        userAnswersCaptor.getValue.data mustBe Json.parse("""
            |{
            |  "preTaskList" : {
            |    "officeOfDeparture" : {
            |      "id" : "GB1",
            |      "name" : "someName",
            |      "isInCL112" : false,
            |      "isInCL147" : false,
            |      "isInCL010" : true
            |    }
            |  }
            |}
            |""".stripMargin)
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setExistingUserAnswers(emptyUserAnswers)
      when(mockCustomsOfficesService.getCustomsOfficesOfDeparture(any())).thenReturn(Future.successful(customsOffices))

      val request   = FakeRequest(POST, officeOfDepartureRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = gbForm.bind(Map("value" -> ""))

      val result = route(app, request).value

      val view = injector.instanceOf[OfficeOfDepartureView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, lrn, customsOffices.values, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, officeOfDepartureRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, officeOfDepartureRoute)
        .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual mainRoutes.SessionExpiredController.onPageLoad().url
    }
  }
}
