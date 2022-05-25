package forms

import models.Address._

import javax.inject.Inject

abstract class $formProvider$ @Inject() extends AddressFormProvider {

  override val addressLine1: AddressLine = NumberAndStreet
  override val addressLine2: AddressLine = Town
}
