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

import scala.collection.immutable.ArraySeq

class ItemNumberFormProviderSpec extends StringFieldBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value
  val requiredKey    = s"$prefix.error.required"
  val lengthKey      = s"$prefix.error.length"
  val rangeKey       = s"$prefix.error.range"
  val maxLength      = 4
  val maxLengthValue = 9999
  val maxValue       = 1999
  val minValue       = 1

  val form = new ItemNumberFormProvider()(prefix)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength, Gen.numChar)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength)),
      gen = stringsLongerThan(maxLength, Gen.numChar)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like stringFieldWithMaximumIntValue(
      form,
      fieldName,
      maxValue,
      maxLengthValue,
      FormError(fieldName, rangeKey, ArraySeq(maxValue))
    )

    behave like stringFieldWithMinimumIntValue(
      form,
      fieldName,
      minValue,
      FormError(fieldName, rangeKey, ArraySeq(minValue - 1))
    )

    "when value breaks multiple validation rules" - {
      "should only return one error" in {
        val result = form.bind(Map(fieldName -> "123565rt")).apply(fieldName)
        result.errors.size mustBe 1
      }
    }
  }
}
