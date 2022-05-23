package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class IntFormProvider @Inject() extends Mappings {

  def apply(prefix: String, maximum: Int): Form[Int] =
    Form(
      "value" -> int("\$prefix.error.required",
        "\$prefix.error.wholeNumber",
        "\$prefix.error.nonNumeric")
        .verifying(maximumValue(maximum, "\$prefix.error.maximum"))
    )
}
