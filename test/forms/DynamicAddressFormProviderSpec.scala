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

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.AddressLine._
import org.scalacheck.Gen
import play.api.data.FormError

class DynamicAddressFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val prefix = Gen.alphaNumStr.sample.value
  private val name   = Gen.alphaNumStr.sample.value

  private val requiredKey = s"$prefix.error.required"
  private val lengthKey   = s"$prefix.error.length"
  private val invalidKey  = s"$prefix.error.invalid"

  "when postal code is required" - {

    val form = new DynamicAddressFormProvider().apply(prefix, name, isPostalCodeRequired = true)

    ".numberAndStreet" - {

      val fieldName = NumberAndStreet.field

      behave like fieldThatBindsValidData(
        form = form,
        fieldName = fieldName,
        validDataGenerator = stringsWithMaxLength(NumberAndStreet.length)
      )

      behave like fieldWithMaxLength(
        form = form,
        fieldName = fieldName,
        maxLength = NumberAndStreet.length,
        lengthError = FormError(fieldName, lengthKey, Seq(NumberAndStreet.arg.capitalize, name, NumberAndStreet.length))
      )

      behave like mandatoryTrimmedField(
        form = form,
        fieldName = fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(NumberAndStreet.arg, name))
      )

      behave like fieldWithInvalidCharacters(
        form = form,
        fieldName = fieldName,
        error = FormError(fieldName, invalidKey, Seq(NumberAndStreet.arg.capitalize, name)),
        length = NumberAndStreet.length
      )
    }

    ".city" - {

      val fieldName = City.field

      behave like fieldThatBindsValidData(
        form = form,
        fieldName = fieldName,
        validDataGenerator = stringsWithMaxLength(City.length)
      )

      behave like fieldWithMaxLength(
        form = form,
        fieldName = fieldName,
        maxLength = City.length,
        lengthError = FormError(fieldName, lengthKey, Seq(City.arg.capitalize, name, City.length))
      )

      behave like mandatoryTrimmedField(
        form = form,
        fieldName = fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(City.arg, name))
      )

      behave like fieldWithInvalidCharacters(
        form = form,
        fieldName = fieldName,
        error = FormError(fieldName, invalidKey, Seq(City.arg.capitalize, name)),
        length = City.length
      )
    }

    ".postalCode" - {

      val postcodeInvalidKey    = s"$prefix.error.postalCode.invalid"
      val postalCodeRequiredKey = s"$prefix.error.postalCode.required"
      val lengthKey             = s"$prefix.error.postalCode.length"

      val fieldName = PostalCode.field

      val invalidPostalOverLength = stringsLongerThan(PostalCode.length + 1)

      behave like fieldThatBindsValidData(
        form = form,
        fieldName = fieldName,
        validDataGenerator = stringsWithMaxLength(PostalCode.length)
      )

      behave like fieldWithMaxLength(
        form = form,
        fieldName = fieldName,
        maxLength = PostalCode.length,
        lengthError = FormError(fieldName, lengthKey, Seq(name, PostalCode.length)),
        gen = invalidPostalOverLength
      )

      behave like mandatoryField(
        form = form,
        fieldName = fieldName,
        requiredError = FormError(fieldName, postalCodeRequiredKey, Seq(name))
      )

      behave like fieldWithInvalidCharacters(
        form = form,
        fieldName = fieldName,
        error = FormError(fieldName, postcodeInvalidKey, Seq(name)),
        length = PostalCode.length
      )
    }
  }

  "when postal code is not required" - {

    val form = new DynamicAddressFormProvider().apply(prefix, name, isPostalCodeRequired = false)

    ".numberAndStreet" - {

      val fieldName = NumberAndStreet.field

      behave like fieldThatBindsValidData(
        form = form,
        fieldName = fieldName,
        validDataGenerator = stringsWithMaxLength(NumberAndStreet.length)
      )

      behave like fieldWithMaxLength(
        form = form,
        fieldName = fieldName,
        maxLength = NumberAndStreet.length,
        lengthError = FormError(fieldName, lengthKey, Seq(NumberAndStreet.arg.capitalize, name, NumberAndStreet.length))
      )

      behave like mandatoryTrimmedField(
        form = form,
        fieldName = fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(NumberAndStreet.arg, name))
      )

      behave like fieldWithInvalidCharacters(
        form = form,
        fieldName = fieldName,
        error = FormError(fieldName, invalidKey, Seq(NumberAndStreet.arg.capitalize, name)),
        length = NumberAndStreet.length
      )
    }

    ".city" - {

      val fieldName = City.field

      behave like fieldThatBindsValidData(
        form = form,
        fieldName = fieldName,
        validDataGenerator = stringsWithMaxLength(City.length)
      )

      behave like fieldWithMaxLength(
        form = form,
        fieldName = fieldName,
        maxLength = City.length,
        lengthError = FormError(fieldName, lengthKey, Seq(City.arg.capitalize, name, City.length))
      )

      behave like mandatoryTrimmedField(
        form = form,
        fieldName = fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(City.arg, name))
      )

      behave like fieldWithInvalidCharacters(
        form = form,
        fieldName = fieldName,
        error = FormError(fieldName, invalidKey, Seq(City.arg.capitalize, name)),
        length = City.length
      )
    }

    ".postalCode" - {

      val postcodeInvalidKey = s"$prefix.error.postalCode.invalid"
      val lengthKey          = s"$prefix.error.postalCode.length"

      val fieldName = PostalCode.field

      val invalidPostalOverLength = stringsLongerThan(PostalCode.length + 1)

      behave like fieldThatBindsValidData(
        form = form,
        fieldName = fieldName,
        validDataGenerator = stringsWithMaxLength(PostalCode.length)
      )

      behave like fieldWithMaxLength(
        form = form,
        fieldName = fieldName,
        maxLength = PostalCode.length,
        lengthError = FormError(fieldName, lengthKey, Seq(name, PostalCode.length)),
        gen = invalidPostalOverLength
      )

      behave like optionalField(
        form = form,
        fieldName = fieldName
      )

      behave like fieldWithInvalidCharacters(
        form = form,
        fieldName = fieldName,
        error = FormError(fieldName, postcodeInvalidKey, Seq(name)),
        length = PostalCode.length
      )
    }
  }
}
