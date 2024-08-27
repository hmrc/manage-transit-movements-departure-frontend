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

package forms.behaviours

import org.scalacheck.Gen
import play.api.data.{Form, FormError}

trait DoubleFieldBehaviours extends FieldBehaviours {

  def doubleField(form: Form[?], fieldName: String, nonNumericError: FormError): Unit =
    "must not bind non-numeric numbers" in {

      forAll(nonNumerics -> "nonNumeric") {
        nonNumeric =>
          val result = form.bind(Map(fieldName -> nonNumeric)).apply(fieldName)
          result.errors mustEqual Seq(nonNumericError)
      }
    }

  def doubleFieldWithMinimum(form: Form[?], fieldName: String, minimum: Double, expectedError: FormError): Unit =
    s"must not bind values below $minimum" in {

      forAll(doublesBelowValue(minimum) -> "doubleBelowMin") {
        (number: Double) =>
          val result = form.bind(Map(fieldName -> number.toString)).apply(fieldName)
          result.errors mustEqual Seq(expectedError)
      }
    }

  def doubleFieldWithMaximum(form: Form[?], fieldName: String, maxBits: Int, expectedError: FormError): Unit =
    s"must not bind values above 1E$maxBits" in {
      forAll(stringsLongerThan(maxBits, Gen.numChar) -> "doubleAboveMax") {
        numberAsString =>
          val result = form.bind(Map(fieldName -> numberAsString)).apply(fieldName)
          result.errors mustEqual Seq(expectedError)
      }
    }
}
