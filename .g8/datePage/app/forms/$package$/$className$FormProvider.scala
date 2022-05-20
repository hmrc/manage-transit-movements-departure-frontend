package forms.$package$

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.Form

import java.time.LocalDate

class $className$FormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
          invalidKey     = "$className;format="decap"$.error.invalid",
          allRequiredKey = "$className;format="decap"$.error.required.all",
          twoRequiredKey = "$className;format="decap"$.error.required.two",
          requiredKey    = "$className;format="decap"$.error.required"
        )
    )
}
