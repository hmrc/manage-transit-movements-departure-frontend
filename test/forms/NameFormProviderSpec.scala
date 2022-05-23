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

import forms.Constants.maxNameLength
import forms.behaviours.StringFieldBehaviours
import models.domain.StringFieldRegex.stringFieldRegex
import play.api.data.FormError

class NameFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey = "traderDetails.holderOfTransit.name.error.required"
  private val lengthKey   = "traderDetails.holderOfTransit.name.error.length"
  private val invalidKey  = "traderDetails.holderOfTransit.name.error.invalid"

  val form = new NameFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxNameLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxNameLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxNameLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nameFieldWithInvalidCharacters(
      form,
      fieldName,
      error = FormError(fieldName, invalidKey, Seq(stringFieldRegex.regex))
    )
  }
}
