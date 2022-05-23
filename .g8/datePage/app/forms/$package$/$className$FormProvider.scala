package forms.$package$

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.Form

import java.time.LocalDate

class $className$FormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
          invalidKey     = "$package$.$className;format="decap"$.error.invalid",
          allRequiredKey = "$package$.$className;format="decap"$.error.required.all",
          twoRequiredKey = "$package$.$className;format="decap"$.error.required.two",
          requiredKey    = "$package$.$className;format="decap"$.error.required"
        )
    )
}
