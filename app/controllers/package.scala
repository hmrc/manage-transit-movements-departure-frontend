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
import models.{Mode, UserAnswers}
import models.journeyDomain.{OpsError, WriterError}
import models.requests.DataRequest
import navigation.Navigator
import pages.QuestionPage
import play.api.libs.json.Writes
import play.api.mvc.{Call, Result}
import play.api.mvc.Results.Redirect
import repositories.SessionRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

package object controllers {

  type EitherType[A]        = Either[OpsError, A]
  type UserAnswersWriter[A] = ReaderT[EitherType, UserAnswers, A]

  object UserAnswersReader {
    def apply[A: UserAnswersWriter]: UserAnswersWriter[A] = implicitly[UserAnswersWriter[A]]

    def apply(fn: UserAnswers => EitherType[UserAnswers]): UserAnswersWriter[UserAnswers] =
      ReaderT[EitherType, UserAnswers, UserAnswers](fn)
  }

  implicit class SettableOps[A](page: QuestionPage[A]) {

    def userAnswerWriter(value: A)(implicit writes: Writes[A]): UserAnswersWriter[(QuestionPage[A], UserAnswers)] =
      ReaderT[EitherType, UserAnswers, (QuestionPage[A], UserAnswers)](
        userAnswers =>
          userAnswers.set[A](page, value) match {
            case Success(value)     => Right((page, value))
            case Failure(exception) => Left(WriterError(page, Some(s"Failed to write $value to page $page with exception: ${exception.toString}")))
          }
      )
  }

  implicit class SettableOpsRunner[A](userAnswersWriter: UserAnswersWriter[(QuestionPage[A], UserAnswers)]) {

    def runner(userAnswers: UserAnswers): EitherType[(QuestionPage[A], UserAnswers)]               = userAnswersWriter.run(userAnswers)
    def runner()(implicit dataRequest: DataRequest[_]): EitherType[(QuestionPage[A], UserAnswers)] = userAnswersWriter.run(dataRequest.userAnswers)

    def writeToSession(
      userAnswers: UserAnswers
    )(implicit sessionRepository: SessionRepository, executionContext: ExecutionContext): Future[(QuestionPage[A], UserAnswers)] = runner(userAnswers) match {
      case Left(opsError) => Future.failed(new Exception(s"${opsError.toString}"))
      case Right(value) =>
        sessionRepository
          .set(value._2)
          .map(
            _ => value
          )
    }

    def writeToSession()(implicit
      dataRequest: DataRequest[_],
      sessionRepository: SessionRepository,
      ex: ExecutionContext
    ): Future[(QuestionPage[A], UserAnswers)] = runner() match {
      case Left(opsError) => Future.failed(new Exception(s"${opsError.toString}"))
      case Right(value) =>
        sessionRepository
          .set(value._2)
          .map(
            _ => value
          )
    }

    def writeToSessionNavigator(userAnswers: UserAnswers,
                                mode: Mode
    )(implicit sessionRepository: SessionRepository, navigator: Navigator, executionContext: ExecutionContext): Future[Result] =
      writeToSession(userAnswers).map {
        result => Redirect(navigator.nextPage(result._1, mode, result._2))
      }

    def writeToSessionNavigator(
      mode: Mode
    )(implicit dataRequest: DataRequest[_], sessionRepository: SessionRepository, navigator: Navigator, executionContext: ExecutionContext): Future[Result] =
      writeToSession(dataRequest.userAnswers).map {
        result => Redirect(navigator.nextPage(result._1, mode, result._2))
      }

    def writeToSessionNavigator(
      call: Call
    )(implicit dataRequest: DataRequest[_], sessionRepository: SessionRepository, executionContext: ExecutionContext): Future[Result] =
      writeToSession(dataRequest.userAnswers).map {
        _ => Redirect(call)
      }
  }
}
