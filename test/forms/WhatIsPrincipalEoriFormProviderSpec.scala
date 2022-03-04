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

import forms.Constants._
import forms.behaviours.StringFieldBehaviours
import models.domain.StringFieldRegex.eoriNumberRegex
import models.reference.CountryCode
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class WhatIsPrincipalEoriFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey         = "whatIsPrincipalEori.error.required"
  private val lengthKey           = "whatIsPrincipalEori.error.length"
  private val invalidFormatKey    = "whatIsPrincipalEori.error.invalidFormat"
  private val invalidPrefixKey    = "whatIsPrincipalEori.error.prefix"
  private val invalidCharacterKey = "whatIsPrincipalEori.error.invalidCharacters"

  val formGBSimplified = new WhatIsPrincipalEoriFormProvider()(true, CountryCode("GB"))
  val formXISimplified = new WhatIsPrincipalEoriFormProvider()(true, CountryCode("XI"))
  val form             = new WhatIsPrincipalEoriFormProvider()(false, CountryCode("XI"))

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLengthEoriNumber)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLengthEoriNumber,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLengthEoriNumber))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidCharacterKey, maxLengthEoriNumber)

    "must not bind strings that do not match the eoriNumber format regex" in {
      val expectedError          = FormError(fieldName, invalidFormatKey, Seq(eoriNumberRegex))
      val generator: Gen[String] = RegexpGen.from("^[a-zA-Z]{1}[0-9]{1}[0-9a-zA-Z]{1,13}")
      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind when Principal EORI starts with 'XI' but Office of Departure starts with 'GB' " in {
      val expectedError          = FormError(fieldName, invalidPrefixKey, Seq("GB"))
      val generator: Gen[String] = RegexpGen.from("^[X][I][0-9]{5}")
      forAll(generator) {

        invalidString =>
          val result: Field = formGBSimplified.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind when Principal EORI starts with 'GB' but Office of Departure starts with 'XI' " in {
      val expectedError          = FormError(fieldName, invalidPrefixKey, Seq("XI"))
      val generator: Gen[String] = RegexpGen.from("^[G][B][0-9]{5}")
      forAll(generator) {

        invalidString =>
          val result: Field = formXISimplified.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

  }
}
