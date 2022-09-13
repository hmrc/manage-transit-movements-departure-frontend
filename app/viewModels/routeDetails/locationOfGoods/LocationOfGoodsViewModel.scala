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

package viewModels.routeDetails.locationOfGoods

import models.{NormalMode, UserAnswers}
import play.api.i18n.Messages
import utils.cyaHelpers.routeDetails.LocationOfGoodsCheckYourAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class LocationOfGoodsViewModel(section: Section)

object LocationOfGoodsViewModel {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): LocationOfGoodsViewModel =
    new LocationOfGoodsViewModelProvider()(userAnswers)

  class LocationOfGoodsViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers)(implicit messages: Messages): LocationOfGoodsViewModel = {
      val helper = new LocationOfGoodsCheckYourAnswersHelper(userAnswers, NormalMode)

      val rows = Seq(
        helper.locationOfGoodsEori,
        helper.locationOfGoodsType,
        helper.locationOfGoodsCoordinates,
        helper.locationOfGoodsAuthorisationNumber,
        helper.locationOfGoodsUnLocode,
        helper.additionalIdentifierYesNo,
        helper.additionalIdentifier,
        helper.locationOfGoodsIdentification,
        helper.locationOfGoodsCustomsOfficeIdentifier,
        helper.locationOfGoodsAddress,
        helper.locationOfGoodsPostalCode,
        helper.contactYesNo,
        helper.contactName,
        helper.contactPhoneNumber
      ).flatten

      new LocationOfGoodsViewModel(Section(rows))
    }
  }
}
