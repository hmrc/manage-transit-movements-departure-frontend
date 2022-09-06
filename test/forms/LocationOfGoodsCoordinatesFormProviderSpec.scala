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
import models.{AddressLine, Coordinates, CountryList}
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.{Field, Form, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class LocationOfGoodsCoordinatesFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val prefix = Gen.alphaNumStr.sample.value

  private val requiredKey = s"$prefix.error.required"
  private val lengthKey   = s"$prefix.error.length"
  private val invalidKey  = s"$prefix.error.invalid"

  private val form = new LocationOfGoodsCoordinatesFormProvider()(prefix)

  "coordinates" - {

    val arbCoordinates = arbitraryCoordinates.arbitrary

//    "must bind valid data" in {
//
//      forAll(arbCoordinates) {
//        coordinates =>
//          val latitude  = coordinates.latitude
//          val longitude = coordinates.longitude
//
//          val data = Map(
//            "latitude"  -> latitude,
//            "longitude" -> longitude
//          )
//
//          val result: Form[Coordinates] = form.bind(data)
//
//          result.errors mustBe List.empty
//          result.value.value mustBe Coordinates(latitude, longitude)
//      }
//
//    }

    ".latitude" - {

      val fieldName = "latitude"

      behave like fieldThatBindsValidData(
        form = form,
        fieldName = fieldName,
        validDataGenerator = RegexpGen.from("^[+-]?([0-8]?[0-9]\\.[0-9]{5,7})$")
      )

      behave like mandatoryTrimmedField(
        form = form,
        fieldName = fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(s"$prefix.$fieldName"))
      )

      behave like fieldWithInvalidCharacters(
        form = form,
        fieldName = fieldName,
        error = FormError(fieldName, invalidKey, Seq(s"$prefix.$fieldName"))
      )
    }

    ".longitude" - {

      val fieldName = "longitude"

      behave like fieldThatBindsValidData(
        form = form,
        fieldName = fieldName,
        validDataGenerator = RegexpGen.from("^[+-]?((0?[0-9]?|1[0-7])[0-9]\\.[0-9]{5,7})$")
      )

      behave like mandatoryTrimmedField(
        form = form,
        fieldName = fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(s"$prefix.$fieldName"))
      )

      behave like fieldWithInvalidCharacters(
        form = form,
        fieldName = fieldName,
        error = FormError(fieldName, invalidKey, Seq(s"$prefix.$fieldName"))
      )
    }
  }
}
