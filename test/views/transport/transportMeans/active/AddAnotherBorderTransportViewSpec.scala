package views.transport.transportMeans.active

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.transport.transportMeans.active.AddAnotherBorderTransportView

class AddAnotherBorderTransportViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[AddAnotherBorderTransportView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "transport.transportMeans.active.addAnotherBorderTransport"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
