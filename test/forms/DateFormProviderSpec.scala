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

import forms.behaviours.DateBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

import java.time.{Clock, LocalDate, ZoneOffset}

class DateFormProviderSpec extends DateBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value

  private val (minDate, minDateAsString) = (LocalDate.of(2020: Int, 12: Int, 31: Int), "31 December 2020")
  private val maxDate                    = LocalDate.now(ZoneOffset.UTC)
  private val zone                       = ZoneOffset.UTC
  private val clock                      = Clock.systemDefaultZone.withZone(zone)
  private val form                       = new DateFormProvider(clock)(prefix, minDate)

  ".value" - {

    val fieldName = "value"

    val validData = datesBetween(
      min = minDate,
      max = LocalDate.now(zone)
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, fieldName, s"$prefix.error.required.all")

    behave like dateFieldWithMin(form, fieldName, min = minDate, FormError("value", s"$prefix.error.min.date", Seq(minDateAsString)))

    behave like dateFieldWithMax(form, fieldName, max = maxDate, FormError("value", s"$prefix.error.max.date"))

  }
}
