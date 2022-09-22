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

package forms.locationOfGoods

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.Coordinates
import models.domain.StringFieldRegex.coordinateFormatRegex
import org.scalacheck.Gen
import play.api.data.{Field, Form, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class CoordinatesFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val prefix = Gen.alphaNumStr.sample.value

  private val requiredKey     = s"$prefix.error.required"
  private val invalidKey      = s"$prefix.error.invalid"
  private val maxLongitudeKey = s"$prefix.error.longitude.maximum"
  private val maxLatitudeKey  = s"$prefix.error.latitude.maximum"

  private val form = new CoordinatesFormProvider()(prefix)

  "coordinates" - {

    val arbCoordinates = arbitraryCoordinates.arbitrary

    "must bind valid data" in {

      forAll(arbCoordinates) {
        coordinates =>
          val latitude  = coordinates.latitude
          val longitude = coordinates.longitude

          val data = Map(
            "latitude"  -> latitude,
            "longitude" -> longitude
          )

          val result: Form[Coordinates] = form.bind(data)

          result.errors mustBe List.empty
          result.value.value mustBe Coordinates(latitude, longitude)
      }

    }

    ".latitude" - {

      val fieldName = "latitude"

      behave like fieldThatBindsValidData(
        form = form,
        fieldName = fieldName,
        validDataGenerator = RegexpGen.from(coordinateFormatRegex.toString)
      )

      behave like mandatoryTrimmedField(
        form = form,
        fieldName = fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
      )

      behave like fieldWithInvalidCharacters(
        form = form,
        fieldName = fieldName,
        error = FormError(fieldName, invalidKey, Seq(fieldName))
      )

      "must throw error when latitude above maximum" in {

        val invalidString = "90.11111"
        val error         = FormError(fieldName, maxLatitudeKey, Seq(fieldName))

        val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
        result.errors must contain(error)
      }

      "must throw error when latitude below minimum" in {

        val invalidString = "-90.11111"
        val error         = FormError(fieldName, maxLatitudeKey, Seq(fieldName))

        val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
        result.errors must contain(error)
      }

    }

    ".longitude" - {

      val fieldName = "longitude"

      behave like fieldThatBindsValidData(
        form = form,
        fieldName = fieldName,
        validDataGenerator = RegexpGen.from(coordinateFormatRegex.toString)
      )

      behave like mandatoryTrimmedField(
        form = form,
        fieldName = fieldName,
        requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
      )

      behave like fieldWithInvalidCharacters(
        form = form,
        fieldName = fieldName,
        error = FormError(fieldName, invalidKey, Seq(fieldName))
      )

      "must throw error when longitude above maximum" in {

        val invalidString = "180.11111"
        val error         = FormError(fieldName, maxLongitudeKey, Seq(fieldName))

        val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
        result.errors must contain(error)
      }

      "must throw error when longitude below minimum" in {

        val invalidString = "-180.11111"
        val error         = FormError(fieldName, maxLongitudeKey, Seq(fieldName))

        val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
        result.errors must contain(error)
      }

    }
  }
}
