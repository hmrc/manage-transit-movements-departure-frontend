package forms

import forms.Constants.identificationNumberLength
import forms.mappings.Mappings
import models.domain.StringFieldRegex.alphaNumericRegex

import javax.inject.Inject
import play.api.data.Form

class MeansIdentificationNumberProvider @Inject() extends Mappings {

  def apply(prefix: String, arg: String): Form[String] =
    Form(
      "value" -> text(s"$prefix.error.required", arg)
        .verifying(
          forms.StopOnFirstFail[String](
            regexp(alphaNumericRegex, s"$prefix.error.invalid", arg),
            maxLength(identificationNumberLength, s"$prefix.error.length", arg)
          )
        )
    )
}
