package forms.$package$

import forms.behaviours.IntFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class $className$FormProviderSpec extends IntFieldBehaviours {

  val requiredKey = "$className;format="decap"$.error.required"
  val maximum = $maximum$

  val generatedInt = Gen.oneOf(1 to maximum)

  val form = new $className$FormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      generatedInt.toString
    )

    behave like intField(
      form,
      fieldName,
      nonNumericError  = FormError(fieldName, "$className;format="decap"$.error.nonNumeric"),
      wholeNumberError = FormError(fieldName, "$className;format="decap"$.error.wholeNumber")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
