package forms.$package$

import forms.StopOnFirstFail
import models.domain.StringFieldRegex.stringFieldRegex
import play.api.data.Form
import play.api.data.Forms.mapping
import forms.mappings.Mappings
import javax.inject.Inject
import models.Address

class $formProvider$ @Inject() extends Mappings {

  def apply(prefix: String, name: String): Form[Address] = Form(
    mapping(
      "buildingAndStreet" -> text(s"\$prefix.error.required", Seq(Address.Constants.Fields.buildingAndStreetName, name))
        .verifying(
          StopOnFirstFail[String](
            maxLength(Address.Constants.buildingAndStreetLength,
              s"\$prefix.error.length",
              Seq(Address.Constants.Fields.buildingAndStreetName, name)
            ),
            regexp(stringFieldRegex, s"\$prefix.error.invalid", Seq(Address.Constants.Fields.buildingAndStreetName, name))
          )
        ),
      "city" -> text(s"\$prefix.error.required", args = Seq(Address.Constants.Fields.city, name))
        .verifying(
          StopOnFirstFail[String](
            maxLength(Address.Constants.cityLength, s"\$prefix.error.length", args = Seq(Address.Constants.Fields.city, name)),
            regexp(stringFieldRegex, s"\$prefix.error.invalid", Seq(Address.Constants.Fields.city, name))
          )
        ),
      "postcode" -> text(s"\$prefix.error.postcode.required", args = Seq(name))
        .verifying(
          StopOnFirstFail[String](
            maxLength(Address.Constants.postcodeLength, s"\$prefix.error.postcode.length", args = Seq(name)),
            regexp(Address.Constants.postCodeRegex, s"\$prefix.error.postcode.invalid", args = Seq(name)),
            regexp(Address.Constants.postCodeFormatRegex, s"\$prefix.error.postcode.invalidFormat", args = Seq(name))
          )
        )
    )(Address.apply)(Address.unapply)
  )
}
