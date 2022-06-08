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

import models.UserAnswers
import play.api.i18n.Messages
import utils.cyaHelpers.traderDetails.HolderOfTransitCheckYourAnswersHelper
import viewModels.sections.Section
import viewModels.{SectionViewModel, SubSectionViewModel}

sealed trait HolderOfTransitViewModel {
  self: SectionViewModel =>

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): Seq[Section] = {
    val helper = new HolderOfTransitCheckYourAnswersHelper(userAnswers, mode)

    val holderOfTransitSection = Section(
      sectionTitle = messages("traderDetails.holderOfTransit.checkYourAnswers.transitHolder"),
      rows = Seq(
        helper.eoriYesNo,
        helper.eori,
        helper.tirIdentificationYesNo,
        helper.tirIdentification,
        helper.name,
        helper.address
      ).flatten
    )

    val additionalContactSection = Section(
      sectionTitle = messages("traderDetails.holderOfTransit.checkYourAnswers.additionalContact"),
      rows = Seq(
        helper.addContact,
        helper.contactName,
        helper.contactTelephoneNumber
      ).flatten
    )

    Seq(holderOfTransitSection, additionalContactSection)
  }
}

object HolderOfTransitViewModel {
  class HolderOfTransitSectionViewModel extends SectionViewModel with HolderOfTransitViewModel
  class HolderOfTransitSubSectionViewModel extends SubSectionViewModel with HolderOfTransitViewModel
}
