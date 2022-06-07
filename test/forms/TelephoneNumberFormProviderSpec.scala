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

package forms

import forms.Constants.maxTelephoneNumberLength
import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.{Field, FormError}

class TelephoneNumberFormProviderSpec extends StringFieldBehaviours {

  private val prefix              = Gen.alphaNumStr.sample.value
  private val name                = Gen.alphaNumStr.sample.value
  private val requiredKey         = s"$prefix.error.required"
  private val lengthKey           = s"$prefix.error.length"
  private val invalidFormatKey    = s"$prefix.error.invalidFormat"
  private val invalidCharacterKey = s"$prefix.error.invalidCharacter"

  val form = new TelephoneNumberFormProvider()(prefix, name)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxTelephoneNumberLength, Gen.numChar)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxTelephoneNumberLength,
      lengthError = FormError(fieldName, lengthKey, Seq(name, maxTelephoneNumberLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(name))
    )

    "must not bind strings that do not match character regex" in {

      val invalidCharacters = Gen.oneOf(Seq("%", "£", "!", "$", "^", "&"))

      val expectedError = FormError("value", invalidCharacterKey, Seq(name))

      forAll(invalidCharacters) {
        invalidCharacter =>
          val result: Field = form.bind(Map(fieldName -> invalidCharacter)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings that do not match format regex" in {

      val invalidFormat: Gen[String] = genNumberString.retryUntil(_.length < maxTelephoneNumberLength)

      val expectedError = FormError("value", invalidFormatKey, Seq(name))

      forAll(invalidFormat) {
        invalidNumber =>
          val result: Field = form.bind(Map(fieldName -> invalidNumber)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
