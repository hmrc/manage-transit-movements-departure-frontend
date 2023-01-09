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

import models.reference.{Country, CustomsOffice, Nationality, UnLocode}
import models.{CountryList, CustomsOfficeList, Enumerable, LocalReferenceNumber, NationalityList, UnLocodeList}
import play.api.data.FieldMapping
import play.api.data.Forms.of
import play.api.data.format.Formats.ignoredFormat

import java.time.{LocalDate, LocalTime}

trait Mappings extends Formatters with Constraints {

  protected def text(errorKey: String = "error.required", args: Seq[Any] = Seq.empty): FieldMapping[String] =
    of(stringFormatter(errorKey, args))

  protected def trimmedText(errorKey: String = "error.required", args: Seq[Any] = Seq.empty): FieldMapping[String] =
    of(trimmedStringFormatter(errorKey, args))

  protected def textWithSpacesRemoved(errorKey: String = "error.required"): FieldMapping[String] =
    of(spacelessStringFormatter(errorKey))

  protected def formattedPostcode(errorKey: String = "error.required", args: Seq[Any] = Seq.empty): FieldMapping[String] =
    of(postcodeFormatter(errorKey, args))

  protected def mandatoryIfBoolean(errorKey: String = "error.required", condition: Boolean, defaultValue: Boolean): FieldMapping[Boolean] =
    if (condition) boolean(errorKey) else of(ignoredFormat(defaultValue))

  protected def int(
    requiredKey: String = "error.required",
    wholeNumberKey: String = "error.wholeNumber",
    nonNumericKey: String = "error.nonNumeric",
    args: Seq[String] = Seq.empty[String]
  ): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def boolean(requiredKey: String = "error.required", invalidKey: String = "error.boolean", args: Seq[Any] = Seq.empty): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey, args))

  protected def enumerable[A](requiredKey: String = "error.required", invalidKey: String = "error.invalid")(implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey))

  protected def lrn(
    requiredKey: String,
    lengthKey: String,
    invalidCharactersKey: String,
    invalidFormatKey: String
  ): FieldMapping[LocalReferenceNumber] =
    of(lrnFormatter(requiredKey, lengthKey, invalidCharactersKey, invalidFormatKey))

  protected def localDate(
    invalidKey: String,
    allRequiredKey: String,
    twoRequiredKey: String,
    requiredKey: String,
    args: Seq[String] = Seq.empty
  ): FieldMapping[LocalDate] =
    of(new LocalDateFormatter(invalidKey, allRequiredKey, twoRequiredKey, requiredKey, args))

  protected def localTime(
    invalidKey: String,
    allRequiredKey: String,
    requiredKey: String,
    args: Seq[String] = Seq.empty
  ): FieldMapping[LocalTime] =
    of(new LocalTimeFormatter(invalidKey, allRequiredKey, requiredKey, args))

  protected def country(
    countryList: CountryList,
    errorKey: String = "error.required",
    args: Seq[Any] = Seq.empty
  ): FieldMapping[Country] =
    of(countryFormatter(countryList, errorKey, args))

  protected def customsOffice(
    customsOfficeList: CustomsOfficeList,
    errorKey: String = "error.required",
    args: Seq[Any] = Seq.empty
  ): FieldMapping[CustomsOffice] =
    of(customsOfficeFormatter(customsOfficeList, errorKey, args))

  protected def unLocode(
    unLocodeList: UnLocodeList,
    errorKey: String = "error.required",
    args: Seq[Any] = Seq.empty
  ): FieldMapping[UnLocode] =
    of(unLocodeFormatter(unLocodeList, errorKey, args))

  protected def currency(
    requiredKey: String = "error.required",
    invalidCharactersKey: String = "error.invalidCharacters",
    invalidFormatKey: String = "error.invalidFormat",
    invalidValueKey: String = "error.invalidValue"
  ): FieldMapping[BigDecimal] =
    of(currencyFormatter(requiredKey, invalidCharactersKey, invalidFormatKey, invalidValueKey))

  protected def nationality(
    nationalityList: NationalityList,
    errorKey: String = "error.required",
    args: Seq[Any] = Seq.empty
  ): FieldMapping[Nationality] =
    of(nationalityFormatter(nationalityList, errorKey, args))
}
