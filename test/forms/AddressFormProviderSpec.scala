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
import models.Address.Constants.Fields._
import models.Address.Constants._
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class AddressFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val addressHolderName = "addressHolder"
  private val prefix            = Gen.alphaNumStr.sample.value

  private val form = new AddressFormProvider()(prefix, addressHolderName)

  private val requiredKey              = s"$prefix.error.required"
  private val addressLengthKey         = s"$prefix.error.length"
  private val addressInvalidKey        = s"$prefix.error.invalid"
  private val postcodeLengthKey        = s"$prefix.error.postcode.length"
  private val postcodeInvalidKey       = s"$prefix.error.postcode.invalid"
  private val postcodeInvalidFormatKey = s"$prefix.error.postcode.invalidFormat"

  ".value" - {

    ".numberAndStreet" - {

      val fieldName = "numberAndStreet"

      val validAddressOverLength: Gen[String] = for {
        num  <- Gen.chooseNum[Int](numberAndStreetLength + 1, numberAndStreetLength + 5)
        list <- Gen.listOfN(num, Gen.alphaNumChar)
      } yield list.mkString("")

      val args = Seq(numberAndStreet, addressHolderName)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(numberAndStreetLength)
      )

      behave like fieldWithMaxLength(
        form,
        fieldName,
        maxLength = numberAndStreetLength,
        lengthError = FormError(fieldName, addressLengthKey, args),
        validAddressOverLength
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, requiredKey, args)
      )

      "must not bind strings that do not match regex" in {
        val fieldName = "numberAndStreet"
        val args      = Seq(numberAndStreet, addressHolderName)

        val generator: Gen[String] = RegexpGen.from(s"[!£^(){}_+=:;|`~,±<>éèâñüç]{$numberAndStreetLength}")
        val expectedError          = FormError(fieldName, addressInvalidKey, args)

        forAll(generator) {
          invalidString =>
            val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
            result.errors must contain(expectedError)
        }
      }
    }

    ".town" - {

      val fieldName = "town"

      val validAddressOverLength: Gen[String] = for {
        num  <- Gen.chooseNum[Int](townLength + 1, townLength + 5)
        list <- Gen.listOfN(num, Gen.alphaNumChar)
      } yield list.mkString("")

      val args = Seq(town, addressHolderName)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(townLength)
      )

      behave like fieldWithMaxLength(
        form,
        fieldName,
        maxLength = townLength,
        lengthError = FormError(fieldName, addressLengthKey, args),
        validAddressOverLength
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, requiredKey, args)
      )

      "must not bind strings that do not match regex" in {
        val fieldName = "town"
        val args      = Seq(town, addressHolderName)

        val generator: Gen[String] = RegexpGen.from(s"[!£^(){}_+=:;|`~,±<>]{$townLength}")
        val expectedError          = FormError(fieldName, addressInvalidKey, args)

        forAll(generator) {
          invalidString =>
            val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
            result.errors must contain(expectedError)
        }
      }
    }

    ".postcode" - {

      val fieldName = "postcode"

      val validAddressOverLength: Gen[String] = for {
        num  <- Gen.chooseNum[Int](postcodeLength + 1, postcodeLength + 5)
        list <- Gen.listOfN(num, Gen.alphaNumChar)
      } yield list.mkString("")

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(postcodeLength)
      )

      behave like fieldWithMaxLength(
        form,
        fieldName,
        maxLength = postcodeLength,
        lengthError = FormError(fieldName, postcodeLengthKey, Seq(addressHolderName)),
        validAddressOverLength
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(postcode, addressHolderName))
      )

      "must not bind strings that do not match regex" in {
        val fieldName = "postcode"

        val generator: Gen[String] = RegexpGen.from(s"[!£^(){}_+=:;|`~,±<>]{$postcodeLength}")
        val expectedError          = FormError(fieldName, postcodeInvalidKey, Seq(addressHolderName))

        forAll(generator) {
          invalidString =>
            val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
            result.errors must contain(expectedError)
        }
      }

      "must not bind strings that do not match postcode format" in {
        val fieldName = "postcode"

        val genInvalidString: Gen[String] = {
          stringsWithMaxLength(postcodeLength, Gen.alphaChar) suchThat (!_.matches(postCodeFormatRegex.toString()))
        }
        val expectedError = FormError(fieldName, postcodeInvalidFormatKey, Seq(addressHolderName))

        forAll(genInvalidString) {
          invalidString =>
            val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
            result.errors must contain(expectedError)
        }
      }
    }
  }
}
