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
import models.AddressLine._
import models.{Address, CountryList}
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

import javax.inject.Inject

class AddressFormProvider @Inject() extends Mappings {

  def apply(prefix: String, name: String, countryList: CountryList)(implicit messages: Messages): Form[Address] =
    Form(
      mapping(
        AddressLine1.field -> {
          val args = Seq(AddressLine1.arg, name)
          trimmedText(s"$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(AddressLine1.length, s"$prefix.error.length", Seq(AddressLine1.arg.capitalize, name, AddressLine1.length)),
                regexp(AddressLine1.regex, s"$prefix.error.invalid", Seq(AddressLine1.arg.capitalize, name))
              )
            )
        },
        AddressLine2.field -> {
          val args = Seq(AddressLine2.arg, name)
          trimmedText(s"$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(AddressLine2.length, s"$prefix.error.length", Seq(AddressLine2.arg.capitalize, name, AddressLine2.length)),
                regexp(AddressLine2.regex, s"$prefix.error.invalid", Seq(AddressLine2.arg.capitalize, name))
              )
            )
        },
        PostalCode.field -> {
          val args = Seq(name)
          trimmedText(s"$prefix.error.postalCode.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(PostalCode.length, s"$prefix.error.postalCode.length", args :+ PostalCode.length),
                regexp(PostalCode.regex, s"$prefix.error.postalCode.invalid", args)
              )
            )
        },
        Country.field -> {
          country(countryList, s"$prefix.error.country.required", Seq(name))
        }
      )(Address.apply)(Address.unapply)
    )
}
