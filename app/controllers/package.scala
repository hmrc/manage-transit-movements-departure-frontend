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

import cats.data.ReaderT
import models.UserAnswers
import play.api.libs.json.Writes
import queries.Settable
import repositories.SessionRepository

import scala.concurrent.{ExecutionContext, Future}

package object controllers {

  implicit class SettableOps[A](a: Settable[A]) {


    def userAnswerWriter(value: A)(implicit writes: Writes[A], ec: ExecutionContext): ReaderT[Future, UserAnswers, UserAnswers] =
      ReaderT[Future, UserAnswers, UserAnswers](
        userAnswers => Future.fromTry(userAnswers.set[A](a, value))
      )

    def sessionWriter(value: A, sessionRepository: SessionRepository)
                     (implicit writes: Writes[A], ec: ExecutionContext): ReaderT[Future, UserAnswers, UserAnswers] = {
      ReaderT[Future, UserAnswers, UserAnswers](
        userAnswers =>
          Future.fromTry(userAnswers.set[A](a, value)).flatMap(
            updatedUserAnswers => sessionRepository.set(updatedUserAnswers).map(_ =>
              updatedUserAnswers
            )
          )
      )
    }
  }

}
