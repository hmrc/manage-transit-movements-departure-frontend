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

import forms.Constants.maxAuthorisationRefNumberLength
import forms.mappings.Mappings
import models.domain.StringFieldRegex.alphaNumericRegex

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.Messages

class AuthorisationReferenceNumberFormProvider @Inject() extends Mappings {

  def apply(prefix: String, dynamicTitle: String)(implicit messages: Messages): Form[String] = {

    val arg = messages(dynamicTitle)

    Form(
      "value" -> text(s"$prefix.error.required", Seq(arg))
        .verifying(
          StopOnFirstFail[String](
            maxLength(maxAuthorisationRefNumberLength, s"$prefix.error.length"),
            regexp(alphaNumericRegex, s"$prefix.error.invalidCharacters")
          )
        )
    )
  }
}
