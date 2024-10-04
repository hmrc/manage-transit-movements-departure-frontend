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

package forms.mappings

import models.{Enumerable, LocalReferenceNumber, Radioable, Selectable, SelectableList}
import play.api.data.FieldMapping
import play.api.data.Forms.of

trait Mappings extends Formatters with Constraints {

  protected def text(errorKey: String = "error.required", args: Seq[Any] = Seq.empty): FieldMapping[String] =
    adaptedText(errorKey, args)(identity)

  protected def adaptedText(errorKey: String = "error.required", args: Seq[Any] = Seq.empty)(f: String => String): FieldMapping[String] =
    of(stringFormatter(errorKey, args)(f))

  protected def formattedPostcode(errorKey: String = "error.required", args: Seq[Any] = Seq.empty): FieldMapping[String] =
    of(postcodeFormatter(errorKey, args))

  protected def int(
    requiredKey: String = "error.required",
    wholeNumberKey: String = "error.wholeNumber",
    nonNumericKey: String = "error.nonNumeric",
    args: Seq[String] = Seq.empty[String]
  ): FieldMapping[Int] =
    of(intFormatter(requiredKey, wholeNumberKey, nonNumericKey, args))

  protected def boolean(requiredKey: String = "error.required", invalidKey: String = "error.boolean", args: Seq[Any] = Seq.empty): FieldMapping[Boolean] =
    of(booleanFormatter(requiredKey, invalidKey, args))

  protected def enumerable[A <: Radioable[A]](
    requiredKey: String = "error.required",
    invalidKey: String = "error.invalid"
  )(implicit ev: Enumerable[A]): FieldMapping[A] =
    of(enumerableFormatter[A](requiredKey, invalidKey))

  protected def lrn(
    requiredKey: String,
    lengthKey: String,
    invalidCharactersKey: String,
    invalidFormatKey: String
  ): FieldMapping[LocalReferenceNumber] =
    of(lrnFormatter(requiredKey, lengthKey, invalidCharactersKey, invalidFormatKey))

  protected def selectable[T <: Selectable](
    selectableList: SelectableList[T],
    errorKey: String = "error.required",
    args: Seq[Any] = Seq.empty
  ): FieldMapping[T] =
    of(selectableFormatter[T](selectableList, errorKey, args))
}
