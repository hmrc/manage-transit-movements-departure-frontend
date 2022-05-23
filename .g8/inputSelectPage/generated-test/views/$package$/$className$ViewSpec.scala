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

  override def form: Form[$referenceClass$] = new $className$FormProvider()($referenceListClass$(values))

  override def applyView(form: Form[$referenceClass$]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "$package$.$className;format="decap"$"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithSelect

  behave like pageWithHint("$className;format="decap"$ hint")

  behave like pageWithContent("label", "$className;format="decap"$ label")

  behave like pageWithSubmitButton("Continue")
}
