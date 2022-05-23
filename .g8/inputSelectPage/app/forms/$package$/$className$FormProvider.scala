package forms.$package$

import javax.inject.Inject
import forms.mappings.Mappings
import play.api.data.Form
import models.reference.$referenceClass$
import models.$referenceListClass$

class $className$FormProvider @Inject() extends Mappings {

  def apply(prefix: String, $referenceClassValue;format="decap"$: $referenceListClass$): Form[$referenceClass$] =

    Form(
      "value" -> text(s"\$prefix.error.required")
        .verifying(s"\$prefix.error.required", value => $referenceClassValue;format="decap"$.getAll.exists(_.id == value))
        .transform[$referenceClass$](value => $referenceClassValue;format="decap"$.get$referenceClass$(value).get, _.id)
    )
}
