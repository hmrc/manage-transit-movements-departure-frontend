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
import models.Address
import models.Address.Constants.Fields._
import models.Address.Constants._
import models.domain.StringFieldRegex.stringFieldRegex
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

import javax.inject.Inject

class AddressFormProvider @Inject() extends Mappings {

  def apply(prefix: String, name: String)(implicit messages: Messages): Form[Address] = Form(
    mapping(
      "numberAndStreet" -> {
        lazy val args = Seq(numberAndStreet, name)
        text(s"$prefix.error.required", args)
          .verifying(
            StopOnFirstFail[String](
              maxLength(buildingAndStreetLength, s"$prefix.error.length", args),
              regexp(stringFieldRegex, s"$prefix.error.invalid", args)
            )
          )
      },
      "town" -> {
        lazy val args = Seq(town, name)
        text(s"$prefix.error.required", args)
          .verifying(
            StopOnFirstFail[String](
              maxLength(townLength, s"$prefix.error.length", args),
              regexp(stringFieldRegex, s"$prefix.error.invalid", args)
            )
          )
      },
      "postcode" -> {
        lazy val args = Seq(name)
        text(s"$prefix.error.required", postcode +: args)
          .verifying(
            StopOnFirstFail[String](
              regexp(postCodeRegex, s"$prefix.error.postcode.invalid", args),
              regexp(postCodeFormatRegex, s"$prefix.error.postcode.invalidFormat", args)
            )
          )
      }
    )(Address.apply)(Address.unapply)
  )
}
