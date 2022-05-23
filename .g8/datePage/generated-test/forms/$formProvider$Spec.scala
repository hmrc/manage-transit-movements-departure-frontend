package forms

import forms.behaviours.DateBehaviours
import org.scalacheck.Gen

import java.time.{LocalDate, ZoneOffset}

class $formProvider$Spec extends DateBehaviours {

  private val prefix      = Gen.alphaNumStr.sample.value
  val form = new $formProvider$()(prefix)

  ".value" - {

    val validData = datesBetween(
      min = LocalDate.of(2000, 1, 1),
      max = LocalDate.now(ZoneOffset.UTC)
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", s"\$prefix.error.required.all")
  }
}
