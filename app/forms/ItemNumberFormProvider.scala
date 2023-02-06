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

import forms.Constants.{itemNumberLength, itemNumberMax}
import forms.mappings.Mappings
import models.domain.StringFieldRegex.numericRegex
import play.api.data.Form

import javax.inject.Inject

class ItemNumberFormProvider @Inject() extends Mappings {

  def apply(prefix: String): Form[String] =
    Form(
      "value" -> text(s"$prefix.error.required")
        .verifying(
          StopOnFirstFail[String](
            maxLength(itemNumberLength, s"$prefix.error.length"),
            regexp(numericRegex, s"$prefix.error.invalidCharacters"),
            minimumIntValue(0, s"$prefix.error.range"),
            maximumIntValue(itemNumberMax, s"$prefix.error.range")
          )
        )
    )
}
