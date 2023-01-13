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

import forms.behaviours.DateBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

import java.time.LocalDate

class DateFormProviderSpec extends DateBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value

  private val (minDate, mustBeAfter)  = (LocalDate.of(2021: Int, 1: Int, 1: Int), "31 December 2020")
  private val (maxDate, mustBeBefore) = (LocalDate.of(2021: Int, 12: Int, 31: Int), "1 January 2022")
  private val form                    = new DateFormProvider()(prefix, minDate, maxDate)

  ".value" - {

    val fieldName = "value"

    val validData = datesBetween(
      min = minDate,
      max = maxDate
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, fieldName, s"$prefix.error.required.all")

    behave like dateFieldWithMin(form, fieldName, min = minDate, FormError("value", s"$prefix.error.min.date", Seq(mustBeAfter)))

    behave like dateFieldWithMax(form, fieldName, max = maxDate, FormError("value", s"$prefix.error.max.date", Seq(mustBeBefore)))

  }
}
