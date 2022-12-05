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

package forms.transport.transportMeans.active

import base.SpecBase
import forms.Constants.identificationNumberLength
import forms.behaviours.StringFieldBehaviours
import models.transport.transportMeans.departure.Identification
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.data.FormError

class IdentificationNumberFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val identificationType = arbitrary[Identification].sample.value
  private val prefix             = Gen.alphaNumStr.sample.value

  private val dynamicTitle = s"$prefix.${identificationType.toString}"
  private val requiredKey  = s"$prefix.error.required"
  private val lengthKey    = s"$prefix.error.length"
  private val invalidKey   = s"$prefix.error.invalid"

  val form = new IdentificationNumberFormProvider()(prefix, dynamicTitle)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(identificationNumberLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = identificationNumberLength,
      lengthError = FormError(fieldName, lengthKey, Seq(dynamicTitle))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(dynamicTitle))
    )

    behave like fieldWithInvalidCharacters(
      form,
      fieldName,
      error = FormError(fieldName, invalidKey, Seq(dynamicTitle)),
      identificationNumberLength
    )
  }
}
