package views.$package$

import forms.$formProvider$
import models.{NormalMode, $objectClassName$}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.$package$.$className$View
import org.scalacheck.Arbitrary

class $className$ViewSpec extends InputTextViewBehaviours[$objectClassName$] {

  override val prefix: String = "$package$.$className;format="decap"$"

  override def form: Form[$objectClassName$] = new $formProvider$()(prefix)

  override def applyView(form: Form[$objectClassName$]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, lrn, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[$objectClassName$] = arbitrary$objectClassName$

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithoutHint

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
