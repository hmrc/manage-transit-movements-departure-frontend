/*
 * Copyright 2023 HM Revenue & Customs
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

package viewModels.routeDetails.locationOfGoods

import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.routeDetails.locationOfGoods.LocationOfGoodsCheckYourAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class LocationOfGoodsAnswersViewModel(section: Section)

object LocationOfGoodsAnswersViewModel {

  class LocationOfGoodsAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): LocationOfGoodsAnswersViewModel = {
      val helper = new LocationOfGoodsCheckYourAnswersHelper(userAnswers, mode)

      val section = Section(
        sectionTitle = messages("routeDetails.locationOfGoods.checkYourAnswers.subHeading"),
        rows = Seq(
          helper.addLocationOfGoods,
          helper.locationType,
          helper.locationOfGoodsIdentification,
          helper.customsOfficeIdentifier,
          helper.eori,
          helper.authorisationNumber,
          helper.additionalIdentifierYesNo,
          helper.additionalIdentifier,
          helper.coordinates,
          helper.unLocode,
          helper.country,
          helper.address,
          helper.postalCode,
          helper.contactYesNo,
          helper.contactName,
          helper.contactPhoneNumber
        ).flatten
      )

      new LocationOfGoodsAnswersViewModel(section)
    }
  }
}
