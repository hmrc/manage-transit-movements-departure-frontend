package forms.$package$

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class $className$FormProvider @Inject() extends Mappings {

  def apply(): Form[Int] =
    Form(
      "value" -> int("$className;format="decap"$.error.required",
        "$className;format="decap"$.error.wholeNumber",
        "$className;format="decap"$.error.nonNumeric")
        .verifying(maximumValue($maximum$, "$className;format="decap"$.error.maximum"))
    )
}
