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
import models.journeyDomain.{OpsError, WriterError}
import models.requests.DataRequest
import models.{Mode, UserAnswers}
import navigation.Navigator
import play.api.libs.json.Writes
import play.api.mvc.AnyContent
import play.api.mvc.Results.Redirect
import queries.Settable
import repositories.SessionRepository

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

    def sessionWriter(value: A)(implicit writes: Writes[A], sessionRepository: SessionRepository): UserAnswersWriter[UserAnswers] =
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
  }

  implicit class SettableOpsRunner[A](userAnswersWriter: UserAnswersWriter[A]) {

    def runner(userAnswers: UserAnswers): EitherType[A]                        = userAnswersWriter.run(userAnswers)
    def runner()(implicit dataRequest: DataRequest[AnyContent]): EitherType[A] = userAnswersWriter.run(dataRequest.userAnswers)

    def runWithRedirect(userAnswers: UserAnswers, mode: Mode)(implicit navigator: Navigator) = runner(userAnswers) match {
      case Left(_)      => Redirect(controllers.routes.ErrorController.technicalDifficulties())
      case Right(value) => Redirect(navigator.nextPage(???, mode, userAnswers))

    }

    def runWithRedirect()(implicit dataRequest: DataRequest[_]): EitherType[A] = userAnswersWriter.run(dataRequest.userAnswers)
  }
}
