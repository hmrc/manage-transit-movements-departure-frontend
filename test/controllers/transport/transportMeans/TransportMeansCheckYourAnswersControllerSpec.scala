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

package controllers.transport.transportMeans

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import models.Mode
import navigation.transport.TransportNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.sections.Section
import viewModels.transport.transportMeans.TransportMeansAnswersViewModel
import viewModels.transport.transportMeans.TransportMeansAnswersViewModel.TransportMeansAnswersViewModelProvider
import views.html.transport.transportMeans.TransportMeansCheckYourAnswersView

class TransportMeansCheckYourAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val mode: Mode = arbitrary[Mode].sample.value

  private lazy val mockViewModelProvider = mock[TransportMeansAnswersViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[TransportMeansAnswersViewModelProvider].toInstance(mockViewModelProvider))
      .overrides(bind(classOf[TransportNavigatorProvider]).toInstance(fakeTransportNavigatorProvider))

  "ActiveBorderCheckYourAnswers Controller" - {

    "must return OK and the correct view for a GET" in {
      val sampleSections = arbitrary[List[Section]].sample.value

      when(mockViewModelProvider.apply(any(), any())(any())).thenReturn(TransportMeansAnswersViewModel(sampleSections))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, routes.TransportMeansCheckYourAnswersController.onPageLoad(lrn, mode).url)
      val result  = route(app, request).value

      val view = injector.instanceOf[TransportMeansCheckYourAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn, mode, sampleSections)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.TransportMeansCheckYourAnswersController.onPageLoad(lrn, mode).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to next page" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, routes.TransportMeansCheckYourAnswersController.onSubmit(lrn, mode).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }
  }
}
