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

import forms.Constants.maxRefNumberLength
import forms.behaviours.StringFieldBehaviours
import models.domain.StringFieldRegex.{alphaNumericRegex, referenceNumberFormatRegex}
import org.scalacheck.Gen
import play.api.data.FormError

class GuaranteeReferenceNumberFormProviderSpec extends StringFieldBehaviours {

  private val prefix                 = Gen.alphaNumStr.sample.value
  private val invalidFormatRefNumber = Gen.alphaNumStr.sample.value.take(maxRefNumberLength)

  val requiredKey = s"$prefix.error.required"
  val lengthKey   = s"$prefix.error.length"
  val invalidKey  = s"$prefix.error.invalid"
  val formatKey   = s"$prefix.error.format"

  val form = new GuaranteeReferenceNumberFormProvider()(prefix)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxRefNumberLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxRefNumberLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxRefNumberLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidCharacters(
      form,
      fieldName,
      error = FormError(fieldName, invalidKey, Seq(alphaNumericRegex.regex)),
      maxRefNumberLength
    )

    behave like fieldThatDoesNotBindInvalidData(
      form = form,
      fieldName = fieldName,
      regex = referenceNumberFormatRegex.regex,
      gen = invalidFormatRefNumber,
      invalidKey = formatKey
    )

    "must remove spaces on bound strings" in {
      val result = form.bind(Map(fieldName -> "  01  GB1  234  567890120A  123456  "))
      result.errors mustEqual Nil
      result.get mustEqual "01GB1234567890120A123456"
    }
  }
}
