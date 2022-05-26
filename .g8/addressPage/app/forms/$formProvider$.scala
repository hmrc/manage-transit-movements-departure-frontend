package forms

import forms.mappings.Mappings
import models.AddressLine._
import models.{Address, CountryList}
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.i18n.Messages

import javax.inject.Inject

class $formProvider$ @Inject() extends Mappings {

  def apply(prefix: String, name: String, countryList: CountryList)(implicit messages: Messages): Form[Address] =
    Form(
      mapping(
        AddressLine1.field -> {
          lazy val args = Seq(AddressLine1.arg, name)
          trimmedText(s"\$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(AddressLine1.length, s"\$prefix.error.length", args :+ AddressLine1.length),
                regexp(AddressLine1.regex, s"\$prefix.error.invalid", args)
              )
            )
        },
        AddressLine2.field -> {
          lazy val args = Seq(AddressLine2.arg, name)
          trimmedText(s"\$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(AddressLine2.length, s"\$prefix.error.length", args :+ AddressLine2.length),
                regexp(AddressLine2.regex, s"\$prefix.error.invalid", args)
              )
            )
        },
        PostalCode.field -> {
          lazy val args = Seq(PostalCode.arg, name)
          trimmedText(s"\$prefix.error.required", args)
            .verifying(
              StopOnFirstFail[String](
                maxLength(PostalCode.length, s"\$prefix.error.length", args :+ PostalCode.length),
                regexp(PostalCode.regex, s"\$prefix.error.postalCode.invalid", Seq(name))
              )
            )
        },
        Country.field -> {
          country(countryList, s"\$prefix.error.required", Seq(Country.arg, name))
        }
      )(Address.apply)(Address.unapply)
    )
}
