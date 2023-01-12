package views.transport.authorisationsAndLimit.limit

import forms.DateFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DateInputViewBehaviours
import views.html.transport.authorisationsAndLimit.limit.LimitDateView

import java.time.LocalDate

class LimitDateViewSpec extends DateInputViewBehaviours {

  override def form: Form[LocalDate] = new DateFormProvider()(prefix)

  override def applyView(form: Form[LocalDate]): HtmlFormat.Appendable =
    injector.instanceOf[LimitDateView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "transport.authorisationsAndLimit.limit.limitDate"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithDateInput

  behave like pageWithSubmitButton("Save and continue")
}
