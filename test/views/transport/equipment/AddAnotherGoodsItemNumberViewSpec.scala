package views.transport.equipment

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.transport.equipment.AddAnotherGoodsItemNumberView

class AddAnotherGoodsItemNumberViewSpec extends ViewBehaviours {

  override val urlContainsLrn: Boolean = true

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[AddAnotherGoodsItemNumberView].apply(lrn)(fakeRequest, messages)

  override val prefix: String = "transport.equipment.addAnotherGoodsItemNumber"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()
}
