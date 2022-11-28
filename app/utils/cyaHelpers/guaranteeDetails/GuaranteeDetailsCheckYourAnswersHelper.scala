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

package utils.cyaHelpers.guaranteeDetails

import controllers.guaranteeDetails.guarantee.{routes => guaranteeRoutes}
import models.journeyDomain.guaranteeDetails.GuaranteeDomain
import models.{GuaranteeType, Mode, UserAnswers}
import pages.guaranteeDetails.guarantee.GuaranteeTypePage
import pages.sections.guaranteeDetails.GuaranteeDetailsSection
import play.api.i18n.Messages
import utils.cyaHelpers.AnswersHelper
import viewModels.ListItem

class GuaranteeDetailsCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(GuaranteeDetailsSection) {
      index =>
        buildListItem[GuaranteeDomain, GuaranteeType](
          page = GuaranteeTypePage(index),
          formatJourneyDomainModel = x => formatEnumAsString(GuaranteeType.messageKeyPrefix)(x.`type`),
          formatType = formatEnumAsString(GuaranteeType.messageKeyPrefix),
          removeRoute = Some(guaranteeRoutes.RemoveGuaranteeYesNoController.onPageLoad(lrn, index))
        )(GuaranteeDomain.userAnswersReader(index), implicitly)
    }
}
