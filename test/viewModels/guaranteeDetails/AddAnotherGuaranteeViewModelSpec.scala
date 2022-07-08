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

package viewModels.guaranteeDetails

import base.SpecBase
import controllers.guaranteeDetails.{routes => gdRoutes}
import generators.Generators
import models.guaranteeDetails.GuaranteeType.{GuaranteeNotRequired, GuaranteeWaiver}
import models.{DeclarationType, Index, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import pages.guaranteeDetails.GuaranteeTypePage
import pages.preTaskList.DeclarationTypePage
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

class AddAnotherGuaranteeViewModelSpec extends SpecBase with Generators {

  "must get list items" in {

    val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
    val userAnswers = emptyUserAnswers
      .setValue(DeclarationTypePage, declarationType)
      .setValue(GuaranteeTypePage(Index(0)), GuaranteeNotRequired)
      .setValue(GuaranteeTypePage(Index(1)), GuaranteeWaiver)

    val result = AddAnotherGuaranteeViewModel(userAnswers)
    result.listItems mustBe Seq(
      ListItem(
        name = "(R) Guarantee not required – goods carried on the Rhine, the Danube or their waterways",
        changeUrl = gdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn, Index(0)).url,
        removeUrl = gdRoutes.RemoveGuaranteeYesNoController.onPageLoad(lrn, Index(0)).url
      ),
      ListItem(
        name = "(0) Guarantee waiver",
        changeUrl = gdRoutes.ReferenceNumberController.onPageLoad(userAnswers.lrn, NormalMode, Index(1)).url,
        removeUrl = gdRoutes.RemoveGuaranteeYesNoController.onPageLoad(lrn, Index(1)).url
      )
    )
  }

}
