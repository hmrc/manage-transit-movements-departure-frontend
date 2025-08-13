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

package services

import base.SpecBase
import connectors.CacheConnector
import generators.Generators
import models.LockCheck
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.Future

class LockServiceSpec extends SpecBase with BeforeAndAfterEach with ScalaCheckPropertyChecks with Generators {

  private val mockConnector = mock[CacheConnector]

  private val service = new LockService(mockConnector)

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockConnector)
  }

  "Lock service" - {

    "when checkLock" - {
      "must call checkLock in connector" in {
        forAll(arbitrary[LockCheck]) {
          response =>
            beforeEach()

            val userAnswers = emptyUserAnswers
            when(mockConnector.checkLock(any())(any())).thenReturn(Future.successful(response))
            val result = service.checkLock(userAnswers)
            result.futureValue mustEqual response
            verify(mockConnector).checkLock(eqTo(userAnswers))(any())
        }
      }
    }

    "when deleteLock" - {
      "must call deleteLock in connector" in {
        forAll(arbitrary[Boolean]) {
          response =>
            beforeEach()

            val userAnswers = emptyUserAnswers
            when(mockConnector.deleteLock(any())(any())).thenReturn(Future.successful(response))
            val result = service.deleteLock(userAnswers)
            result.futureValue mustEqual response
            verify(mockConnector).deleteLock(eqTo(userAnswers))(any())
        }
      }
    }
  }
}
