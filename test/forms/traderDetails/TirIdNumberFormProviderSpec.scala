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

package forms.traderDetails

import forms.behaviours.{FieldBehaviours, StringFieldBehaviours}
import models.domain.StringFieldRegex.{tirIdNumberCharacterRegex, tirIdNumberFormatRegex}
import org.scalacheck.Gen
import play.api.data.{Field, FormError}

class TirIdNumberFormProviderSpec extends StringFieldBehaviours with FieldBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value

  private val requiredKey         = s"$prefix.error.required"
  private val invalidFormatKey    = s"$prefix.error.invalidFormat"
  private val invalidCharacterKey = s"$prefix.error.invalidCharacter"
  private val lengthKey           = s"$prefix.error.length"
  private val form                = new TirIdNumberFormProvider()(prefix)

  private val tirMaxLength = 17

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsThatMatchRegex(tirIdNumberFormatRegex)
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      tirMaxLength,
      FormError(fieldName, lengthKey, Seq(tirMaxLength))
    )

    "must not bind strings that do not match character regex" in {

      val invalidCharacters = Gen.oneOf(Seq("%", "(", ")", "£", "!", "$", "^", "&"))

      val expectedError = FormError(fieldName, invalidCharacterKey, Seq(tirIdNumberCharacterRegex.regex))

      forAll(invalidCharacters) {
        invalidCharacter =>
          val result: Field = form.bind(Map(fieldName -> invalidCharacter)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind if string doesn't match format regex" in {

      val invalidFormats = Gen.oneOf(
        Seq(
          "AAA999/99999",
          "AAA99999999",
          "AAA/99999999",
          "/AAA/999/9999"
        )
      )

      val expectedError = FormError(fieldName, invalidFormatKey, Seq(tirIdNumberFormatRegex.regex))

      forAll(invalidFormats) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
