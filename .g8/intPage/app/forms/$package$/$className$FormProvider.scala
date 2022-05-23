package forms.$package$

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class $className$FormProvider @Inject() extends Mappings {

  def apply(): Form[Int] =
    Form(
      "value" -> int("$package$.$className;format="decap"$.error.required",
        "$package$.$className;format="decap"$.error.wholeNumber",
        "$package$.$className;format="decap"$.error.nonNumeric")
        .verifying(maximumValue($maximum$, "$package$.$className;format="decap"$.error.maximum"))
    )
}
