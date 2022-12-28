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

package controllers.transport.transportMeans.active

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import models.NormalMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.sections.Section
import viewModels.transport.transportMeans.active.CheckYourAnswersViewModel
import viewModels.transport.transportMeans.active.CheckYourAnswersViewModel.CheckYourAnswersViewModelProvider
import views.html.transport.transportMeans.active.CheckYourAnswersView

class CheckYourAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private lazy val mockViewModelProvider = mock[CheckYourAnswersViewModelProvider]

  private lazy val checkYourAnswersRoute = routes.CheckYourAnswersController.onPageLoad(lrn, NormalMode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[CheckYourAnswersViewModelProvider].toInstance(mockViewModelProvider))

  "CheckYourAnswers Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val sampleSections = listWithMaxLength[Section]().sample.value

      when(mockViewModelProvider.apply(any(), any(), any())(any()))
        .thenReturn(CheckYourAnswersViewModel(sampleSections))

      val request = FakeRequest(GET, checkYourAnswersRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[CheckYourAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(lrn, NormalMode, index, sampleSections)(request, messages).toString
    }
  }
}
