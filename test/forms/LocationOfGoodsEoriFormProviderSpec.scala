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

import forms.Constants.{maxEoriNumberLength, minEoriNumberLength}
import forms.behaviours.StringFieldBehaviours
import models.LocalReferenceNumber.maxLength
import models.domain.StringFieldRegex.{alphaNumericRegex, eoriNumberRegex}
import org.scalacheck.Gen
import play.api.data.{Field, FormError}

class LocationOfGoodsEoriFormProviderSpec extends StringFieldBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value

  private val requiredKey          = s"$prefix.error.required"
  private val maxLengthKey         = s"$prefix.error.maxLength"
  private val minLengthKey         = s"$prefix.error.minLength"
  private val invalidCharactersKey = s"$prefix.error.invalidCharacters"
  private val invalidFormatKey     = s"$prefix.error.invalidFormat"

  val form = new LocationOfGoodsEoriFormProvider()(prefix)

  private val validPrefixes = Seq("GB", "gb", "Gb", "gB", "XI", "xi", "Xi", "xI")
  private val prefixGen     = Gen.oneOf(validPrefixes)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
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
      gen = stringsWithLength(maxEoriNumberLength),
      invalidKey = invalidCharactersKey
    )

    "must not bind strings with correct prefix and suffix but over max length" in {
      val expectedError = FormError(fieldName, maxLengthKey, Seq(maxEoriNumberLength))

      val gen = for {
        prefix <- prefixGen
        suffix <- stringsLongerThan(maxEoriNumberLength - prefix.length, Gen.numChar)
      } yield prefix + suffix

      forAll(gen) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings with correct prefix and suffix but under min length" in {
      val expectedError = FormError(fieldName, minLengthKey, Seq(minEoriNumberLength))

      val gen = for {
        prefix <- prefixGen
        suffix <- stringsWithMaxLength(minEoriNumberLength - prefix.length - 1, Gen.numChar)
      } yield prefix + suffix

      forAll(gen) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings with correct prefix but invalid suffix" in {
      val expectedError = FormError(fieldName, invalidFormatKey, Seq(eoriNumberRegex.regex))

      val gen = for {
        prefix <- prefixGen
        suffix <- stringsLongerThan(maxEoriNumberLength - prefix.length, Gen.alphaNumChar)
      } yield prefix + suffix

      forAll(gen) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
