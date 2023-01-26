package views.draftDepartures

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.draftDepartures.DraftDashboardView

class DraftDashboardViewSpec extends ViewBehaviours {

  override val urlContainsLrn: Boolean = true

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[DraftDashboardView].apply(lrn)(fakeRequest, messages)

  override val prefix: String = "draftDepartures.draftDashboard"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()
}
