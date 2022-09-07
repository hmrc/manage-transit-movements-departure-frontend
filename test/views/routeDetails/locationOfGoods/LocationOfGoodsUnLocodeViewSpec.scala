package views.routeDetails.locationOfGoods

import forms.UnLocodeFormProvider
import generators.Generators
import models.reference.UnLocode
import models.{NormalMode, UnLocodeList}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.routeDetails.locationOfGoods.LocationOfGoodsUnLocodeView

class LocationOfGoodsUnLocodeViewSpec extends InputSelectViewBehaviours[UnLocode] with Generators {

  private lazy val unLocode1 = arbitraryUnLocode.arbitrary.sample.get
  private lazy val unLocode2 = arbitraryUnLocode.arbitrary.sample.get
  private lazy val unLocode3 = arbitraryUnLocode.arbitrary.sample.get

  override def values: Seq[UnLocode] =
    Seq(
      unLocode1,
      unLocode2,
      unLocode3
    )

  override def form: Form[UnLocode] = new UnLocodeFormProvider()(prefix, UnLocodeList(values))

  override def applyView(form: Form[UnLocode]): HtmlFormat.Appendable =
    injector.instanceOf[LocationOfGoodsUnLocodeView].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "routeDetails.locationOfGoods.locationOfGoodsUnLocode"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithSelect

  behave like pageWithHint("What is the UN/LOCODE for the location of goods? hint")

  behave like pageWithContent("label", "What is the UN/LOCODE for the location of goods? label")

  behave like pageWithSubmitButton("Save and continue")
}
