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

package forms.mappings

import play.api.data.FormError
import play.api.data.format.Formatter

import java.time.LocalDateTime
import scala.util.{Failure, Success, Try}

private[mappings] class LocalDateTimeFormatter(
  invalidKey: String,
  allRequiredKey: String,
  multipleRequiredKey: String,
  requiredKey: String,
  args: Seq[String] = Seq.empty
) extends Formatter[LocalDateTime]
    with Formatters {

  private val fieldKeys: List[String] = List("day", "month", "year", "hour", "minute")

  private def toDateTime(key: String, day: Int, month: Int, year: Int, hour: Int, minute: Int): Either[Seq[FormError], LocalDateTime] =
    Try(LocalDateTime.of(year, month, day, hour, minute, 0)) match {
      case Success(date) =>
        Right(date)
      case Failure(_) =>
        Left(Seq(FormError(key, invalidKey, args)))
    }

  private def formatDateTime(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDateTime] = {
    val int = intFormatter(
      requiredKey = invalidKey,
      wholeNumberKey = invalidKey,
      nonNumericKey = invalidKey,
      args
    )

    for {
      day      <- int.bind(s"$key.day".replaceAll("\\s", ""), data).right
      month    <- int.bind(s"$key.month".replaceAll("\\s", ""), data).right
      year     <- int.bind(s"$key.year".replaceAll("\\s", ""), data).right
      hour     <- int.bind(s"$key.hour".replaceAll("\\s", ""), data).right
      minute   <- int.bind(s"$key.minute".replaceAll("\\s", ""), data).right
      dateTime <- toDateTime(key, day, month, year, hour, minute).right

    } yield dateTime
  }

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalDateTime] = {
    val fields: Map[String, Option[String]] = fieldKeys.map {
      field =>
        field -> data.get(s"$key.$field").filter(_.nonEmpty).map(_.replaceAll("\\s", ""))
    }.toMap

    lazy val missingFields = fields
      .withFilter(_._2.isEmpty)
      .map(_._1)
      .toList
    fields.count(_._2.isDefined) match {
      case 5 =>
        formatDateTime(key, data).left.map {
          _.map(_.copy(key = key, args = args))
        }
      case 4 =>
        Left(List(FormError(key, requiredKey, missingFields ++ args)))
      case 0 =>
        Left(List(FormError(key, allRequiredKey, args)))
      case _ =>
        Left(List(FormError(key, multipleRequiredKey, missingFields ++ args)))
    }
  }

  override def unbind(key: String, value: LocalDateTime): Map[String, String] =
    Map(
      s"$key.day"    -> value.getDayOfMonth.toString.replaceAll("\\s", ""),
      s"$key.month"  -> value.getMonthValue.toString.replaceAll("\\s", ""),
      s"$key.year"   -> value.getYear.toString.replaceAll("\\s", ""),
      s"$key.hour"   -> value.getHour.toString.replaceAll("\\s", ""),
      s"$key.minute" -> value.getMinute.toString.replaceAll("\\s", "")
    )
}
