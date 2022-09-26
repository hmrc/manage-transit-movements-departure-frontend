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

package utils.cyaHelpers.routeDetails.loadingAndUnloading

import models.reference.{Country, UnLocode}
import models.{Mode, UserAnswers}
import pages.routeDetails.loadingAndUnloading._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class LoadingAndUnloadingCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def addLoadingUnLocode: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = loading.AddUnLocodeYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.loading.addUnLocodeYesNo",
    id = Some("add-loading-un-locode")
  )

  def loadingUnLocode: Option[SummaryListRow] = getAnswerAndBuildRow[UnLocode](
    page = loading.UnLocodePage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.loading.unLocode",
    id = Some("loading-un-locode")
  )

  def addLoadingCountryAndLocation: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = loading.AddExtraInformationYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.loading.addExtraInformationYesNo",
    id = Some("add-loading-country-and-location")
  )

  def loadingCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = loading.CountryPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.loading.country",
    id = Some("loading-country")
  )

  def loadingLocation: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = loading.LocationPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.loading.location",
    id = Some("loading-location")
  )

  def addPlaceOfUnloading: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = unloading.AddPlaceOfUnloadingPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.unloading.addPlaceOfUnloading",
    id = Some("add-place-of-unloading")
  )

  def addUnloadingUnLocode: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = unloading.UnLocodeYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.unloading.addUnLocodeYesNo",
    id = Some("add-unloading-un-locode")
  )

  def unloadingUnLocode: Option[SummaryListRow] = getAnswerAndBuildRow[UnLocode](
    page = unloading.UnLocodePage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.unloading.unLocode",
    id = Some("unloading-un-locode")
  )

  def addUnloadingCountryAndLocation: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = unloading.AddExtraInformationYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.unloading.addExtraInformationYesNo",
    id = Some("add-unloading-country-and-location")
  )

  def unloadingCountry: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = unloading.CountryPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.unloading.country",
    id = Some("unloading-country")
  )

  def unloadingLocation: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = unloading.LocationPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.unloading.location",
    id = Some("unloading-location")
  )
}
