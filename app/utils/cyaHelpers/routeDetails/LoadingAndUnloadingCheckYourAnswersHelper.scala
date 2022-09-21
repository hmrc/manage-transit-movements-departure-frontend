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

package utils.cyaHelpers.routeDetails

import models.reference.{Country, UnLocode}
import models.{Mode, UserAnswers}
import pages.routeDetails.loadingAndUnloading.loading._
import pages.routeDetails.loadingAndUnloading.unloading._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class LoadingAndUnloadingCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def addLoadingUnLocode: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = PlaceOfLoadingAddUnLocodeYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.loading.placeOfLoadingAddUnLocodeYesNo",
    id = Some("add-loading-un-locode")
  )

  def loadingUnLocode: Option[SummaryListRow] = getAnswerAndBuildRow[UnLocode](
    page = PlaceOfLoadingUnLocodePage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.loading.placeOfLoadingUnLocode",
    id = Some("loading-un-locode")
  )

  def addLoadingCountryAndLocation: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = PlaceOfLoadingAddExtraInformationYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.loading.placeOfLoadingAddExtraInformationYesNo",
    id = Some("add-loading-country-and-location")
  )

  def loadingCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = PlaceOfLoadingCountryPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.loading.placeOfLoadingCountry",
    id = Some("loading-country")
  )

  def loadingLocation: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = PlaceOfLoadingLocationPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.loading.placeOfLoadingLocation",
    id = Some("loading-location")
  )

  def addPlaceOfUnloading: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddPlaceOfUnloadingPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.unloading.addPlaceOfUnloading",
    id = Some("add-place-of-unloading")
  )

  def addUnloadingUnLocode: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = PlaceOfUnloadingUnLocodeYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.unloading.addExtraInformationYesNo",
    id = Some("add-unloading-un-locode")
  )

  def unloadingUnLocode: Option[SummaryListRow] = getAnswerAndBuildRow[UnLocode](
    page = PlaceOfUnloadingUnLocodePage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.unloading.placeOfUnloadingUnLocode",
    id = Some("unloading-un-locode")
  )

  def addUnloadingCountryAndLocation: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddExtraInformationYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.unloading.addExtraInformationYesNo",
    id = Some("add-unloading-country-and-location")
  )

  def unloadingCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.unloading.country",
    id = Some("unloading-country")
  )
}
