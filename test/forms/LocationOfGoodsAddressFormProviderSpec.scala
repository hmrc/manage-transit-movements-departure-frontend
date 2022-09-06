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
import models.reference.{Country, CountryCode}
import models.{AddressLine, CountryList}
import org.scalacheck.Gen
import play.api.data.FormError

class LocationOfGoodsAddressFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val prefix = Gen.alphaNumStr.sample.value

  private val country   = Country(CountryCode("GB"), "United Kingdom")
  private val countries = CountryList(Seq(country))

  private val requiredKey = s"$prefix.error.required"
  private val lengthKey   = s"$prefix.error.length"
  private val invalidKey  = s"$prefix.error.invalid"

  private val lengthAddressLine1Key   = s"$prefix.error.addressLine1.length"
  private val requiredAddressLine1Key = s"$prefix.error.addressLine1.required"
  private val invalidAddressLine1Key  = s"$prefix.error.addressLine1.invalidCharacters"

  private val lengthAddressLine2Key   = s"$prefix.error.addressLine2.length"
  private val requiredAddressLine2Key = s"$prefix.error.addressLine2.required"
  private val invalidAddressLine2Key  = s"$prefix.error.addressLine2.invalidCharacters"

  private val lengthPostalCodeKey   = s"$prefix.error.postcode.length"
  private val requiredPostalCodeKey = s"$prefix.error.postcode.required"
  private val invalidPostalCodeKey  = s"$prefix.error.postcode.invalidCharacters"

  private val form = new LocationOfGoodsAddressFormProvider()(prefix, countries)

  ".addressLine1" - {

    val fieldName = AddressLine1.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(AddressLine1.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = AddressLine1.length,
      lengthError = FormError(fieldName, lengthAddressLine1Key, Seq(AddressLine1.arg.capitalize, AddressLine1.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredAddressLine1Key, Seq(AddressLine1.arg))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidAddressLine1Key, Seq(AddressLine1.arg.capitalize)),
      length = AddressLine1.length
    )
  }

  ".addressLine2" - {

    val fieldName = AddressLine2.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(AddressLine2.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = AddressLine2.length,
      lengthError = FormError(fieldName, lengthAddressLine2Key, Seq(AddressLine2.arg.capitalize, AddressLine2.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredAddressLine2Key, Seq(AddressLine2.arg))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidAddressLine2Key, Seq(AddressLine2.arg.capitalize)),
      length = AddressLine2.length
    )
  }

  ".postalCode" - {

    val fieldName = PostalCode.field

    val validPostalOverLength: Gen[String] = for {
      num  <- Gen.chooseNum[Int](PostalCode.length + 1, PostalCode.length + 5)
      list <- Gen.listOfN(num, Gen.alphaNumChar)
    } yield list.mkString("")

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(PostalCode.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = PostalCode.length,
      lengthError = FormError(fieldName, lengthPostalCodeKey, Seq(PostalCode.length)),
      gen = validPostalOverLength
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredPostalCodeKey, Seq())
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidPostalCodeKey, Seq()),
      length = PostalCode.length
    )
  }

  ".country" - {

    import AddressLine.Country

    val fieldName = Country.field

    val countryRequiredKey = s"$prefix.error.country.required"

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = nonEmptyString
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, countryRequiredKey, Seq())
    )

    "not bind if country code does not exist in the country list" in {
      val result        = form.bind(Map(fieldName -> "foobar")).apply(fieldName)
      val expectedError = FormError(fieldName, countryRequiredKey, Seq())
      result.errors must contain(expectedError)
    }

    "bind a country code which is in the list" in {
      val result = form.bind(Map(fieldName -> country.code.code)).apply(fieldName)
      result.value.value mustBe country.code.code
    }
  }
}
