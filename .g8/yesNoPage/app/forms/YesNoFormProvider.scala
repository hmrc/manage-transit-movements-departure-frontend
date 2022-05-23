package forms.$package$

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class $className$FormProvider @Inject() extends Mappings {

  def apply(prefix: String): Form[Boolean] =
    Form(
      "value" -> boolean(s"$prefix.error.required")
  )
}