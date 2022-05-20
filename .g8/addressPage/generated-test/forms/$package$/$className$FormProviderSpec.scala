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
import models.Address
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class $className$FormProviderSpec extends StringFieldBehaviours with SpecBase {

  val addressHolderName = "addressHolder"
  val form = new $className$FormProvider()(addressHolderName)

  lazy val addressRequiredKey          = "$className;format="decap"$.error.required"
  lazy val addressLengthKey            = "$className;format="decap"$.error.length"
  lazy val addressInvalidKey           = "$className;format="decap"$.error.invalid"
  lazy val postcodeRequiredKey         = "$className;format="decap"$.error.postcode.required"
  lazy val postcodeLengthKey           = "$className;format="decap"$.error.postcode.length"
  lazy val postcodeInvalidKey          = "$className;format="decap"$.error.postcode.invalid"
  lazy val postcodeInvalidFormatKey    = "$className;format="decap"$.error.postcode.invalidFormat"

  ".value" - {

    ".buildingAndStreet" - {

      val fieldName = "buildingAndStreet"

      val validAdressOverLength: Gen[String] = for {
        num  <- Gen.chooseNum[Int](Address.Constants.buildingAndStreetLength + 1, Address.Constants.buildingAndStreetLength + 5)
        list <- Gen.listOfN(num, Gen.alphaNumChar)
      } yield list.mkString("")

      val args = Seq(Address.Constants.Fields.buildingAndStreetName, addressHolderName)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(Address.Constants.buildingAndStreetLength)
      )

      behave like fieldWithMaxLength(
        form,
        fieldName,
        maxLength = Address.Constants.buildingAndStreetLength,
        lengthError = FormError(fieldName, addressLengthKey, args),
        validAdressOverLength
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, addressRequiredKey, args)
      )

      "must not bind strings that do not match regex" in {
        val fieldName = "buildingAndStreet"
        val args      = Seq(Address.Constants.Fields.buildingAndStreetName, addressHolderName)

        val generator: Gen[String] = RegexpGen.from(s"[!£^(){}_+=:;|`~,±<>éèâñüç]{${Address.Constants.buildingAndStreetLength}}")
        val expectedError          = FormError(fieldName, addressInvalidKey, args)

        forAll(generator) {
          invalidString =>
            val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
            result.errors must contain(expectedError)
        }
      }
    }

    ".city" - {

      val fieldName = "city"

      val validAddressOverLength: Gen[String] = for {
        num  <- Gen.chooseNum[Int](Address.Constants.cityLength + 1, Address.Constants.cityLength + 5)
        list <- Gen.listOfN(num, Gen.alphaNumChar)
      } yield list.mkString("")

      val args = Seq(Address.Constants.Fields.city, addressHolderName)

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(Address.Constants.cityLength)
      )

      behave like fieldWithMaxLength(
        form,
        fieldName,
        maxLength = Address.Constants.cityLength,
        lengthError = FormError(fieldName, addressLengthKey, args),
        validAddressOverLength
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, addressRequiredKey, args)
      )

      "must not bind strings that do not match regex" in {
        val fieldName = "city"
        val args      = Seq(Address.Constants.Fields.city, addressHolderName)

        val generator: Gen[String] = RegexpGen.from(s"[!£^(){}_+=:;|`~,±<>]{${Address.Constants.cityLength}}")
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

      val validAdressOverLength: Gen[String] = for {
        num  <- Gen.chooseNum[Int](Address.Constants.postcodeLength + 1, Address.Constants.postcodeLength + 5)
        list <- Gen.listOfN(num, Gen.alphaNumChar)
      } yield list.mkString("")

      behave like fieldThatBindsValidData(
        form,
        fieldName,
        stringsWithMaxLength(Address.Constants.postcodeLength)
      )

      behave like fieldWithMaxLength(
        form,
        fieldName,
        maxLength = Address.Constants.postcodeLength,
        lengthError = FormError(fieldName, postcodeLengthKey, Seq(addressHolderName)),
        validAdressOverLength
      )

      behave like mandatoryField(
        form,
        fieldName,
        requiredError = FormError(fieldName, postcodeRequiredKey, Seq(addressHolderName))
      )

      "must not bind strings that do not match regex" in {
        val fieldName = "postcode"

        val generator: Gen[String] = RegexpGen.from(s"[!£^(){}_+=:;|`~,±<>]{${Address.Constants.postcodeLength}}")
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
          stringsWithMaxLength(Address.Constants.postcodeLength) suchThat (!_.matches(Address.Constants.postCodeFormatRegex.toString()))
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
