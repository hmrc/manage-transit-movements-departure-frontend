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

import forms.Constants.{maxTelephoneNumberLength, minTelephoneNumberLength}
import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.{Field, FormError}

class TelephoneNumberFormProviderSpec extends StringFieldBehaviours {

  private val prefix              = Gen.alphaNumStr.sample.value
  private val name                = Gen.alphaNumStr.sample.value
  private val requiredKey         = s"$prefix.error.required"
  private val maxLengthKey        = s"$prefix.error.maxLength"
  private val minLengthKey        = s"$prefix.error.minLength"
  private val invalidFormatKey    = s"$prefix.error.invalidFormat"
  private val invalidCharacterKey = s"$prefix.error.invalidCharacters"

  val form = new TelephoneNumberFormProvider()(prefix)

  ".value" - {

    val fieldName = "value"

    "with form name" - {
      val form = new TelephoneNumberFormProvider()(prefix, name)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(maxTelephoneNumberLength, Gen.numChar)
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(name))
      )

      s"must not bind strings longer than $maxTelephoneNumberLength characters" in {

        val stringInt = "9" * maxTelephoneNumberLength

        val expectedError = FormError(fieldName, maxLengthKey, Seq(name, maxTelephoneNumberLength))

        val result = form.bind(Map(fieldName -> ("+" + stringInt))).apply(fieldName)
        result.errors mustEqual Seq(expectedError)

      }

      s"must not bind strings less than $minTelephoneNumberLength characters" in {

        val genInt = intsInsideRange(0, 9999).sample.value

        val expectedError = FormError(fieldName, minLengthKey, Seq(name, minTelephoneNumberLength))

        val result = form.bind(Map(fieldName -> ("+" + genInt))).apply(fieldName)
        result.errors mustEqual Seq(expectedError)

      }

      "must not bind strings that do not match character regex" in {

        val invalidCharacters = Gen.oneOf(Seq("%", "£", "!", "$", "^", "&", "(", ")"))

        val expectedError = FormError("value", invalidCharacterKey, Seq(name))

        forAll(invalidCharacters) {
          invalidCharacter =>
            val result: Field = form.bind(Map(fieldName -> invalidCharacter)).apply(fieldName)
            result.errors must contain(expectedError)
        }
      }

      "must not bind strings that do not match format regex" in {

        val expectedError = FormError("value", invalidFormatKey, Seq(name))

        val result: Field = form.bind(Map(fieldName -> "123456")).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }

    "without form name" - {
      val form = new TelephoneNumberFormProvider()(prefix)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(maxTelephoneNumberLength, Gen.numChar)
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, requiredKey)
      )

      s"must not bind strings longer than $maxTelephoneNumberLength characters" in {

        val stringInt = "9" * maxTelephoneNumberLength

        val expectedError = FormError(fieldName, maxLengthKey, Seq(maxTelephoneNumberLength))

        val result = form.bind(Map(fieldName -> ("+" + stringInt))).apply(fieldName)
        result.errors mustEqual Seq(expectedError)

      }

      s"must not bind strings less than $minTelephoneNumberLength characters" in {

        val genInt = intsInsideRange(0, 9999).sample.value

        val expectedError = FormError(fieldName, minLengthKey, Seq(minTelephoneNumberLength))

        val result = form.bind(Map(fieldName -> ("+" + genInt))).apply(fieldName)
        result.errors mustEqual Seq(expectedError)

      }

      "must not bind strings that do not match character regex" in {

        val invalidCharacters = Gen.oneOf(Seq("%", "£", "!", "$", "^", "&", "(", ")"))

        val expectedError = FormError("value", invalidCharacterKey)

        forAll(invalidCharacters) {
          invalidCharacter =>
            val result: Field = form.bind(Map(fieldName -> invalidCharacter)).apply(fieldName)
            result.errors must contain(expectedError)
        }
      }

      "must not bind strings that do not match format regex" in {

        val expectedError = FormError("value", invalidFormatKey)

        val result: Field = form.bind(Map(fieldName -> "123456")).apply(fieldName)
        result.errors must contain(expectedError)
      }
    }
  }
}
