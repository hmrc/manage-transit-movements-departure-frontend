package forms.$package$

import forms.StopOnFirstFail
import models.domain.StringFieldRegex.stringFieldRegex
import play.api.data.Form
import play.api.data.Forms.mapping
import forms.mappings.Mappings
import javax.inject.Inject
import models.Address

class $formProvider$ @Inject() extends Mappings {

  def apply(name: String): Form[Address] = Form(
    mapping(
      "buildingAndStreet" -> text("$package$.$className;format="decap"$.error.required", Seq(Address.Constants.Fields.buildingAndStreetName, name))
        .verifying(
          StopOnFirstFail[String](
            maxLength(Address.Constants.buildingAndStreetLength,
              "$package$.$className;format="decap"$.error.length",
              Seq(Address.Constants.Fields.buildingAndStreetName, name)
            ),
            regexp(stringFieldRegex, "$package$.$className;format="decap"$.error.invalid", Seq(Address.Constants.Fields.buildingAndStreetName, name))
          )
        ),
      "city" -> text("$package$.$className;format="decap"$.error.required", args = Seq(Address.Constants.Fields.city, name))
        .verifying(
          StopOnFirstFail[String](
            maxLength(Address.Constants.cityLength, "$package$.$className;format="decap"$.error.length", args = Seq(Address.Constants.Fields.city, name)),
            regexp(stringFieldRegex, "$package$.$className;format="decap"$.error.invalid", Seq(Address.Constants.Fields.city, name))
          )
        ),
      "postcode" -> text("$package$.$className;format="decap"$.error.postcode.required", args = Seq(name))
        .verifying(
          StopOnFirstFail[String](
            maxLength(Address.Constants.postcodeLength, "$package$.$className;format="decap"$.error.postcode.length", args = Seq(name)),
            regexp(Address.Constants.postCodeRegex, "$package$.$className;format="decap"$.error.postcode.invalid", args = Seq(name)),
            regexp(Address.Constants.postCodeFormatRegex, "$package$.$className;format="decap"$.error.postcode.invalidFormat", args = Seq(name))
          )
        )
    )(Address.apply)(Address.unapply)
  )
}
