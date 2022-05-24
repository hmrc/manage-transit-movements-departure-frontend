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
import javax.inject.Inject
import models.TelephoneNumber
import play.api.data.Form
import play.api.data.Forms.mapping

class TelephoneNumberFormProvider @Inject() extends Mappings {

  def apply(prefix: String, name: String): Form[TelephoneNumber] =
    Form(
      mapping(
        "value" -> text(s"$prefix.error.required")
          .verifying(
            StopOnFirstFail[String](
              maxLength(TelephoneNumber.Constants.maxTelephoneNumberLength,
                        s"$prefix.error.length",
                        args = Seq(name, TelephoneNumber.Constants.maxTelephoneNumberLength)
              ),
              regexp(TelephoneNumber.Constants.telephoneNumberRegex, s"$prefix.error.invalidFormat", args = Seq(name))
            )
          )
      )(TelephoneNumber.apply)(TelephoneNumber.unapply)
    )
}
