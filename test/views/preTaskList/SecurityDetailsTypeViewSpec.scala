/*
 * Copyright 2024 HM Revenue & Customs
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

package views.preTaskList

import forms.EnumerableFormProvider
import models.NormalMode
import models.reference.SecurityType
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.EnumerableViewBehaviours
import views.html.preTaskList.SecurityDetailsTypeView

class SecurityDetailsTypeViewSpec extends EnumerableViewBehaviours[SecurityType] {

  override def form: Form[SecurityType] = new EnumerableFormProvider()(prefix, values)

  override def applyView(form: Form[SecurityType]): HtmlFormat.Appendable =
    injector.instanceOf[SecurityDetailsTypeView].apply(form, values, lrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "securityDetailsType"

  override def radioItems(fieldId: String, checkedValue: Option[SecurityType] = None): Seq[RadioItem] =
    values.toRadioItems(fieldId, checkedValue)

  override def values: Seq[SecurityType] = Seq(
    SecurityType("0", "Not used for safety and security purposes"),
    SecurityType("1", "ENS")
  )

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithContent(
    "p",
    "This is used by border authorities to analyse the risk of goods entering their territory. You may also need to provide these details to the territories your goods move through."
  )

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
