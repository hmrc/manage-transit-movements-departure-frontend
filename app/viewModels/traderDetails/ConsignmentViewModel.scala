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

package viewModels.traderDetails

import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.traderDetails.ConsignmentCheckYourAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class ConsignmentViewModel(sections: Seq[Section])

object ConsignmentViewModel {

  class ConsignmentViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): ConsignmentViewModel = {
      val helper = new ConsignmentCheckYourAnswersHelper(userAnswers, mode)

      val consignorSection = Section(
        sectionTitle = messages("traderDetails.consignment.checkYourAnswers.consignor"),
        rows = Seq(
          helper.approvedOperator,
          helper.consignorEoriYesNo,
          helper.consignorEori,
          helper.consignorName,
          helper.consignorCountry,
          helper.consignorAddress
        ).flatten
      )

      val consignorContactSection = Section(
        sectionTitle = messages("traderDetails.consignment.checkYourAnswers.consignorContact"),
        rows = Seq(
          helper.addConsignorContact,
          helper.consignorContactName,
          helper.consignorContactTelephoneNumber
        ).flatten
      )

      val consigneeSection = Section(
        sectionTitle = messages("traderDetails.consignment.checkYourAnswers.consignee"),
        rows = Seq(
          helper.moreThanOneConsignee,
          helper.consigneeEoriYesNo,
          helper.consigneeEori,
          helper.consigneeName,
          helper.consigneeCountry,
          helper.consigneeAddress
        ).flatten
      )

      new ConsignmentViewModel(Seq(consignorSection, consignorContactSection, consigneeSection))
    }
  }
}
