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

import models.reference.{Country, CustomsOffice}
import models.{CountryList, CustomsOfficeList, Enumerable, LocalReferenceNumber, RichString}
import play.api.data.FormError
import play.api.data.format.Formatter

import scala.util.control.Exception.nonFatalCatch

trait Formatters {

  private[mappings] def stringFormatter(errorKey: String, args: Seq[Any] = Seq.empty): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None | Some("") => Left(Seq(FormError(key, errorKey, args)))
        case Some(s)         => Right(s)
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def trimmedStringFormatter(errorKey: String, args: Seq[Any] = Seq.empty): Formatter[String] = new Formatter[String] {

    private def error(key: String) = Left(Seq(FormError(key, errorKey, args)))

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None                      => error(key)
        case Some(s) if s.trim.isEmpty => error(key)
        case Some(s)                   => Right(s.trim)
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def spacelessStringFormatter(errorKey: String): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      lazy val error = Left(Seq(FormError(key, errorKey)))
      data.get(key) match {
        case None                                => error
        case Some(s) if s.removeSpaces().isEmpty => error
        case Some(s)                             => Right(s.removeSpaces())
      }
    }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def postcodeFormatter(errorKey: String, args: Seq[Any] = Seq.empty): Formatter[String] = new Formatter[String] {

    private def formattedPostcode(string: String) =
      string.patch(string.length - 3, " ", 0)

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {

      val result: Option[String] = data.get(key).map(_.replaceAll(" ", "")).filterNot(_.isEmpty)

      Either.cond(result.isDefined, formattedPostcode(result.get), Seq(FormError(key, errorKey, args)))
    }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def booleanFormatter(requiredKey: String, invalidKey: String, args: Seq[Any] = Seq.empty): Formatter[Boolean] =
    new Formatter[Boolean] {

      private val baseFormatter = stringFormatter(requiredKey, args)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Boolean] =
        baseFormatter
          .bind(key, data)
          .right
          .flatMap {
            case "true"  => Right(true)
            case "false" => Right(false)
            case _       => Left(Seq(FormError(key, invalidKey, args)))
          }

      def unbind(key: String, value: Boolean): Map[String, String] = Map(key -> value.toString)
    }

  private[mappings] def intFormatter(requiredKey: String, wholeNumberKey: String, nonNumericKey: String, args: Seq[String] = Seq.empty): Formatter[Int] =
    new Formatter[Int] {

      val decimalRegexp = """^-?(\d*\.\d*)$"""

      private val baseFormatter = stringFormatter(requiredKey, args)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Int] =
        baseFormatter
          .bind(key, data)
          .right
          .map(_.replace(",", ""))
          .right
          .flatMap {
            case s if s.matches(decimalRegexp) =>
              Left(Seq(FormError(key, wholeNumberKey, args)))
            case s =>
              nonFatalCatch
                .either(s.toInt)
                .left
                .map(
                  _ => Seq(FormError(key, nonNumericKey, args))
                )
          }

      override def unbind(key: String, value: Int): Map[String, String] =
        baseFormatter.unbind(key, value.toString)
    }

  private[mappings] def enumerableFormatter[A](requiredKey: String, invalidKey: String)(implicit ev: Enumerable[A]): Formatter[A] =
    new Formatter[A] {

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] =
        baseFormatter.bind(key, data).right.flatMap {
          str =>
            ev.withName(str).map(Right.apply).getOrElse(Left(Seq(FormError(key, invalidKey))))
        }

      override def unbind(key: String, value: A): Map[String, String] =
        baseFormatter.unbind(key, value.toString)
    }

  private[mappings] def lrnFormatter(
    requiredKey: String,
    lengthKey: String,
    invalidCharactersKey: String,
    invalidFormatKey: String
  ): Formatter[LocalReferenceNumber] =
    new Formatter[LocalReferenceNumber] {

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], LocalReferenceNumber] =
        spacelessStringFormatter(requiredKey)
          .bind(key, data)
          .right
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

  private[mappings] def countryFormatter(
    countryList: CountryList,
    errorKey: String,
    args: Seq[Any] = Seq.empty
  ): Formatter[Country] = new Formatter[Country] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Country] = {
      lazy val error = Left(Seq(FormError(key, errorKey, args)))
      data.get(key) match {
        case None => error
        case Some(code) =>
          countryList.countries.find(_.code.code == code) match {
            case Some(country) => Right(country)
            case None          => error
          }
      }
    }

    override def unbind(key: String, country: Country): Map[String, String] =
      Map(key -> country.code.code)
  }

  private[mappings] def customsOfficeFormatter(
    customsOfficeList: CustomsOfficeList,
    errorKey: String,
    args: Seq[Any] = Seq.empty
  ): Formatter[CustomsOffice] = new Formatter[CustomsOffice] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], CustomsOffice] = {
      lazy val error = Left(Seq(FormError(key, errorKey, args)))
      data.get(key) match {
        case None => error
        case Some(id) =>
          customsOfficeList.customsOffices.find(_.id == id) match {
            case Some(customsOffice) => Right(customsOffice)
            case None                => error
          }
      }
    }

    override def unbind(key: String, customsOffice: CustomsOffice): Map[String, String] =
      Map(key -> customsOffice.id)
  }

  private[mappings] def currencyFormatter(
    requiredKey: String = "error.required",
    invalidCharactersKey: String = "error.invalidCharacters",
    invalidFormatKey: String = "error.invalidFormat",
    invalidValueKey: String = "error.invalidValue"
  ): Formatter[BigDecimal] =
    new Formatter[BigDecimal] {

      private val invalidCharactersRegex = """^[0-9.]*$"""
      private val invalidFormatRegex     = """^[0-9]*(\.[0-9]{1,2})?$"""
      private val invalidValueRegex      = """^[0-9]{0,16}(\.[0-9]{1,2})?$"""

      private val baseFormatter = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], BigDecimal] =
        baseFormatter
          .bind(key, data)
          .right
          .map(_.replace(",", ""))
          .map(_.replace(" ", ""))
          .right
          .flatMap {
            case s if !s.matches(invalidCharactersRegex) => Left(Seq(FormError(key, invalidCharactersKey)))
            case s if !s.matches(invalidFormatRegex)     => Left(Seq(FormError(key, invalidFormatKey)))
            case s if !s.matches(invalidValueRegex)      => Left(Seq(FormError(key, invalidValueKey)))
            case s                                       => Right(BigDecimal(s))
          }

      override def unbind(key: String, value: BigDecimal): Map[String, String] =
        baseFormatter.unbind(key, value.toString())
    }
}
