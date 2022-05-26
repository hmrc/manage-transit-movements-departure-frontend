package forms

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.AddressLine._
import models.reference.{Country, CountryCode}
import models.{AddressLine, CountryList}
import org.scalacheck.Gen
import play.api.data.FormError

class $formProvider$Spec extends StringFieldBehaviours with SpecBase {

  private val prefix = Gen.alphaNumStr.sample.value
  private val name   = Gen.alphaNumStr.sample.value

  private val country   = Country(CountryCode("GB"), "United Kingdom")
  private val countries = CountryList(Seq(country))

  private val requiredKey = s"\$prefix.error.required"
  private val lengthKey   = s"\$prefix.error.length"
  private val invalidKey  = s"\$prefix.error.invalid"

  private val form = new AddressFormProvider()(prefix, name, countries)

  ".addressLine1" - {

    val fieldName = AddressLine1.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(AddressLine1.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = AddressLine1.length,
      lengthError = FormError(fieldName, lengthKey, Seq(AddressLine1.arg, name, AddressLine1.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(AddressLine1.arg, name))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidKey, Seq(AddressLine1.arg, name)),
      length = AddressLine1.length
    )
  }

  ".addressLine2" - {

    val fieldName = AddressLine2.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(AddressLine2.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = AddressLine2.length,
      lengthError = FormError(fieldName, lengthKey, Seq(AddressLine2.arg, name, AddressLine2.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(AddressLine2.arg, name))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidKey, Seq(AddressLine2.arg, name)),
      length = AddressLine2.length
    )
  }

  ".postalCode" - {

    val postcodeInvalidKey = s"\$prefix.error.postalCode.invalid"

    val fieldName = PostalCode.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(PostalCode.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = PostalCode.length,
      lengthError = FormError(fieldName, lengthKey, Seq(PostalCode.arg, name, PostalCode.length))
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(PostalCode.arg, name))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, postcodeInvalidKey, Seq(name)),
      length = PostalCode.length
    )
  }

  ".country" - {

    import AddressLine.Country

    val fieldName = Country.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = nonEmptyString
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(Country.arg, name))
    )

    "not bind if country code does not exist in the country list" in {
      val result        = form.bind(Map(fieldName -> "foobar")).apply(fieldName)
      val expectedError = FormError(fieldName, requiredKey, Seq(Country.arg, name))
      result.errors must contain(expectedError)
    }

    "bind a country code which is in the list" in {
      val result = form.bind(Map(fieldName -> country.code.code)).apply(fieldName)
      result.value.value mustBe country.code.code
    }
  }
}
