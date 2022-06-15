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
import models.journeyDomain.{OpsError, WriterError}
import models.requests.DataRequest
import play.api.libs.json.Writes
import queries.Settable
import repositories.SessionRepository

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

package object controllers {

  type EitherType[A]        = Either[OpsError, A]
  type UserAnswersWriter[A] = ReaderT[EitherType, UserAnswers, A]

  object UserAnswersReader {
    def apply[A: UserAnswersWriter]: UserAnswersWriter[A] = implicitly[UserAnswersWriter[A]]

    def apply(fn: UserAnswers => EitherType[UserAnswers]): UserAnswersWriter[UserAnswers] =
      ReaderT[EitherType, UserAnswers, UserAnswers](fn)
  }

  implicit class SettableOps[A](page: Settable[A]) {

    def userAnswerWriter(value: A)(implicit writes: Writes[A]): UserAnswersWriter[UserAnswers] =
      ReaderT[EitherType, UserAnswers, UserAnswers](
        userAnswers =>
          userAnswers.set[A](page, value) match {
            case Success(value)     => Right(value)
            case Failure(exception) => Left(WriterError(page, Some(s"Failed to write $value to page $page with exception: ${exception.toString}")))
          }
      )

    def userAnswersWriterRun(value: A)(implicit writes: Writes[A], request: DataRequest[_]): EitherType[UserAnswers] =
      userAnswerWriter(value).run(request.userAnswers)

    def sessionWriter(value: A, sessionRepository: SessionRepository)(implicit writes: Writes[A]): UserAnswersWriter[UserAnswers] =
      userAnswerWriter(value).flatMap {
        updatedUserAnswers =>
          ReaderT[EitherType, UserAnswers, UserAnswers](
            _ =>
              sessionRepository.set(updatedUserAnswers).value match {
                case Some(Success(true))  => Right(updatedUserAnswers)
                case Some(Success(false)) => Left(WriterError(page, Some(s"Failed to write $value to Mongo for page $page non critical")))
                case Some(Failure(exception)) =>
                  Left(WriterError(page, Some(s"Failed to write $value to Mongo for page $page with this exception: ${exception.toString}")))
                case None => Left(WriterError(page, Some("Future not complete")))
              }
          )
      }

    def sessionWriterRun(value: A, sessionRepository: SessionRepository)(implicit
      writes: Writes[A],
      ec: ExecutionContext,
      request: DataRequest[_]
    ): EitherType[UserAnswers] =
      sessionWriter(value, sessionRepository).run(request.userAnswers)
  }
}
