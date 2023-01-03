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

import forms.mappings.Mappings
import play.api.data.Form

import java.time.format.DateTimeFormatter
import java.time.{Clock, LocalDate}
import javax.inject.Inject

class DateFormProvider @Inject() (clock: Clock) extends Mappings {

  def apply(prefix: String, minimumDate: LocalDate): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = s"$prefix.error.invalid",
        allRequiredKey = s"$prefix.error.required.all",
        twoRequiredKey = s"$prefix.error.required.two",
        requiredKey = s"$prefix.error.required"
      ).verifying(
        minDate(minimumDate, s"$prefix.error.min.date", minimumDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))),
        maxDate(LocalDate.now(clock), s"$prefix.error.max.date")
      )
    )
}
