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

class $formProvider$ @Inject() extends Mappings {

  def apply(prefix: String, name: String): Form[Address] = Form(
    mapping(
      "numberAndStreet" -> {
        lazy val args = Seq(numberAndStreet, name)
        text(s"\$prefix.error.required", args)
          .verifying(
            StopOnFirstFail[String](
              maxLength(buildingAndStreetLength, s"\$prefix.error.length", args),
              regexp(stringFieldRegex, s"\$prefix.error.invalid", args)
            )
          )
      },
      "town" -> {
        lazy val args = Seq(town, name)
        text(s"\$prefix.error.required", args)
          .verifying(
            StopOnFirstFail[String](
              maxLength(townLength, s"\$prefix.error.length", args),
              regexp(stringFieldRegex, s"\$prefix.error.invalid", args)
            )
          )
      },
      "postcode" -> {
        lazy val args = Seq(name)
        text(s"\$prefix.error.required", postcode +: args)
          .verifying(
            StopOnFirstFail[String](
              regexp(postCodeRegex, s"\$prefix.error.postcode.invalid", args),
              regexp(postCodeFormatRegex, s"\$prefix.error.postcode.invalidFormat", args)
            )
          )
      }
    )(Address.apply)(Address.unapply)
  )
}
