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
import connectors.CacheConnector
import models.EoriNumber
import models.requests.DataRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContent, Request, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LockActionSpec extends SpecBase with AppWithDefaultMockFixtures {

  val dataRequest = DataRequest(FakeRequest(GET, "/").asInstanceOf[Request[AnyContent]], EoriNumber(""), emptyUserAnswers)

  def harness(): Result = {

    lazy val actionProvider = app.injector.instanceOf[LockActionImpl]

    actionProvider
      .invokeBlock(
        dataRequest,
        {
          _: DataRequest[_] => Future.successful(Results.Ok)
        }
      )
      .futureValue
  }

  val cacheConnector: CacheConnector = mock[CacheConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[CacheConnector].toInstance(cacheConnector))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(cacheConnector)
  }

  "Lock Action" - {

    "must return Ok when lock is open" in {

      when(cacheConnector.checkLock(any())(any())).thenReturn(Future(true))

      harness() mustBe Results.Ok
    }

    "must redirect to lock page when lock is not open" in {

      when(cacheConnector.checkLock(any())(any())).thenReturn(Future(false))

      harness() mustBe Results.SeeOther(controllers.routes.SessionExpiredController.onPageLoad().url)
    }
  }

}
