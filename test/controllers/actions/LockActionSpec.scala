/*
 * Copyright 2024 HM Revenue & Customs
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

import base.SpecBase
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

class LockActionSpec extends SpecBase {

  final private val mockLockService = mock[LockService]

  val dataRequest: DataRequest[AnyContent] = DataRequest(FakeRequest(GET, "/").asInstanceOf[Request[AnyContent]], EoriNumber(""), emptyUserAnswers)

  def harness(actionProvider: LockActionProvider): Result =
    actionProvider()
      .invokeBlock(
        dataRequest,
        (_: DataRequest[AnyContent]) => Future.successful(Results.Ok)
      )
      .futureValue

  "Lock Action" - {

    "must return Ok when lock is open" in {

      when(mockLockService.checkLock(any())(any())).thenReturn(Future(LockCheck.Unlocked))

      val lockActionProvider = new LockActionProvider(mockLockService)

      harness(lockActionProvider) mustEqual Results.Ok
    }

    "must redirect to lock page when lock is not open" in {

      when(mockLockService.checkLock(any())(any())).thenReturn(Future(LockCheck.Locked))

      val lockActionProvider = new LockActionProvider(mockLockService)

      harness(lockActionProvider) mustEqual Results.SeeOther(controllers.routes.LockedController.onPageLoad().url)
    }

    "must redirect to technical difficulties when lock check fails" in {

      when(mockLockService.checkLock(any())(any())).thenReturn(Future(LockCheck.LockCheckFailure))

      val lockActionProvider = new LockActionProvider(mockLockService)

      harness(lockActionProvider) mustEqual Results.SeeOther(controllers.routes.ErrorController.technicalDifficulties().url)
    }
  }

}
