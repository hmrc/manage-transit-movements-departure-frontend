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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.{Generators, PreTaskListUserAnswersGenerator, RouteDetailsUserAnswersGenerator, TraderDetailsUserAnswersGenerator}
import models.{CountryList, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{ApiService, CountriesService}
import uk.gov.hmrc.http.HttpResponse
import viewModels.taskList.{Task, TaskListViewModel}
import views.html.TaskListView

import scala.concurrent.Future

class TaskListControllerSpec
    extends SpecBase
    with AppWithDefaultMockFixtures
    with Generators
    with PreTaskListUserAnswersGenerator
    with RouteDetailsUserAnswersGenerator
    with TraderDetailsUserAnswersGenerator {

  private lazy val mockViewModel                     = mock[TaskListViewModel]
  private val mockCountriesService: CountriesService = mock[CountriesService]
  private val mockApiService: ApiService             = mock[ApiService]
  private val response: HttpResponse                 = mock[HttpResponse]

  override def beforeEach(): Unit = {
    reset(mockCountriesService, mockViewModel, mockApiService, response)
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[TaskListViewModel].toInstance(mockViewModel))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))
      .overrides(bind(classOf[ApiService]).toInstance(mockApiService))

  "Task List Controller" - {

    "must return OK and the correct view for a GET" in {
      val sampleTasks = listWithMaxLength[Task]()(arbitraryTask).sample.value

      when(mockViewModel.apply(any())(any(), any())).thenReturn(sampleTasks)

      when(mockCountriesService.getCountryCodesCTC()(any())).thenReturn(Future.successful(CountryList(Nil)))
      when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))

      val userAnswers = arbitraryPreTaskListAnswers(emptyUserAnswers).sample.value
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      val view = injector.instanceOf[TaskListView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn, sampleTasks)(request, messages).toString
    }

    "must redirect to LRN page if pre- task list section is incomplete" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.preTaskList.routes.LocalReferenceNumberController.onPageLoad().url
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.TaskListController.onPageLoad(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to confirmation page when submission success" in {

      val sampleTasks = listWithMaxLength[Task]()(arbitraryTask).sample.value

      when(mockViewModel.apply(any())(any(), any())).thenReturn(sampleTasks)
      when(mockCountriesService.getCountryCodesCTC()(any())).thenReturn(Future.successful(CountryList(Nil)))
      when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))
      when(response.status).thenReturn(OK)
      when(mockApiService.submitDeclaration(any())(any())).thenReturn(Future.successful(response))

      val preTask: UserAnswers         = arbitraryPreTaskListAnswers(emptyUserAnswers).sample.value
      val traderDetails: UserAnswers   = arbitraryTraderDetailsAnswers(preTask).sample.value
      val departureDomain: UserAnswers = arbitraryRouteDetailsAnswers(traderDetails).sample.value

      setExistingUserAnswers(departureDomain)

      val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.DeclarationSubmittedController.onPageLoad().url

    }

    "must return a bad request for a 400" in {

      val sampleTasks = listWithMaxLength[Task]()(arbitraryTask).sample.value

      when(mockViewModel.apply(any())(any(), any())).thenReturn(sampleTasks)
      when(mockCountriesService.getCountryCodesCTC()(any())).thenReturn(Future.successful(CountryList(Nil)))
      when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))
      when(response.status).thenReturn(BAD_REQUEST)
      when(mockApiService.submitDeclaration(any())(any())).thenReturn(Future.successful(response))

      val preTask: UserAnswers         = arbitraryPreTaskListAnswers(emptyUserAnswers).sample.value
      val traderDetails: UserAnswers   = arbitraryTraderDetailsAnswers(preTask).sample.value
      val departureDomain: UserAnswers = arbitraryRouteDetailsAnswers(traderDetails).sample.value

      setExistingUserAnswers(departureDomain)

      val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

    }

    "must return a internal server error for a 500" in {

      val sampleTasks = listWithMaxLength[Task]()(arbitraryTask).sample.value

      when(mockViewModel.apply(any())(any(), any())).thenReturn(sampleTasks)
      when(mockCountriesService.getCountryCodesCTC()(any())).thenReturn(Future.successful(CountryList(Nil)))
      when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any())).thenReturn(Future.successful(CountryList(Nil)))
      when(response.status).thenReturn(INTERNAL_SERVER_ERROR)
      when(mockApiService.submitDeclaration(any())(any())).thenReturn(Future.successful(response))

      val preTask: UserAnswers         = arbitraryPreTaskListAnswers(emptyUserAnswers).sample.value
      val traderDetails: UserAnswers   = arbitraryTraderDetailsAnswers(preTask).sample.value
      val departureDomain: UserAnswers = arbitraryRouteDetailsAnswers(traderDetails).sample.value

      setExistingUserAnswers(departureDomain)

      val request = FakeRequest(POST, routes.TaskListController.onSubmit(lrn).url)

      val result = route(app, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

    }
  }
}
