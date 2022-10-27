package views.transport.transportMeans.departure

import forms.MeansIdentificationNumberProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.transport.transportMeans.departure.MeansIdentificationNumberView

class MeansIdentificationNumberViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String = "transport.transportMeans.departure.meansIdentificationNumber"

  override def form: Form[String] = new MeansIdentificationNumberProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[MeansIdentificationNumberView].apply(form, lrn, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithoutHint()

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Save and continue")
}
