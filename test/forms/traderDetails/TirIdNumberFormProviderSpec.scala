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

package forms.traderDetails

import forms.Constants._
import forms.behaviours.{FieldBehaviours, StringFieldBehaviours}
import models.domain.StringFieldRegex.tirIdNumberRegex
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.{Field, FormError}

class TirIdNumberFormProviderSpec extends StringFieldBehaviours with FieldBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value

  private val requiredKey      = s"$prefix.error.required"
  private val maxLengthKey     = s"$prefix.error.maxLength"
  private val invalidFormatKey = s"$prefix.error.invalidFormat"

  private val form = new TirIdNumberFormProvider()(prefix)

  implicit lazy val prefixGen: Arbitrary[String] =
    Arbitrary {
      for {
        startString <- stringsWithLength(3, Gen.alphaChar)
        middleNos   <- stringsWithLength(3, Gen.numChar)
      } yield s"$startString/$middleNos/"
    }

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(maxTirIdNumberLength)
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind strings with correct prefix and suffix but over max length" in {
      val expectedError = FormError(fieldName, maxLengthKey, Seq(maxTirIdNumberLength))

      val gen = for {
        prefix <- prefixGen.arbitrary
        suffix <- stringsLongerThan(maxTirIdNumberLength - prefix.length, Gen.numChar)
      } yield prefix + suffix

      forAll(gen) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings with correct prefix but invalid suffix" in {
      val expectedError = FormError(fieldName, invalidFormatKey, Seq(tirIdNumberRegex.regex))

      val gen = for {
        prefix <- prefixGen.arbitrary
        suffix <- stringsLongerThan(maxTirIdNumberLength - prefix.length, Gen.alphaChar)
      } yield prefix + suffix

      forAll(gen) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }

    "must not bind strings with wrong prefix" in {
      val expectedError = FormError(fieldName, invalidFormatKey, Seq(tirIdNumberRegex.regex))

      val gen = for {
        prefix <- stringsWithLength(7, Gen.alphaChar)
        suffix <- stringsWithLength(maxTirIdNumberLength - prefix.length, Gen.numChar)
      } yield prefix + suffix

      forAll(gen) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
