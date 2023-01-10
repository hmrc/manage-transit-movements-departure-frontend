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

package forms

import forms.behaviours.FieldBehaviours
import generators.Generators
import models.DateTime
import org.scalacheck.Gen
import play.api.data.{Field, Form, FormError}
import utils.Format.RichLocalDate

import java.time.LocalDate

class DateTimeFormProviderSpec extends FieldBehaviours with Generators {

  private val prefix = Gen.alphaNumStr.sample.value

  private val invalidDate       = s"$prefix.date.error.invalid"
  private val requiredAllDate   = s"$prefix.date.error.required.all"
  private val requiredMultiDate = s"$prefix.date.error.required.multiple"
  private val requiredOneDate   = s"$prefix.date.error.required"
  private val maxDate           = s"$prefix.date.error.futureDate"
  private val minDate           = s"$prefix.date.error.pastDate"

  private val invalidTime     = s"$prefix.time.error.invalid"
  private val requiredAllTime = s"$prefix.time.error.required.all"
  private val hourRequired    = s"$prefix.time.error.required.hour"
  private val minuteRequired  = s"$prefix.time.error.required.minute"

  private val localDate  = LocalDate.now()
  private val dateBefore = localDate.minusDays(1)
  private val dateAfter  = localDate.plusDays(1)

  private val form = new DateTimeFormProvider()(prefix, dateBefore, dateAfter)

  "dateTime" - {

    "must bind valid data" in {

      val localDateTime = arbitraryLocalDateTime.arbitrary

      forAll(localDateTime) {
        dateTime =>
          val data: Map[String, String] = Map(
            "timeHour"   -> dateTime.getHour.toString,
            "timeMinute" -> dateTime.getMinute.toString,
            "dateDay"    -> dateTime.getDayOfMonth.toString,
            "dateMonth"  -> dateTime.getMonthValue.toString,
            "dateYear"   -> dateTime.getYear.toString
          )

          val dateBefore = dateTime.toLocalDate.minusDays(1)
          val dateAfter  = dateTime.toLocalDate.plusDays(1)

          val form = new DateTimeFormProvider()(prefix, dateBefore, dateAfter)

          val result: Form[DateTime] = form.bind(data)

          val date = dateTime.toLocalDate
          val time = dateTime.toLocalTime

          result.errors mustBe List.empty
          result.value.value mustBe DateTime(date, time)
      }
    }
  }

  "time" - {

    val fieldName = "time"

    "must not bind when empty" in {
      val result: Field = form.bind(emptyForm).apply(fieldName)

      result.errors mustBe Seq(FormError(fieldName, List(requiredAllTime)))
    }

    "must not bind when hour is missing" in {

      val data: Map[String, String] = Map(
        "timeMinute" -> "20"
      )

      val result = form.bind(data).apply(fieldName)
      result.errors mustBe Seq(FormError(fieldName, List(hourRequired), List("hour")))
    }

    "must not bind when hour is invalid" in {

      val data: Map[String, String] = Map(
        "timeHour"   -> "65",
        "timeMinute" -> "20"
      )

      val result = form.bind(data).apply(fieldName)
      result.errors mustBe Seq(FormError(fieldName, List(invalidTime), List.empty))
    }

    "must not bind when minute is missing" in {

      val data: Map[String, String] = Map(
        "timeHour" -> "20"
      )

      val result = form.bind(data).apply(fieldName)
      result.errors mustBe Seq(FormError(fieldName, List(minuteRequired), List("minute")))
    }

    "must not bind when minute is invalid" in {

      val data: Map[String, String] = Map(
        "timeHour"   -> "20",
        "timeMinute" -> "65"
      )

      val result = form.bind(data).apply(fieldName)
      result.errors mustBe Seq(FormError(fieldName, List(invalidTime), List.empty))
    }
  }

