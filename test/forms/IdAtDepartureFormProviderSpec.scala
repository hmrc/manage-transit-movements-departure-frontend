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

import forms.Constants.vehicleIdMaxLength
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class IdAtDepartureFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey = "idAtDeparture.error.required"
  private val lengthKey   = "idAtDeparture.error.length"
  private val invalidKey  = "idAtDeparture.error.invalidCharacters"
  private val form        = new IdAtDepartureFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(vehicleIdMaxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = vehicleIdMaxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(vehicleIdMaxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, vehicleIdMaxLength)

  }
}
