/*
 * Copyright 2023 HM Revenue & Customs
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

package forms.mappings

import generators.Generators
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.{Form, FormError}

import java.time.LocalTime

class TimeMappingsSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks with Generators with OptionValues with Mappings {

  val form: Form[LocalTime] = Form(
    "value" -> localTime(
      requiredKey = "error.required",
      allRequiredKey = "error.required.all",
      invalidKey = "error.invalid"
    )
  )

  val invalidField: Gen[String] = Gen.alphaStr.suchThat(_.nonEmpty)

  val genTime: Gen[LocalTime] = arbitraryLocalTime.arbitrary

  "must bind valid data" in {

    forAll(genTime -> "valid time") {
      time =>
        val data = Map(
          "valueMinute" -> time.getMinute.toString,
          "valueHour"   -> time.getHour.toString
        )

        val result = form.bind(data)

        result.value.value mustEqual time
    }
  }

  "must fail to bind an empty time" in {

    val result = form.bind(Map.empty[String, String])

    result.errors must contain only FormError("value", "error.required.all")
  }

  "must fail to bind a time with a missing minute" in {

    forAll(genTime -> "valid time") {
      time =>
        val data = Map(
          "valueMinute" -> "",
          "valueHour"   -> time.getHour.toString
        )

        val result = form.bind(data)

        result.errors must contain only FormError("value", "error.required.minute", List("minute"))
    }
  }

  "must fail to bind a time with an invalid minute" in {

    forAll(genTime -> "valid time", invalidField -> "invalid field") {
      (time, field) =>
        val data = Map(
          "valueMinute" -> field,
          "valueHour"   -> time.getHour.toString
        )

        val result = form.bind(data)

        result.errors must contain(
          FormError("value", "error.invalid", List.empty)
        )
    }
  }

  "must fail to bind a time with a missing hour" in {

    forAll(genTime -> "valid time") {
      time =>
        val data = Map(
          "valueMinute" -> time.getMinute.toString,
          "valueHour"   -> ""
        )

        val result = form.bind(data)

        result.errors must contain only FormError("value", "error.required.hour", List("hour"))
    }
  }

  "must fail to bind a time with an invalid hour" in {

    forAll(genTime -> "valid data", invalidField -> "invalid field") {
      (time, field) =>
        val data = Map(
          "valueMinute" -> time.getMinute.toString,
          "valueHour"   -> field
        )

        val result = form.bind(data)

        result.errors must contain(
          FormError("value", "error.invalid", List.empty)
        )
    }
  }

  "must fail to bind an invalid minute and hour" in {

    forAll(invalidField -> "invalid minute", invalidField -> "invalid hour") {
      (minute, hour) =>
        val data = Map(
          "valueMinute" -> minute,
          "valueHour"   -> hour
        )

        val result = form.bind(data)

        result.errors must contain only FormError("value", "error.invalid", List.empty)
    }
  }

  "must unbind a time" in {

    forAll(genTime -> "valid time") {
      time =>
        val filledForm = form.fill(time)

        filledForm("valueMinute").value.value mustEqual time.getMinute.toString
        filledForm("valueHour").value.value mustEqual time.getHour.toString
    }
  }
}
