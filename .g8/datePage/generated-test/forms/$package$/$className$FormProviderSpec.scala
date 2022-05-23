package forms.$package$

import forms.behaviours.DateBehaviours

import java.time.{LocalDate, ZoneOffset}

class $className$FormProviderSpec extends DateBehaviours {

  val form = new $className$FormProvider()()

  ".value" - {

    val validData = datesBetween(
      min = LocalDate.of(2000, 1, 1),
      max = LocalDate.now(ZoneOffset.UTC)
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "$package$.$className;format="decap"$.error.required.all")
  }
}
