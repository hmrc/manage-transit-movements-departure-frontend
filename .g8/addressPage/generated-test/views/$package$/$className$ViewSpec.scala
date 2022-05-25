package views.$package$

import forms.$formProvider$
import models.{Address, NormalMode}
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.IndividualAddressViewBehaviours
import views.html.$package$.$className$View

class $className$ViewSpec extends IndividualAddressViewBehaviours {

  private val addressHolderName = Gen.alphaNumStr.sample.value

  override def form: Form[Address] = new $formProvider$()(prefix, addressHolderName)

  override def applyView(form: Form[Address]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, lrn, NormalMode, addressHolderName)(fakeRequest, messages)

  override val prefix: String = "$package$.$className;format="decap"$"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithAddressInput()

  behave like pageWithSubmitButton("Continue")
}
