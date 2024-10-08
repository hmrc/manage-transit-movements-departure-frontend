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

package forms.behaviours

import org.scalacheck.Gen
import play.api.data.{Field, Form, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

trait StringFieldBehaviours extends FieldBehaviours {

  def fieldWithMaxLength(form: Form[?], fieldName: String, maxLength: Int, lengthError: FormError): Unit =
    s"must not bind strings longer than $maxLength characters" in {

      forAll(stringsLongerThan(maxLength) -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithMaxLength(
    form: Form[?],
    fieldName: String,
    maxLength: Int,
    lengthError: FormError,
    gen: Gen[String]
  ): Unit =
    s"must not bind strings longer than $maxLength characters" in {

      forAll(gen -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithMinLength(form: Form[?], fieldName: String, minLength: Int, lengthError: FormError): Unit =
    s"must not bind strings shorter than $minLength characters" in {

      forAll(stringsWithLength(minLength - 1) -> "shortString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithInvalidCharacters(form: Form[?], fieldName: String, error: FormError, length: Int = 100): Unit =
    "must not bind strings with invalid characters" in {

      val generator: Gen[String] = RegexpGen.from(s"[!£^*(){}_+=:;|`~<>,±üçñèé]{$length}")

      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(error)
      }
    }

  def mandatoryTrimmedField(form: Form[?], fieldName: String, requiredError: FormError): Unit = {

    mandatoryField(form, fieldName, requiredError)

    "must not bind values that trim to empty" in {

      val result = form.bind(Map(fieldName -> "   ")).apply(fieldName)
      result.errors mustEqual Seq(requiredError)
    }
  }

  def stringFieldWithMaximumIntValue(form: Form[?], fieldName: String, max: Int, fieldMax: Int, expectedError: FormError): Unit =
    s"must not bind values > $max and < $max" in {
      forAll(positiveIntsMinMax(max, fieldMax)) {
        number =>
          val result = form.bind(Map(fieldName -> number.toString)).apply(fieldName)
          result.errors mustEqual Seq(expectedError)
      }
    }

  def stringFieldWithMinimumIntValue(form: Form[?], fieldName: String, min: Int, expectedError: FormError): Unit =
    s"must not bind values < $min" in {
      val testCase = min - 1
      val result   = form.bind(Map(fieldName -> testCase.toString)).apply(fieldName)
      result.errors mustEqual Seq(expectedError)
    }
}
