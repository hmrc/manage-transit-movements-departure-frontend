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
import models.DynamicAddress
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}
import play.api.i18n.Messages

import javax.inject.Inject

class DynamicAddressFormProvider @Inject() extends Mappings {

  def apply(prefix: String, name: String, isPostalCodeRequired: Boolean)(implicit messages: Messages): Form[DynamicAddress] =
    Form(
      mapping(
        NumberAndStreet.field -> {
          val args = Seq(NumberAndStreet.arg, name)
          trimmedText(s"$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(NumberAndStreet.length, s"$prefix.error.length", Seq(NumberAndStreet.arg.capitalize, name, NumberAndStreet.length)),
                regexp(NumberAndStreet.regex, s"$prefix.error.invalid", Seq(NumberAndStreet.arg.capitalize, name))
              )
            )
        },
        City.field -> {
          val args = Seq(City.arg, name)
          trimmedText(s"$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(City.length, s"$prefix.error.length", Seq(City.arg.capitalize, name, City.length)),
                regexp(City.regex, s"$prefix.error.invalid", Seq(City.arg.capitalize, name))
              )
            )
        },
        PostalCode.field -> {
          val args = Seq(name)
          if (isPostalCodeRequired) {
            trimmedText(s"$prefix.error.postalCode.required", args)
              .verifying(
                StopOnFirstFail[String](
                  maxLength(PostalCode.length, s"$prefix.error.postalCode.length", args :+ PostalCode.length),
                  regexp(PostalCode.regex, s"$prefix.error.postalCode.invalid", args)
                )
              )
              .transform[Option[String]](Some(_), _.getOrElse(""))
          } else {
            optional(
              trimmedText()
                .verifying(
                  StopOnFirstFail[String](
                    maxLength(PostalCode.length, s"$prefix.error.postalCode.length", args :+ PostalCode.length),
                    regexp(PostalCode.regex, s"$prefix.error.postalCode.invalid", args)
                  )
                )
            )
          }
        }
      )(DynamicAddress.apply)(DynamicAddress.unapply)
    )
}

object DynamicAddressFormProvider {

  def apply(prefix: String, name: String, isPostalCodeRequired: Boolean)(implicit messages: Messages): Form[DynamicAddress] =
    new DynamicAddressFormProvider()(prefix, name, isPostalCodeRequired)
}
