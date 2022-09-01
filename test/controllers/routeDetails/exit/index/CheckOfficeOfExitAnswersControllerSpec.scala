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
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.routeDetails.exit.OfficeOfExitViewModel
import viewModels.routeDetails.exit.OfficeOfExitViewModel.OfficeOfExitViewModelProvider
import viewModels.sections.Section
import views.html.routeDetails.exit.index.CheckOfficeOfExitAnswersView

import scala.concurrent.Future

class CheckOfficeOfExitAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private lazy val mockViewModelProvider         = mock[OfficeOfExitViewModelProvider]
  private lazy val checkOfficeOfExitAnswersRoute = routes.CheckOfficeOfExitAnswersController.onPageLoad(lrn, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[OfficeOfExitViewModelProvider].toInstance(mockViewModelProvider))

  "CheckOfficeOfExitAnswers Controller" - {

    "must return OK and the correct view for a GET" in {
      val sampleSection = arbitrary[Section].sample.value

      when(mockViewModelProvider.apply(any(), any())(any()))
        .thenReturn(OfficeOfExitViewModel(sampleSection))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, checkOfficeOfExitAnswersRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[CheckOfficeOfExitAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn, index, Seq(sampleSection))(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.CheckOfficeOfExitAnswersController.onPageLoad(lrn, index).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to add another" in {
      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

      val request = FakeRequest(POST, routes.CheckOfficeOfExitAnswersController.onSubmit(lrn, index).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.routeDetails.exit.routes.AddAnotherOfficeOfExitController.onPageLoad(lrn).url
    }
  }
}