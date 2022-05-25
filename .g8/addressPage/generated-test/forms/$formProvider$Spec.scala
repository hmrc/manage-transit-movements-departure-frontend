package forms

class $formProvider$Spec extends AddressFormProviderSpec {

  override val formProvider: AddressFormProvider = new $formProvider$()

  behave like addressFormProvider()
}
