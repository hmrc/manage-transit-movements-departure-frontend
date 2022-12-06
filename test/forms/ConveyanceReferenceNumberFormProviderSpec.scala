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

import forms.Constants.conveyanceNumberLength
import forms.behaviours.StringFieldBehaviours
import forms.transport.transportMeans.active.ConveyanceReferenceNumberFormProvider
import models.domain.StringFieldRegex.alphaNumericRegex
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.data.FormError

class ConveyanceReferenceNumberFormProviderSpec extends StringFieldBehaviours {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val requiredKey = s"$prefix.error.required"
  private val lengthKey   = s"$prefix.error.length"
  private val invalidKey  = s"$prefix.error.invalid"

  val form = new ConveyanceReferenceNumberFormProvider()(prefix)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(conveyanceNumberLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = conveyanceNumberLength,
      lengthError = FormError(fieldName, lengthKey, Seq(conveyanceNumberLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldThatDoesNotBindInvalidData(
      form = form,
      fieldName = fieldName,
      regex = alphaNumericRegex.regex,
      gen = stringsWithLength(conveyanceNumberLength, arbitrary[Char]),
      invalidKey = invalidKey
    )
  }
}
