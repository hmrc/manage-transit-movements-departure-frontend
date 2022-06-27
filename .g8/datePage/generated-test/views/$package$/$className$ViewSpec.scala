package views.$package$

import forms.$formProvider$
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DateInputViewBehaviours
import views.html.$package$.$className$View

import java.time.LocalDate

class $className$ViewSpec extends DateInputViewBehaviours {

  override def form: Form[LocalDate] = new $formProvider$()(prefix)

  override def applyView(form: Form[LocalDate]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "$package$.$className;format="decap"$"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithDateInput

  behave like pageWithSubmitButton("Save and continue")
}
