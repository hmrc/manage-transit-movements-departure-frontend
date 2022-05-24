package forms

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class $formProvider$ @Inject() extends Mappings {

  def apply(prefix: String, maximum: Int): Form[String] =
    Form(
      "value" -> text(s"\$prefix.error.required")
        .verifying(maxLength(maximum, s"\$prefix.error.length"))
    )
}
