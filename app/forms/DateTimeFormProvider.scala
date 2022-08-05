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

import forms.mappings.Mappings
import models.DateTime
import play.api.data.Form
import play.api.data.Forms.mapping
import utils.Format

import java.time.LocalDate
import javax.inject.Inject

class DateTimeFormProvider @Inject() extends Mappings {

  def apply(prefix: String, dateMin: LocalDate, dateMax: LocalDate, args: Any*): Form[DateTime] =
    Form(
      mapping(
        "date" -> {
          localDate(
            invalidKey = s"$prefix.date.error.invalid",
            allRequiredKey = s"$prefix.date.error.required.all",
            twoRequiredKey = s"$prefix.date.error.required.multiple",
            requiredKey = s"$prefix.date.error.required"
          ).verifying(
            maxDate(dateMax, s"$prefix.date.error.futureDate", Format.dateFormatterDDMMYYYY.format(dateMax.plusDays(1))),
            minDate(dateMin, s"$prefix.date.error.pastDate", Format.dateFormatterDDMMYYYY.format(dateMin.minusDays(1)))
          )
        },
        "time" -> {
          localTime(
            invalidKey = s"$prefix.time.error.invalid",
            allRequiredKey = s"$prefix.time.error.required.all",
            requiredKey = s"$prefix.time.error.required"
          )
        }
      )(DateTime.apply)(DateTime.unapply)
    )
}
