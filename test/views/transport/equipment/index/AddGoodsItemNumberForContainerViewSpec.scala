package views.transport.equipment.index

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.transport.equipment.index.AddGoodsItemNumberForContainerView

class AddGoodsItemNumberForContainerViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[AddGoodsItemNumberForContainerView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "transport.equipment.index.addGoodsItemNumberForContainer"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
