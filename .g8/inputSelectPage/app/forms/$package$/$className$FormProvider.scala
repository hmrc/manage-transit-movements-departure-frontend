package forms.$package$

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.Form
import models.reference.$referenceClass$
import models.$referenceListClass$

class $className$FormProvider @Inject() extends Mappings {

  def apply($referenceClassValue;format="decap"$: $referenceListClass$): Form[$referenceClass$] =

    Form(
      "value" -> text("$package$.$className;format="decap"$.error.required")
        .verifying("$package$.$className;format="decap"$.error.required", value => $referenceClassValue;format="decap"$.getAll.exists(_.id == value))
        .transform[$referenceClass$](value => $referenceClassValue;format="decap"$.get$referenceClass$(value).get, _.id)
    )
}
