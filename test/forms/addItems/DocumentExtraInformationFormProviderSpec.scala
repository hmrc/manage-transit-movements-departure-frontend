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

package forms.addItems

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class DocumentExtraInformationFormProviderSpec extends SpecBase with StringFieldBehaviours {

  private val requiredKey = "documentExtraInformation.error.required"
  private val lengthKey   = "documentExtraInformation.error.length"
  private val maxLength   = 26
  private val invalidKey  = "documentExtraInformation.error.invalid"

  val form = new DocumentExtraInformationFormProvider()(index)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(index.display))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(index.display))
    )

    behave like fieldWithInvalidCharacters(form, fieldName, invalidKey, maxLength)
  }
}
