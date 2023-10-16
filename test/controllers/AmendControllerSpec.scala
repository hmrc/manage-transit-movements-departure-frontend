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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class AmendControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private lazy val amendRoute: String = routes.AmendController.onPageLoad(lrn, departureId).url

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSessionRepository)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()

  "AmendController" - {

    "setUserAnswers" - {

      "when answers successfully submitted to cache" - {
        "must redirect to the declaration summary" in {
          setExistingUserAnswers(emptyUserAnswers.copy(departureId = Some(departureId)))
          when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

          val request = FakeRequest(GET, amendRoute)

          val result = route(app, request).value

          redirectLocation(result).value mustEqual controllers.routes.TaskListController.onPageLoad(lrn).url

        }
      }

      "when answers unsuccessfully submitted to cache" - {
        "must redirect to technical difficulties when" in {
          setExistingUserAnswers(emptyUserAnswers.copy(departureId = Some(departureId)))
          when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(false))

          val request = FakeRequest(GET, amendRoute)

          val result = route(app, request).value

          redirectLocation(result).value mustEqual controllers.routes.ErrorController.technicalDifficulties().url

        }
      }
    }
  }
}
