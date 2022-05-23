/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package views.$package$

import forms.$package$.$className$FormProvider
import models.NormalMode
import models.reference.$referenceClass$
import models.$referenceListClass$
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.$package$.$className$View

class $className$ViewSpec extends InputSelectViewBehaviours[$referenceClass$] with Generators {

  override def form: Form[$className$] = new $className$FormProvider()($referenceListClass$(Nil))

  override def applyView(form: Form[$referenceClass$]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "$package$.$className;format="decap"$"

  override def values: Seq[$referenceClass$] =
    Seq(
      arbitrary$referenceClass$.arbitrary.sample.get,
      arbitrary$referenceClass$.arbitrary.sample.get,
      arbitrary$referenceClass$.arbitrary.sample.get
    )

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithSelect

  behave like pageWithHint("$className;format="decap"$ hint")

  behave like pageWithContent("label", "$className;format="decap"$ label")

  behave like pageWithSubmitButton("Continue")
}
