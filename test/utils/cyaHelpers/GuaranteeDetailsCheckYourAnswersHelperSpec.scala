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

package utils.cyaHelpers

import base.SpecBase
import controllers.guaranteeDetails.{routes => gdRoutes}
import generators.Generators
import models.guaranteeDetails.GuaranteeType._
import models.{CheckMode, DeclarationType, Index, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import pages.guaranteeDetails._
import pages.preTaskList.DeclarationTypePage
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import utils.cyaHelpers.guaranteeDetails.GuaranteeDetailsCheckYourAnswersHelper

class GuaranteeDetailsCheckYourAnswersHelperSpec extends SpecBase with Generators {

  "when empty user answers" - {
    "must return empty list of list items" in {
      val userAnswers = emptyUserAnswers

      val helper = new GuaranteeDetailsCheckYourAnswersHelper(userAnswers, NormalMode)
      helper.listItems mustBe Nil
    }
  }

  "when user answers populated with a complete guarantee" - {
    "must return one list item" in {
      val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
      val userAnswers = emptyUserAnswers
        .setValue(DeclarationTypePage, declarationType)
        .setValue(GuaranteeTypePage(Index(0)), CashDepositGuarantee)
        .setValue(OtherReferenceYesNoPage(Index(0)), false)

      val helper = new GuaranteeDetailsCheckYourAnswersHelper(userAnswers, NormalMode)
      helper.listItems mustBe Seq(
        Right(
          ListItem(
            name = "(3) Individual guarantee in cash or an equivalent recognised by the customs authorities",
            changeUrl = gdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn, Index(0)).url,
            removeUrl = gdRoutes.RemoveGuaranteeYesNoController.onPageLoad(userAnswers.lrn, Index(0)).url
          )
        )
      )
    }
  }

  "when user answers populated with a complete guarantee and in progress guarantee" - {
    "must return two list items" in {
      val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
      val userAnswers = emptyUserAnswers
        .setValue(DeclarationTypePage, declarationType)
        .setValue(GuaranteeTypePage(Index(0)), CashDepositGuarantee)
        .setValue(OtherReferenceYesNoPage(Index(0)), false)
        .setValue(GuaranteeTypePage(Index(1)), GuaranteeWaiver)
        .setValue(GuaranteeTypePage(Index(2)), GuaranteeWaiverByAgreement)
        .setValue(GuaranteeTypePage(Index(3)), GuaranteeNotRequired)

      val helper = new GuaranteeDetailsCheckYourAnswersHelper(userAnswers, NormalMode)
      helper.listItems mustBe Seq(
        Right(
          ListItem(
            name = "(3) Individual guarantee in cash or an equivalent recognised by the customs authorities",
            changeUrl = gdRoutes.CheckYourAnswersController.onPageLoad(userAnswers.lrn, Index(0)).url,
            removeUrl = gdRoutes.RemoveGuaranteeYesNoController.onPageLoad(userAnswers.lrn, Index(0)).url
          )
        ),
        Left(
          ListItem(
            name = "(0) Guarantee waiver",
            changeUrl = gdRoutes.ReferenceNumberController.onPageLoad(userAnswers.lrn, NormalMode, Index(1)).url,
            removeUrl = gdRoutes.RemoveGuaranteeYesNoController.onPageLoad(userAnswers.lrn, Index(1)).url
          )
        ),
        Right(
          ListItem(
            name = "(A) Guarantee waiver by agreement",
            changeUrl = gdRoutes.GuaranteeTypeController.onPageLoad(userAnswers.lrn, CheckMode, Index(2)).url,
            removeUrl = gdRoutes.RemoveGuaranteeYesNoController.onPageLoad(userAnswers.lrn, Index(2)).url
          )
        ),
        Right(
          ListItem(
            name = "(R) Guarantee not required – goods carried on the Rhine, the Danube or their waterways",
            changeUrl = gdRoutes.GuaranteeTypeController.onPageLoad(userAnswers.lrn, CheckMode, Index(3)).url,
            removeUrl = gdRoutes.RemoveGuaranteeYesNoController.onPageLoad(userAnswers.lrn, Index(3)).url
          )
        )
      )
    }
  }
}
