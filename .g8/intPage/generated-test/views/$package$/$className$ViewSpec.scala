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
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.$package$.$className$View
import org.scalacheck.{Arbitrary, Gen}

class $className$ViewSpec extends InputTextViewBehaviours[Int] {

  override def form: Form[Int] = new $className$FormProvider()()

  override def applyView(form: Form[Int]): HtmlFormat.Appendable =
    injector.instanceOf[$className$View].apply(form, lrn, NormalMode)(fakeRequest, messages)

  private val maxInt = $maximum$

  implicit override val arbitraryT: Arbitrary[Int] = Arbitrary(maxInt)

  override val prefix: String = "$package$.$className;format="decap"$"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithoutHint

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
