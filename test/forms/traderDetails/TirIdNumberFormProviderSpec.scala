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
import models.domain.StringFieldRegex.tirIdNumberRegex
import org.scalacheck.Gen
import play.api.data.{Field, FormError}

class TirIdNumberFormProviderSpec extends StringFieldBehaviours with FieldBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value

  private val requiredKey      = s"$prefix.error.required"
  private val invalidFormatKey = s"$prefix.error.invalidFormat"
  private val lengthKey        = s"$prefix.error.length"
  private val form             = new TirIdNumberFormProvider()(prefix)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsThatMatchRegex(tirIdNumberRegex)
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      17,
      FormError(fieldName, lengthKey)
    )

    "must not bind if string doesn't match regex" in {
      val expectedError = FormError(fieldName, invalidFormatKey, Seq(tirIdNumberRegex.regex))

      val gen = nonEmptyString.retryUntil(!_.matches(tirIdNumberRegex.regex))

      forAll(gen) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
