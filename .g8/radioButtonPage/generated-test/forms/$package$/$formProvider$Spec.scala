package forms.$package$

import forms.behaviours.OptionFieldBehaviours
import models.$package$.$className$
import play.api.data.FormError

class $formProvider$Spec extends OptionFieldBehaviours {

  val form = new $formProvider$()()

  ".value" - {

    val fieldName = "value"
    val requiredKey = "$package$.$className;format="decap"$.error.required"

    behave like optionsField[$className$](
      form,
      fieldName,
      validValues  = $className$.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
