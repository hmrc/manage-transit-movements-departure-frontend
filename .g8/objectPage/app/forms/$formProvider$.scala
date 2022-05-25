package forms

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form
import models.$objectClassName$

class $formProvider$ @Inject() extends Mappings {

  def apply(prefix: String): Form[$objectClassName$] =
    Form(
      mapping(
        "value" -> text(s"\$prefix.error.required")
          .verifying(
            StopOnFirstFail[String](
              maxLength($objectClassName$.Constants.maxLength,
                s"\$prefix.error.length",
                args = Seq($objectClassName$.Constants.maxLength)
              )
            )
          )
      )($className$.apply)($className$.unapply)
    )
}
