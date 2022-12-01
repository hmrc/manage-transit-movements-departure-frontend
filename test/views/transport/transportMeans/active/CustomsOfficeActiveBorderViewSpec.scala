package views.transport.transportMeans.active

import forms.CustomsOfficeFormProvider
import views.behaviours.InputSelectViewBehaviours
import models.NormalMode
import models.reference.CustomsOffice
import models.CustomsOfficeList
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.transport.transportMeans.active.CustomsOfficeActiveBorderView

class CustomsOfficeActiveBorderViewSpec extends InputSelectViewBehaviours[CustomsOffice] {

  override def form: Form[CustomsOffice] = new CustomsOfficeFormProvider()(prefix, CustomsOfficeList(values))

  override def applyView(form: Form[CustomsOffice]): HtmlFormat.Appendable =
    injector.instanceOf[CustomsOfficeActiveBorderView].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[CustomsOffice] = arbitraryCustomsOffice

  override val prefix: String = "transport.transportMeans.active.customsOfficeActiveBorder"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithHint("Where is the office of transit hint")

  behave like pageWithContent("label", "Where is the office of transit label")

  behave like pageWithSubmitButton("Save and continue")
}
