package forms

import forms.Constants.identificationNumberLength
import forms.mappings.Mappings
import models.domain.StringFieldRegex.alphaNumericRegex

import javax.inject.Inject
import play.api.data.Form

class MeansIdentificationNumberProvider @Inject() extends Mappings {

  def apply(prefix: String): Form[String] =
    Form(
      "value" -> textWithSpacesRemoved(s"$prefix.error.required")
        .verifying(
          forms.StopOnFirstFail[String](
            regexp(alphaNumericRegex, s"$prefix.error.invalid"),
            maxLength(identificationNumberLength, s"$prefix.error.length")
          )
        )
    )
}
