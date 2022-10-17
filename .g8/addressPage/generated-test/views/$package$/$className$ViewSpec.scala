package views.$package$

import forms.$formProvider$
import generators.Generators
import models.{DynamicAddress, NormalMode}
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DynamicAddressViewBehaviours
import views.html.$package$.$className$View

class $className$ViewSpec extends DynamicAddressViewBehaviours with Generators {

  private val addressHolderName = Gen.alphaNumStr.sample.value

  override def form: Form[DynamicAddress] = new $formProvider$()(prefix, isPostalCodeRequired, addressHolderName)

  override def applyView(form: Form[DynamicAddress]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, lrn, NormalMode, addressHolderName, isPostalCodeRequired)(fakeRequest, messages)

  override val prefix: String = "$package$.$className;format="decap"$"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading(addressHolderName)

  behave like pageWithAddressInput()

  behave like pageWithSubmitButton("Save and continue")
}
