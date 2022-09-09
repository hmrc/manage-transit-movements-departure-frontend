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

class LocationOfGoodsPostalCodeFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val prefix = Gen.alphaNumStr.sample.value

  private val country   = Country(CountryCode("GB"), "United Kingdom")
  private val countries = CountryList(Seq(country))

  private val lengthStreetNumberKey   = s"$prefix.error.streetNumber.length"
  private val requiredStreetNumberKey = s"$prefix.error.streetNumber.required"
  private val invalidStreetNumberKey  = s"$prefix.error.streetNumber.invalidCharacters"

  private val lengthPostalCodeKey   = s"$prefix.error.postalCode.length"
  private val requiredPostalCodeKey = s"$prefix.error.postalCode.required"
  private val invalidPostalCodeKey  = s"$prefix.error.postalCode.invalidCharacters"

  private val form = new LocationOfGoodsPostalCodeFormProvider()(prefix, countries)

  ".streetNumber" - {

    val fieldName = StreetNumber.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(StreetNumber.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = StreetNumber.length,
      lengthError = FormError(fieldName, lengthStreetNumberKey, Seq(StreetNumber.arg.capitalize, StreetNumber.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredStreetNumberKey, Seq(StreetNumber.arg))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidStreetNumberKey, Seq(StreetNumber.arg.capitalize)),
      length = StreetNumber.length
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
