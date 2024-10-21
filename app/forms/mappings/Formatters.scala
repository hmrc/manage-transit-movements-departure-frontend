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

package forms.mappings

import models.{Enumerable, LocalReferenceNumber, Radioable, RichString, Selectable, SelectableList}
import play.api.data.FormError
import play.api.data.format.Formatter

trait Formatters {

  private[mappings] def stringFormatter(errorKey: String, args: Seq[Any] = Seq.empty)(f: String => String): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      val g: String => String = x => f(x.trim)
      data.get(key) match {
        case None                    => Left(Seq(FormError(key, errorKey, args)))
        case Some(s) if g(s).isEmpty => Left(Seq(FormError(key, errorKey, args)))
        case Some(s)                 => Right(g(s))
      }
    }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def booleanFormatter(requiredKey: String, invalidKey: String, args: Seq[Any] = Seq.empty): Formatter[Boolean] =
    new Formatter[Boolean] {

      private val baseFormatter = stringFormatter(requiredKey, args)(identity)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Boolean] =
        baseFormatter
          .bind(key, data)
          .flatMap {
            case "true"  => Right(true)
            case "false" => Right(false)
            case _       => Left(Seq(FormError(key, invalidKey, args)))
          }

      def unbind(key: String, value: Boolean): Map[String, String] = Map(key -> value.toString)
    }

  private[mappings] def enumerableFormatter[A <: Radioable[A]](requiredKey: String, invalidKey: String)(implicit ev: Enumerable[A]): Formatter[A] =
    new Formatter[A] {

      private val baseFormatter = stringFormatter(requiredKey)(identity)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] =
        baseFormatter.bind(key, data).flatMap {
          str =>
            ev.withName(str).map(Right.apply).getOrElse(Left(Seq(FormError(key, invalidKey))))
        }

      override def unbind(key: String, value: A): Map[String, String] =
        baseFormatter.unbind(key, value.code)
    }

  private[mappings] def lrnFormatter(
    requiredKey: String,
    lengthKey: String,
    invalidCharactersKey: String,
    invalidFormatKey: String
  ): Formatter[LocalReferenceNumber] =
    new Formatter[LocalReferenceNumber] {

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalReferenceNumber] =
        stringFormatter(requiredKey)(_.removeSpaces())
          .bind(key, data)
          .flatMap {
            str =>
              if (str.length <= LocalReferenceNumber.maxLength) {
                if (str.startsWith("-") || str.startsWith("_")) {
                  Left(Seq(FormError(key, invalidFormatKey)))
                } else {
                  LocalReferenceNumber(str).map(Right.apply).getOrElse(Left(Seq(FormError(key, invalidCharactersKey))))
                }
              } else {
                Left(Seq(FormError(key, lengthKey)))
              }
          }

      override def unbind(key: String, value: LocalReferenceNumber): Map[String, String] =
        Map(key -> value.toString)
    }

  private[mappings] def selectableFormatter[T <: Selectable](
    selectableList: SelectableList[T],
    errorKey: String,
    args: Seq[Any] = Seq.empty
  ): Formatter[T] = new Formatter[T] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
      lazy val error = Left(Seq(FormError(key, errorKey, args)))
      data.get(key) match {
        case None =>
          error
        case Some(value) =>
          selectableList.values.find(_.value == value) match {
            case Some(selectable) => Right(selectable)
            case None             => error
          }
      }
    }

    override def unbind(key: String, selectable: T): Map[String, String] =
      Map(key -> selectable.value)
  }
}
