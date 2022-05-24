package forms.$package$

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.$package$.$className$

class $formProvider$ @Inject() extends Mappings {

  def apply(): Form[$className$] =
    Form(
      "value" -> enumerable[$className$]("$package$.$className;format="decap"$.error.required")
    )
}
