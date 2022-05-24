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

package forms.preTaskList

import forms.behaviours.StringFieldBehaviours
import models.LocalReferenceNumber
import models.LocalReferenceNumber.maxLength
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.FormError

class LocalReferenceNumberFormProviderSpec extends StringFieldBehaviours {

  private val requiredKey          = "localReferenceNumber.error.required"
  private val lengthKey            = "localReferenceNumber.error.length"
  private val invalidCharactersKey = "localReferenceNumber.error.invalidCharacters"
  private val invalidFormatKey     = "localReferenceNumber.error.invalidFormat"

  private val form = new LocalReferenceNumberFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      arbitrary[LocalReferenceNumber].map(_.toString)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not start with a hyphen" in {
      forAll(stringsWithMaxLength(maxLength - 1)) {
        value =>
          val valueStartingWithHyphen = s"-$value"
          val result                  = form.bind(Map(fieldName -> valueStartingWithHyphen))
          result.errors must contain(FormError(fieldName, invalidFormatKey))
      }
    }

    "must not start with an underscore" in {
      forAll(stringsWithMaxLength(maxLength - 1)) {
        value =>
          val valueStartingWithHyphen = s"_$value"
          val result                  = form.bind(Map(fieldName -> valueStartingWithHyphen))
          result.errors must contain(FormError(fieldName, invalidFormatKey))
      }
    }

    "must not bind invalid LRNs" in {

      forAll(arbitrary[String]) {
        value =>
          whenever(value != "" && LocalReferenceNumber(value).isEmpty) {

            val result = form.bind(Map(fieldName -> value))
            if (value.length > maxLength) {
              result.errors must contain(FormError(fieldName, lengthKey))
            } else {
              result.errors must contain(FormError(fieldName, invalidCharactersKey))
            }
          }
      }
    }

  }
}
