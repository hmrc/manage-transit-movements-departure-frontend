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

package controllers.actions

import base.{AppWithDefaultMockFixtures, SpecBase}
import config.PhaseConfig.Values
import config.{FrontendAppConfig, PhaseConfig}
import models.requests.DataRequest
import models.{EoriNumber, LockCheck}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.mvc.{AnyContent, Request, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.LockService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CrossoverActionSpec extends SpecBase with AppWithDefaultMockFixtures {

  val config = mock[PhaseConfig]

  def harness(actionProvider: CrossoverActionProvider, isTransitional: Boolean): Result =
    actionProvider()
      .invokeBlock(
        DataRequest(FakeRequest(GET, "/").asInstanceOf[Request[AnyContent]], EoriNumber(""), emptyUserAnswers.copy(isTransitional = isTransitional)),
        (_: DataRequest[AnyContent]) => Future.successful(Results.Ok)
      )
      .futureValue

  "Crossover Action" - {

    "must return Ok when crossover flag is same in config and user answers" in {
      when(config.values).thenReturn(Values(2.0))
      val transitionalCrossoverActionProvider = new CrossoverActionProvider(config)
      harness(transitionalCrossoverActionProvider, isTransitional = true) mustBe Results.Ok

      when(config.values).thenReturn(Values(2.1))
      val finalCrossoverActionProvider = new CrossoverActionProvider(config)
      harness(finalCrossoverActionProvider, isTransitional = false) mustBe Results.Ok
    }

    "must redirect to draft no loner available page when crossover flag is different in config and user answers" in {
      when(config.values).thenReturn(Values(2.0))
      val transitionalCrossoverActionProvider = new CrossoverActionProvider(config)
      harness(transitionalCrossoverActionProvider, isTransitional = false) mustBe Results.SeeOther(
        controllers.routes.DraftNoLongerAvailableController.onPageLoad().url
      )

      when(config.values).thenReturn(Values(2.1))
      val finalCrossoverActionProvider = new CrossoverActionProvider(config)
      harness(finalCrossoverActionProvider, isTransitional = true) mustBe Results.SeeOther(controllers.routes.DraftNoLongerAvailableController.onPageLoad().url)
    }

  }

}
