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

class LocationOfGoodsAddressFormProvider @Inject() extends Mappings {

  def apply(prefix: String, countryList: CountryList)(implicit messages: Messages): Form[Address] =
    Form(
      mapping(
        AddressLine1.field -> {
          lazy val args = Seq(AddressLine1.arg)
          trimmedText(s"$prefix.error.addressLine1.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(AddressLine1.length, s"$prefix.error.addressLine1.length", Seq(AddressLine1.arg.capitalize, AddressLine1.length)),
                regexp(AddressLine1.regex, s"$prefix.error.addressLine1.invalidCharacters", Seq(AddressLine1.arg.capitalize))
              )
            )
        },
        AddressLine2.field -> {
          lazy val args = Seq(AddressLine2.arg)
          trimmedText(s"$prefix.error.addressLine2.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(AddressLine2.length, s"$prefix.error.addressLine2.length", Seq(AddressLine2.arg.capitalize, AddressLine2.length)),
                regexp(AddressLine2.regex, s"$prefix.error.addressLine2.invalidCharacters", Seq(AddressLine2.arg.capitalize))
              )
            )
        },
        PostalCode.field -> {
          lazy val args = Seq()
          trimmedText(s"$prefix.error.postcode.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(PostalCode.length, s"$prefix.error.postcode.length", args :+ PostalCode.length),
                regexp(PostalCode.regex, s"$prefix.error.postcode.invalidCharacters", args)
              )
            )
        },
        Country.field -> {
          country(countryList, s"$prefix.error.country.required", Seq())
        }
      )(Address.apply)(Address.unapply)
    )
}
