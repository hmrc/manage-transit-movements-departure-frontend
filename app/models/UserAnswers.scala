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

package models

import pages._
import play.api.libs.json._
import queries.Gettable
import viewModels.taskList.{Task, TaskStatus}

import scala.util.{Failure, Success, Try}

final case class UserAnswers(
  lrn: LocalReferenceNumber,
  eoriNumber: EoriNumber,
  data: JsObject = Json.obj(),
  tasks: Map[String, TaskStatus] = Map(),
  status: SubmissionState
) {

  def getOptional[A](page: Gettable[A])(implicit rds: Reads[A]): Either[String, Option[A]] =
    Reads
      .optionNoError(Reads.at(page.path))
      .reads(data)
      .asOpt
      .toRight(
        "Something went wrong"
      )

  def getAsEither[A](page: Gettable[A])(implicit rds: Reads[A]): Either[String, A] =
    Reads
      .optionNoError(Reads.at(page.path))
      .reads(data)
      .getOrElse(None)
      .toRight(
        "Something went wrong"
      )

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).getOrElse(None)

  def set[A](page: QuestionPage[A], value: A)(implicit writes: Writes[A], reads: Reads[A]): Try[UserAnswers] = {
    lazy val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

    lazy val cleanup: JsObject => Try[UserAnswers] = d => {
      val updatedAnswers = copy(data = d)
      page.cleanup(Some(value), updatedAnswers)
    }

    get(page) match {
      case Some(`value`) => Success(this)
      case _             => updatedData flatMap cleanup
    }
  }

  def remove[A](page: QuestionPage[A]): Try[UserAnswers] = {
    val updatedData    = data.removeObject(page.path).getOrElse(data)
    val updatedAnswers = copy(data = updatedData)
    page.cleanup(None, updatedAnswers)
  }

  def updateTask(task: Task): UserAnswers = {
    val tasks = this.tasks.updated(task.section, task.status)
    this.copy(tasks = tasks)
  }
}

object UserAnswers {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[UserAnswers] =
    (
      (__ \ "lrn").read[LocalReferenceNumber] and
        (__ \ "eoriNumber").read[EoriNumber] and
        (__ \ "data").read[JsObject] and
        (__ \ "tasks").read[Map[String, TaskStatus]] and
        (__ \ "isSubmitted").read[SubmissionState]
    )(UserAnswers.apply _)

  implicit lazy val writes: Writes[UserAnswers] =
    (
      (__ \ "lrn").write[LocalReferenceNumber] and
        (__ \ "eoriNumber").write[EoriNumber] and
        (__ \ "data").write[JsObject] and
        (__ \ "tasks").write[Map[String, TaskStatus]] and
        (__ \ "isSubmitted").write[SubmissionState]
    )(unlift(UserAnswers.unapply))
}
