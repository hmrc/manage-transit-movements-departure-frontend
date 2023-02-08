package views.transport.equipment

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.transport.equipment.AddAnotherTransportEquipmentView

class AddAnotherTransportEquipmentViewSpec extends ViewBehaviours {

  override val urlContainsLrn: Boolean = true

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[AddAnotherTransportEquipmentView].apply(lrn)(fakeRequest, messages)

  override val prefix: String = "transport.equipment.addAnotherTransportEquipment"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()
}
