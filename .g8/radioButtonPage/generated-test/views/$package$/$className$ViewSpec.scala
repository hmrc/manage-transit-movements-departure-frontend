package views.$package$

import forms.EnumerableFormProvider
import models.NormalMode
import models.$package$.$className$
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.$package$.$className$View

class $className$ViewSpec extends RadioViewBehaviours[$className$] {

  override def form: Form[$className$] = new EnumerableFormProvider()(prefix)

  override def applyView(form: Form[$className$]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "$package$.$className;format="decap"$"

  override def values(fieldId: String, checkedValue: Option[$className$] = None): Seq[RadioItem] =
    values.toRadioItems(fieldId, checkedValue)

  override def values: Seq[$className$] = $className$.values

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Save and continue")
}
