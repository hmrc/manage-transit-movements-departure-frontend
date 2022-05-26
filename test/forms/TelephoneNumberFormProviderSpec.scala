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

import forms.behaviours.StringFieldBehaviours
import forms.Constants.maxTelephoneNumberLength
import models.domain.StringFieldRegex.telephoneNumberRegex
import org.scalacheck.Gen
import play.api.data.{Field, FormError}

class TelephoneNumberFormProviderSpec extends StringFieldBehaviours {

  private val prefix   = Gen.alphaNumStr.sample.value
  private val name     = Gen.alphaNumStr.sample.value
  val requiredKey      = s"$prefix.error.required"
  val lengthKey        = s"$prefix.error.length"
  val invalidFormatKey = s"$prefix.error.invalidFormat"

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

    "must not bind strings that do not match regex" in {
      val generator     = stringsWithMaxLength(maxTelephoneNumberLength).retryUntil(!_.matches(telephoneNumberRegex.regex))
      val expectedError = FormError("value", invalidFormatKey, Seq(name))

      forAll(generator) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
