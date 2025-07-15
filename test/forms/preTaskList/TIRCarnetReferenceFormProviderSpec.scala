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

package forms.preTaskList

import forms.Constants.tirCarnetReferenceMaxLength
import forms.behaviours.StringFieldBehaviours
import models.domain.StringFieldRegex._
import play.api.data.{Field, FormError}

class TIRCarnetReferenceFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey          = "tirCarnetReference.error.required"
  private val maxLengthKey         = "tirCarnetReference.error.length"
  private val invalidCharactersKey = "tirCarnetReference.error.invalidCharacters"
  private val invalidFormatKey     = "tirCarnetReference.error.invalidFormat"
  private val maxLength            = tirCarnetReferenceMaxLength
  private val form                 = new TIRCarnetReferenceFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, maxLengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidCharacters(
      form,
      fieldName,
      error = FormError(fieldName, invalidCharactersKey, Seq(alphaNumericRegex.regex)),
      maxLength
    )

    "must not bind strings with an invalid format" in {
      val str               = "TIRREF"
      val result: Field     = form.bind(Map(fieldName -> str)).apply(fieldName)
      val expectedFormError = FormError(fieldName, invalidFormatKey, Seq(tirCarnetNumberRegex.regex))
      result.errors must contain(expectedFormError)
    }

    "must bind valid strings that match" - {

      "[1-9][0-9]{0,6}" in {
        val strs = Seq(
          "3",
          "55",
          "956",
          "4009",
          "50677",
          "748516",
          "2085451"
        )

        strs.foreach {
          str =>
            val result = form.bind(Map(fieldName -> str)).apply(fieldName)
            result.value.value mustEqual str
        }
      }

      "(1[0-9]|2[0-4])[0-9]{0,6}" in {
        val strs = Seq(
          "24",
          "239",
          "1859",
          "20170",
          "109623",
          "1215631",
          "23357415"
        )

        strs.foreach {
          str =>
            val result = form.bind(Map(fieldName -> str)).apply(fieldName)
            result.value.value mustEqual str
        }
      }

      "25000000" in {
        val str    = "25000000"
        val result = form.bind(Map(fieldName -> str)).apply(fieldName)
        result.value.value mustEqual str
      }

      "(X[A-Z]|[A-Z]X)(2[5-9]|[3-9][0-9]|[1-9][0-9][0-9])[0-9]{6}" in {
        val strs = Seq(
          "XE28707033",
          "XX959792193",
          "UX943976499",
          "FX97370960",
          "XF28702846",
          "HX877242599"
        )

        strs.foreach {
          str =>
            val result = form.bind(Map(fieldName -> str)).apply(fieldName)
            result.value.value mustEqual str
        }
      }
    }

    "must bing strings with spaces" in {
      val result = form.bind(Map(fieldName -> "HX877242599   "))
      result.value.value mustEqual "HX877242599"
    }

    "must bind lowercase string and convert it to uppercase" in {
      val result = form.bind(Map(fieldName -> "xe28707033"))
      result.value.value mustEqual "XE28707033"
    }
  }
}
