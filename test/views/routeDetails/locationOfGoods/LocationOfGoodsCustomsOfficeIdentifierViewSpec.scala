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

package views.routeDetails.locationOfGoods

import forms.CustomsOfficeFormProvider
import generators.Generators
import views.behaviours.InputSelectViewBehaviours
import models.NormalMode
import models.reference.CustomsOffice
import models.CustomsOfficeList
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.routeDetails.locationOfGoods.LocationOfGoodsCustomsOfficeIdentifierView

class LocationOfGoodsCustomsOfficeIdentifierViewSpec extends InputSelectViewBehaviours[CustomsOffice] with Generators {

  private lazy val customsOffice1 = arbitraryCustomsOffice.arbitrary.sample.get
  private lazy val customsOffice2 = arbitraryCustomsOffice.arbitrary.sample.get
  private lazy val customsOffice3 = arbitraryCustomsOffice.arbitrary.sample.get

  override def values: Seq[CustomsOffice] =
    Seq(
      customsOffice1,
      customsOffice2,
      customsOffice3
    )

  override def form: Form[CustomsOffice] = new CustomsOfficeFormProvider()(prefix, CustomsOfficeList(values))

  override def applyView(form: Form[CustomsOffice]): HtmlFormat.Appendable =
    injector.instanceOf[LocationOfGoodsCustomsOfficeIdentifierView].apply(form, lrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "routeDetails.locationOfGoods.locationOfGoodsCustomsOfficeIdentifier"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Route details")

  behave like pageWithHeading()

  behave like pageWithSelect

  behave like pageWithHint("Enter the office location or code, like Dover or GB000060.")

  behave like pageWithSubmitButton("Save and continue")
}
