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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, status, GET, _}

import scala.concurrent.Future

class DeleteLockControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockLockService)
  }

  "DeleteLockController" - {
    "must redirect to logout url when lock successfully deleted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockLockService.deleteLock(any())(any())).thenReturn(Future.successful(true))

      val result = route(app, FakeRequest(GET, routes.DeleteLockController.delete(lrn).url)).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe renderConfig.signOutUrl

      verify(mockLockService, times(1)).deleteLock(any())(any())
    }

    "must redirect to logout url when lock is not deleted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockLockService.deleteLock(any())(any())).thenReturn(Future.successful(false))

      val result = route(app, FakeRequest(GET, routes.DeleteLockController.delete(lrn).url)).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe renderConfig.signOutUrl

      verify(mockLockService, times(1)).deleteLock(any())(any())
    }

    "must redirect to logout url when delete lock fails" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockLockService.deleteLock(any())(any())).thenReturn(Future.failed(new Exception))

      val result = route(app, FakeRequest(GET, routes.DeleteLockController.delete(lrn).url)).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe renderConfig.signOutUrl

      verify(mockLockService, times(1)).deleteLock(any())(any())
    }
  }
}
