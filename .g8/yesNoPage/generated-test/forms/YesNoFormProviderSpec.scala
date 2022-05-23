package forms

import forms.behaviours.BooleanFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class YesNoFormProviderSpec extends BooleanFieldBehaviours {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val requiredKey = prefix + ".error.required"
  private val invalidKey  = "error.boolean"

  private val form = new YesNoFormProvider()(prefix)

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}