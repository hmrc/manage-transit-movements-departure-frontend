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

package services

import base.{AppWithDefaultMockFixtures, SpecBase}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserAnswersServiceSpec extends SpecBase with AppWithDefaultMockFixtures {

  "UserAnswersService" - {
    "when answers exist in session repository" - {
      "must get answers from session repository" in {
        val userAnswers = emptyUserAnswers
        when(mockSessionRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
        val service = new UserAnswersService(mockSessionRepository)
        val result  = service.getOrCreateUserAnswers(eoriNumber, lrn).futureValue
        result mustBe userAnswers
      }
    }

    "when answers don't exist in session repository" - {
      "must create new user answers" in {
        val userAnswers = emptyUserAnswers
        when(mockSessionRepository.get(any())(any())).thenReturn(Future.successful(None))
        val service = new UserAnswersService(mockSessionRepository)
        val result  = service.getOrCreateUserAnswers(eoriNumber, lrn).futureValue
        assert(result.lastUpdated isAfter userAnswers.lastUpdated)
      }
    }
  }

}
