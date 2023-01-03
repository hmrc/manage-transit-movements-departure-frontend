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

package forms

import forms.behaviours.DoubleFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class MoneyFormProviderSpec extends DoubleFieldBehaviours {

  private val prefix               = Gen.alphaNumStr.sample.value
  private val requiredKey          = s"$prefix.error.required"
  private val invalidCharactersKey = s"$prefix.error.invalidCharacters"
  private val invalidFormatKey     = s"$prefix.error.invalidFormat"
  private val invalidValueKey      = s"$prefix.error.invalidValue"

  private val maxBits = 16

  private val form = new MoneyFormProvider()(prefix)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxBits, Gen.numChar)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like doubleField(
      form = form,
      fieldName = fieldName,
      nonNumericError = FormError(fieldName, invalidCharactersKey)
    )

    behave like doubleFieldWithMaximum(
      form = form,
      fieldName = fieldName,
      maxBits = maxBits,
      expectedError = FormError(fieldName, invalidValueKey)
    )

    behave like doubleFieldWithMinimum(
      form = form,
      fieldName = fieldName,
      minimum = 0,
      expectedError = FormError(fieldName, invalidCharactersKey)
    )

    "must not allow more than 2 decimal places" in {
      val result = form.bind(Map(fieldName -> "1000.000"))
      result.errors mustEqual Seq(FormError(fieldName, invalidFormatKey))
    }

    "must allow commas but remove them" - {
      "when there are decimal places" in {
        val result = form.bind(Map(fieldName -> "1,000.00"))
        result.errors mustEqual Nil
        result.get mustEqual 1000.00
      }

      "when there are no decimal places" in {
        val result = form.bind(Map(fieldName -> "1,000"))
        result.errors mustEqual Nil
        result.get mustEqual 1000
      }
    }

    "must allow spaces but remove them" - {
      "when there are decimal places" in {
        val result = form.bind(Map(fieldName -> "1 000.00"))
        result.errors mustEqual Nil
        result.get mustEqual 1000.00
      }

      "when there are no decimal places" in {
        val result = form.bind(Map(fieldName -> "1 000"))
        result.errors mustEqual Nil
        result.get mustEqual 1000
      }
    }
  }
}
