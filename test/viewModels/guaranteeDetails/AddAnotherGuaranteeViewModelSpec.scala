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
import generators.Generators
import models.guaranteeDetails.GuaranteeType._
import models.{DeclarationType, Index}
import org.scalacheck.Arbitrary.arbitrary
import pages.guaranteeDetails.guarantee.{GuaranteeTypePage, OtherReferenceYesNoPage}
import pages.preTaskList.DeclarationTypePage

class AddAnotherGuaranteeViewModelSpec extends SpecBase with Generators {

  "must get list items" in {

    val declarationType = arbitrary[DeclarationType](arbitraryNonOption4DeclarationType).sample.value
    val userAnswers = emptyUserAnswers
      .setValue(DeclarationTypePage, declarationType)
      .setValue(GuaranteeTypePage(Index(0)), CashDepositGuarantee)
      .setValue(OtherReferenceYesNoPage(Index(0)), false)
      .setValue(GuaranteeTypePage(Index(1)), GuaranteeWaiver)

    val result = AddAnotherGuaranteeViewModel(userAnswers)
    result.listItems.length mustBe 2
  }

}
