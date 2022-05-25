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
import models.Address._
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

import javax.inject.Inject

abstract class AddressFormProvider @Inject() extends Mappings {

  val addressLine1: AddressLine1
  val addressLine2: AddressLine2

  def apply(prefix: String, name: String)(implicit messages: Messages): Form[Address] = Form(
    mapping(
      addressLine1.field -> {
        lazy val args = Seq(addressLine1.arg, name)
        text(s"$prefix.error.required", args)
          .verifying(
            StopOnFirstFail[String](
              maxLength(addressLine1.length, s"$prefix.error.length", args),
              regexp(addressLine1.regex, s"$prefix.error.invalid", args)
            )
          )
      },
      addressLine2.field -> {
        lazy val args = Seq(addressLine2.arg, name)
        text(s"$prefix.error.required", args)
          .verifying(
            StopOnFirstFail[String](
              maxLength(addressLine2.length, s"$prefix.error.length", args),
              regexp(addressLine2.regex, s"$prefix.error.invalid", args)
            )
          )
      },
      Postcode.field -> {
        lazy val args = Seq(name)
        text(s"$prefix.error.required", Postcode.arg +: args)
          .verifying(
            StopOnFirstFail[String](
              regexp(Postcode.regex, s"$prefix.error.postcode.invalid", args),
              regexp(Postcode.formatRegex, s"$prefix.error.postcode.invalidFormat", args)
            )
          )
      }
    )(Address.apply)(Address.unapply)
  )
}
