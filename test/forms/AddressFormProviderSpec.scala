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
import models.Address._
import org.scalacheck.Gen
import play.api.data.{Field, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

abstract class AddressFormProviderSpec extends StringFieldBehaviours with SpecBase {

  val formProvider: AddressFormProvider

  lazy val addressLine1: AddressLine1 = formProvider.addressLine1
  lazy val addressLine2: AddressLine2 = formProvider.addressLine2

  private val prefix            = Gen.alphaNumStr.sample.value
  private val addressHolderName = Gen.alphaNumStr.sample.value

  private lazy val form = formProvider(prefix, addressHolderName)

  private val requiredKey              = s"$prefix.error.required"
  private val addressLengthKey         = s"$prefix.error.length"
  private val addressInvalidKey        = s"$prefix.error.invalid"
  private val postcodeInvalidKey       = s"$prefix.error.postcode.invalid"
  private val postcodeInvalidFormatKey = s"$prefix.error.postcode.invalidFormat"

  // scalastyle:off method.length
  def addressFormProvider(): Unit = {

    ".value" - {

      s".${addressLine1.field}" - {

        val fieldName = addressLine1.field

        val validAddressOverLength: Gen[String] = for {
          num  <- Gen.chooseNum[Int](addressLine1.length + 1, addressLine1.length + 5)
          list <- Gen.listOfN(num, Gen.alphaNumChar)
        } yield list.mkString("")

        val args = Seq(addressLine1.arg, addressHolderName)

        behave like fieldThatBindsValidData(
          form,
          fieldName,
          stringsWithMaxLength(addressLine1.length)
        )

        behave like fieldWithMaxLength(
          form,
          fieldName,
          maxLength = addressLine1.length,
          lengthError = FormError(fieldName, addressLengthKey, args),
          validAddressOverLength
        )

        behave like mandatoryField(
          form,
          fieldName,
          requiredError = FormError(fieldName, requiredKey, args)
        )

        "must not bind strings that do not match regex" in {
          val generator: Gen[String] = RegexpGen.from(s"[!£^(){}_+=:;|`~,±<>éèâñüç]{${addressLine1.length}}")
          val expectedError          = FormError(fieldName, addressInvalidKey, args)

          forAll(generator) {
            invalidString =>
              val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
              result.errors must contain(expectedError)
          }
        }
      }

      s".${addressLine2.field}" - {

        val fieldName = addressLine2.field

        val validAddressOverLength: Gen[String] = for {
          num  <- Gen.chooseNum[Int](addressLine2.length + 1, addressLine2.length + 5)
          list <- Gen.listOfN(num, Gen.alphaNumChar)
        } yield list.mkString("")

        val args = Seq(addressLine2.arg, addressHolderName)

        behave like fieldThatBindsValidData(
          form,
          fieldName,
          stringsWithMaxLength(addressLine2.length)
        )

        behave like fieldWithMaxLength(
          form,
          fieldName,
          maxLength = addressLine2.length,
          lengthError = FormError(fieldName, addressLengthKey, args),
          validAddressOverLength
        )

        behave like mandatoryField(
          form,
          fieldName,
          requiredError = FormError(fieldName, requiredKey, args)
        )

        "must not bind strings that do not match regex" in {
          val generator: Gen[String] = RegexpGen.from(s"[!£^(){}_+=:;|`~,±<>]{${addressLine2.length}}")
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

        behave like fieldThatBindsValidData(
          form,
          fieldName,
          stringsThatMatchRegex(Postcode.formatRegex)
        )

        behave like mandatoryField(
          form,
          fieldName,
          requiredError = FormError(fieldName, requiredKey, Seq(Postcode.arg, addressHolderName))
        )

        behave like fieldWithInvalidCharacters(
          form,
          fieldName,
          FormError(fieldName, postcodeInvalidKey, Seq(addressHolderName))
        )

        "must not bind strings that do not match postcode format" in {
          val genInvalidString: Gen[String] = {
            stringsThatMatchRegex(Postcode.regex) suchThat (!_.matches(Postcode.formatRegex.regex))
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
  // scalastyle:on method.length
}