  "date" - {

    val fieldName = "date"

    "must not bind when empty" in {
      val result: Field = form.bind(emptyForm).apply(fieldName)

      result.errors mustBe Seq(FormError(fieldName, List(requiredAllDate)))
    }

    "must not bind when one field is missing" in {

      val data: Map[String, String] = Map(
        "dateDay"   -> "1",
        "dateMonth" -> "1",
        "dateYear"  -> "2000"
      )

      val dataKeys = Seq("Day", "Month", "Year")

      dataKeys.foreach {
        key =>
          val missingData = data.-("date" + key)

          val result = form.bind(missingData).apply(fieldName)

          result.errors mustBe Seq(FormError(fieldName, List(requiredOneDate), List(key.toLowerCase)))
      }
    }

    "must not bind when multiple fields are missing" in {

      val data: Map[String, String] = Map(
        "dateDay"   -> "1",
        "dateMonth" -> "1",
        "dateYear"  -> "2000"
      )

      val dataKeys = Seq("Day", "Month", "Year")

      dataKeys.foreach {
        key =>
          val keys: Seq[String] = dataKeys.filterNot(_ == key)

          val missingData1 = data.-("date" + keys.head)
          val missingData2 = missingData1.-("date" + keys(1))

          val result = form.bind(missingData2).apply(fieldName)

          result.errors mustBe Seq(FormError(fieldName, List(requiredMultiDate), keys.map(_.toLowerCase)))
      }
    }

    "must not bind when day is invalid" in {

      forAll(intsAboveValue(31)) {
        invalidDay =>
          val data: Map[String, String] = Map(
            "dateDay"   -> invalidDay.toString,
            "dateMonth" -> "1",
            "dateYear"  -> "2000"
          )

          val result = form.bind(data).apply(fieldName)

          result.errors mustBe Seq(FormError(fieldName, List(invalidDate), List.empty))
      }
    }

    "must not bind when month is invalid" in {

      forAll(intsAboveValue(12)) {
        invalidMonth =>
          val data: Map[String, String] = Map(
            "dateDay"   -> "1",
            "dateMonth" -> invalidMonth.toString,
            "dateYear"  -> "2000"
          )

          val result = form.bind(data).apply(fieldName)

          result.errors mustBe Seq(FormError(fieldName, List(invalidDate), List.empty))
      }
    }

    "must not bind when year is invalid" in {

      forAll(nonNumerics) {
        invalidYear =>
          val data: Map[String, String] = Map(
            "dateDay"   -> "1",
            "dateMonth" -> "1",
            "dateYear"  -> invalidYear
          )

          val result = form.bind(data).apply(fieldName)

          result.errors mustBe Seq(FormError(fieldName, List(invalidDate), List.empty))
      }
    }

    "must not bind when date is above max date" in {

      val localDateTime = arbitraryLocalDateTime.arbitrary

      forAll(localDateTime) {
        dateTime =>
          val invalidDateTime = dateTime.plusDays(2)

          val data: Map[String, String] = Map(
            "timeHour"   -> invalidDateTime.getHour.toString,
            "timeMinute" -> invalidDateTime.getMinute.toString,
            "dateDay"    -> invalidDateTime.getDayOfMonth.toString,
            "dateMonth"  -> invalidDateTime.getMonthValue.toString,
            "dateYear"   -> invalidDateTime.getYear.toString
          )

          val dateBefore = dateTime.toLocalDate.minusDays(1)
          val dateAfter  = dateTime.toLocalDate.plusDays(1)

          val form = new DateTimeFormProvider()(prefix, dateBefore, dateAfter)

          val result: Form[DateTime] = form.bind(data)

          val formattedArg = invalidDateTime.toLocalDate.formatAsString

          result.errors mustBe Seq(FormError(fieldName, List(maxDate), List(formattedArg)))
      }
    }

    "must not bind when date is below min date" in {

      val localDateTime = arbitraryLocalDateTime.arbitrary

      forAll(localDateTime) {
        dateTime =>
          val invalidDateTime = dateTime.minusDays(2)

          val data: Map[String, String] = Map(
            "timeHour"   -> invalidDateTime.getHour.toString,
            "timeMinute" -> invalidDateTime.getMinute.toString,
            "dateDay"    -> invalidDateTime.getDayOfMonth.toString,
            "dateMonth"  -> invalidDateTime.getMonthValue.toString,
            "dateYear"   -> invalidDateTime.getYear.toString
          )

          val dateBefore = dateTime.toLocalDate.minusDays(1)
          val dateAfter  = dateTime.toLocalDate.plusDays(1)

          val form = new DateTimeFormProvider()(prefix, dateBefore, dateAfter)

          val result: Form[DateTime] = form.bind(data)

          val formattedArg = invalidDateTime.toLocalDate.formatAsString

          result.errors mustBe Seq(FormError(fieldName, List(minDate), List(formattedArg)))
      }
    }
  }
}
