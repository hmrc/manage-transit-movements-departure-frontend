package views.$package$

import forms.$formProvider$
import generators.Generators
import views.behaviours.InputSelectViewBehaviours
import models.NormalMode
import models.reference.$referenceClass$
import models.$referenceListClass$
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.$package$.$className$View

class $className$ViewSpec extends InputSelectViewBehaviours[$referenceClass$] with Generators {

  private lazy val $referenceClass;format="decap"$1 = arbitrary$referenceClass$.arbitrary.sample.get
  private lazy val $referenceClass;format="decap"$2 = arbitrary$referenceClass$.arbitrary.sample.get
  private lazy val $referenceClass;format="decap"$3 = arbitrary$referenceClass$.arbitrary.sample.get

  override def values: Seq[$referenceClass$] =
    Seq(
      $referenceClass;format="decap"$1,
      $referenceClass;format="decap"$2,
      $referenceClass;format="decap"$3
    )

  override def form: Form[$referenceClass$] = new $formProvider$()(prefix, $referenceListClass$(values))

  override def applyView(form: Form[$referenceClass$]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "$package$.$className;format="decap"$"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithSelect

  behave like pageWithHint("$title$ hint")

  behave like pageWithContent("label", "$title$ label")

  behave like pageWithSubmitButton("Save and continue")
}
