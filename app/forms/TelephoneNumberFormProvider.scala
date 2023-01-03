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

import forms.Constants.{maxTelephoneNumberLength, minTelephoneNumberLength}
import forms.mappings.Mappings
import models.domain.StringFieldRegex.{telephoneNumberCharacterRegex, telephoneNumberFormatRegex}
import play.api.data.Form

import javax.inject.Inject

class TelephoneNumberFormProvider @Inject() extends Mappings {

  def apply(prefix: String, args: String*): Form[String] =
    Form(
      "value" -> text(s"$prefix.error.required", args)
        .verifying(
          StopOnFirstFail[String](
            regexp(telephoneNumberCharacterRegex, s"$prefix.error.invalidCharacters", args = args),
            regexp(telephoneNumberFormatRegex, s"$prefix.error.invalidFormat", args = args),
            minLength(minTelephoneNumberLength, s"$prefix.error.minLength", args = args :+ minTelephoneNumberLength, trim = true),
            maxLength(maxTelephoneNumberLength, s"$prefix.error.maxLength", args = args :+ maxTelephoneNumberLength, trim = true)
          )
        )
    )
}
