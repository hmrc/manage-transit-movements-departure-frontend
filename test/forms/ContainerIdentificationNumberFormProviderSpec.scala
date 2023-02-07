/*
 * Copyright 2023 HM Revenue & Customs
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
import org.scalacheck.Gen
import play.api.data.FormError

class ContainerIdentificationNumberFormProviderSpec extends StringFieldBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value
  val requiredKey    = s"$prefix.error.required"
  val duplicateKey   = s"$prefix.error.duplicate"
  val lengthKey      = s"$prefix.error.length"
  val maxLength      = 17

  val form = new ContainerIdentificationNumberFormProvider()(prefix, Nil)

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
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind if value exists in the list of other ids" in {
      val otherIds  = Seq("foo", "bar")
      val form      = new ContainerIdentificationNumberFormProvider()(prefix, otherIds)
      val boundForm = form.bind(Map("value" -> "foo"))
      val field     = boundForm("value")
      field.errors mustEqual Seq(FormError(fieldName, duplicateKey))
    }
  }
}
