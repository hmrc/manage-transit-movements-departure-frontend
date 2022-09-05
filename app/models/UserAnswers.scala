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

package models

import pages._
import play.api.libs.json._
import queries.Gettable
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.LocalDateTime
import scala.util.{Failure, Try}

final case class UserAnswers(
  lrn: LocalReferenceNumber,
  eoriNumber: EoriNumber,
  data: JsObject = Json.obj(),
  lastUpdated: LocalDateTime = LocalDateTime.now,
  id: Id = Id()
) {

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.optionNoError(Reads.at(page.path)).reads(data).getOrElse(None)

  def set[A](page: QuestionPage[A], value: A)(implicit format: Format[A]): Try[UserAnswers] =
    data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(updatedData, _) =>
        val updatedAnswers = copy(data = updatedData)
        page
          .cleanup(Some(value), updatedAnswers)
          .flatMap(page.cleanup(Some(value), get(page), _))
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }

  def remove[A](page: QuestionPage[A]): Try[UserAnswers] = {
    val updatedData    = data.removeObject(page.path).getOrElse(data)
    val updatedAnswers = copy(data = updatedData)
    page.cleanup(None, updatedAnswers)
  }
}

object UserAnswers {

  import play.api.libs.functional.syntax._

  implicit lazy val reads: Reads[UserAnswers] =
    (
      (__ \ "lrn").read[LocalReferenceNumber] and
        (__ \ "eoriNumber").read[EoriNumber] and
        (__ \ "data").read[JsObject] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.localDateTimeReads) and
        (__ \ "_id").read[Id]
    )(UserAnswers.apply _)

  implicit lazy val writes: Writes[UserAnswers] =
    (
      (__ \ "lrn").write[LocalReferenceNumber] and
        (__ \ "eoriNumber").write[EoriNumber] and
        (__ \ "data").write[JsObject] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.localDateTimeWrites) and
        (__ \ "_id").write[Id]
    )(unlift(UserAnswers.unapply))

  implicit lazy val format: Format[UserAnswers] = Format(reads, writes)
}
