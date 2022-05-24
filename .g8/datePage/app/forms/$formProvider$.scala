package forms

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.Form

import java.time.LocalDate

class $formProvider$ @Inject() extends Mappings {

  def apply(prefix: String): Form[LocalDate] =
    Form(
      "value" -> localDate(
          invalidKey     = s"\$prefix.error.invalid",
          allRequiredKey = s"\$prefix.error.required.all",
          twoRequiredKey = s"\$prefix.error.required.two",
          requiredKey    = s"\$prefix.error.required"
        )
    )
}
