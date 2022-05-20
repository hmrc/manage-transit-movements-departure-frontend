package forms.$package$

import forms.StopOnFirstFail
import javax.inject.Inject
import models.Address.Constants.{buildingAndStreetLength, cityLength, postcodeLength}
import models.domain.StringFieldRegex.stringFieldRegex
import models.reference.Country
import models.{Address, CountryList}
import play.api.data.Form
import play.api.data.Forms.mapping

import javax.inject.Inject

class $className$FormProvider @Inject() extends Mappings {

  def apply(name: String): Form[Address] = Form(
    mapping(
      "buildingAndStreet" -> text("$className;format="decap"$.error.required", Seq(Address.Constants.Fields.buildingAndStreetName, name))
        .verifying(
          StopOnFirstFail[String](
            maxLength(Address.Constants.buildingAndStreetLength,
              "$className;format="decap"$.error.length",
              Seq(Address.Constants.Fields.buildingAndStreetName, name)
            ),
            regexp(stringFieldRegexAsterisk, "$className;format="decap"$.error.invalid", Seq(Address.Constants.Fields.buildingAndStreetName, name))
          )
        ),
      "city" -> text("$className;format="decap"$.error.required", args = Seq(Address.Constants.Fields.city, name))
        .verifying(
          StopOnFirstFail[String](
            maxLength(Address.Constants.cityLength, "$className;format="decap"$.error.length", args = Seq(Address.Constants.Fields.city, name)),
            regexp(stringFieldRegexAsterisk, "$className;format="decap"$.error.invalid", Seq(Address.Constants.Fields.city, name))
          )
        ),
      "postcode" -> text("$className;format="decap"$.error.postcode.required", args = Seq(name))
        .verifying(
          StopOnFirstFail[String](
            maxLength(Address.Constants.postcodeLength, "$className;format="decap"$.error.postcode.length", args = Seq(name)),
            regexp(Address.Constants.postCodeRegex, "$className;format="decap"$.error.postcode.invalid", args = Seq(name)),
            regexp(Address.Constants.postCodeFormatRegex, "$className;format="decap"$.error.postcode.invalidFormat", args = Seq(name))
          )
        )
    )(Address.apply)(Address.unapply)
  )
}
