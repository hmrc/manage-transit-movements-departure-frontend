/*
 * Copyright 2024 HM Revenue & Customs
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
import org.scalacheck.Gen
import play.api.data.{Form, FormError}

class LocalReferenceNumberFormProviderSpec extends StringFieldBehaviours {

  private val formProvider = new LocalReferenceNumberFormProvider()
  private val prefix       = Gen.oneOf(Seq("localReferenceNumber", "newLocalReferenceNumber")).sample.value

  private val requiredKey          = s"$prefix.error.required"
  private val lengthKey            = s"$prefix.error.length"
  private val invalidCharactersKey = s"$prefix.error.invalidCharacters"
  private val invalidFormatKey     = s"$prefix.error.invalidFormat"

  private val form: Form[LocalReferenceNumber] = formProvider(prefix)

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
          val valueStartingWithUnderscore = s"_$value"
          val result                      = form.bind(Map(fieldName -> valueStartingWithUnderscore))
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

    "must allow spaces but remove them" in {
      forAll(arbitrary[LocalReferenceNumber].map(_.toString)) {
        value =>
          val valueWithSpaces = value
            .map(
              char => s" $char "
            )
            .mkString
          val result = form.bind(Map(fieldName -> valueWithSpaces))
          result.value.value mustEqual LocalReferenceNumber(value).get
      }
    }
  }
}
