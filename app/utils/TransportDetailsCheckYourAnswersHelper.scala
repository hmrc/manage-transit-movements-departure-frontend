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

package utils

import controllers.transportDetails.routes
import models.journeyDomain.TransportDetails.InlandMode.{Mode5or7, Rail}
import models.reference.CountryCode
import models.{CountryList, Mode, TransportModeList, UserAnswers}
import pages._
import play.api.mvc.Call
import uk.gov.hmrc.viewmodels.SummaryList.Row
import uk.gov.hmrc.viewmodels._

class TransportDetailsCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode) extends CheckYourAnswersHelper(userAnswers) {

  def modeAtBorder(transportModeList: TransportModeList): Option[Row] = getAnswerAndBuildModeRow(
    page = ModeAtBorderPage,
    transportModeList = transportModeList,
    prefix = "modeAtBorder",
    id = "change-mode-at-border",
    call = routes.ModeAtBorderController.onPageLoad(lrn, mode)
  )

  def modeCrossingBorder(transportModeList: TransportModeList): Option[Row] = getAnswerAndBuildModeRow(
    page = ModeCrossingBorderPage,
    transportModeList = transportModeList,
    prefix = "modeCrossingBorder",
    id = "change-mode-crossing-border",
    call = routes.ModeCrossingBorderController.onPageLoad(lrn, mode)
  )

  def inlandMode(transportModeList: TransportModeList): Option[Row] = getAnswerAndBuildModeRow(
    page = InlandModePage,
    transportModeList = transportModeList,
    prefix = "inlandMode",
    id = "change-inland-mode",
    call = routes.InlandModeController.onPageLoad(lrn, mode)
  )

  def idCrossingBorder: Option[Row] = getAnswerAndBuildRow[String](
    page = IdCrossingBorderPage,
    formatAnswer = formatAsLiteral,
    prefix = "idCrossingBorder",
    id = Some("change-id-crossing-border"),
    call = routes.IdCrossingBorderController.onPageLoad(lrn, mode)
  )

  def nationalityAtDeparture(countryList: CountryList, inlandModeCode: String): Option[Row] =
    if (inlandModeCode.isMode5Or7Code || inlandModeCode.isRailCode) {
      None
    } else {
      getAnswerAndBuildSimpleCountryRow[CountryCode](
        page = NationalityAtDeparturePage,
        getCountryCode = countryCode => countryCode,
        countryList = countryList,
        prefix = "nationalityAtDeparture",
        id = Some("change-nationality-at-departure"),
        call = routes.NationalityAtDepartureController.onPageLoad(lrn, mode)
      )
    }

  def nationalityCrossingBorder(countryList: CountryList): Option[Row] = getAnswerAndBuildSimpleCountryRow[CountryCode](
    page = NationalityCrossingBorderPage,
    getCountryCode = countryCode => countryCode,
    countryList = countryList,
    prefix = "nationalityCrossingBorder",
    id = Some("change-nationality-crossing-border"),
    call = routes.NationalityCrossingBorderController.onPageLoad(lrn, mode)
  )

  def idAtDeparture(inlandModeCode: String): Option[Row] =
    if (inlandModeCode.isMode5Or7Code) {
      None
    } else {
      getAnswerAndBuildRow[String](
        page = IdAtDeparturePage,
        formatAnswer = formatAsLiteral,
        prefix = "idAtDeparture",
        id = None,
        call = routes.IdAtDepartureController.onPageLoad(lrn, mode)
      )
    }

  def changeAtBorder: Option[Row] = getAnswerAndBuildRow[Boolean](
    page = ChangeAtBorderPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "changeAtBorder",
    id = Some("change-change-at-border"),
    call = routes.ChangeAtBorderController.onPageLoad(lrn, mode)
  )

  def addIdAtDeparture(inlandModeCode: String): Option[Row] =
    if (inlandModeCode.isMode5Or7Code) {
      None
    } else {
      getAnswerAndBuildRow[Boolean](
        page = AddIdAtDeparturePage,
        formatAnswer = formatAsYesOrNo,
        prefix = "addIdAtDeparture",
        id = Some("change-add-id-at-departure"),
        call = routes.AddIdAtDepartureController.onPageLoad(lrn, mode)
      )
    }

  def addNationalityAtDeparture(inlandModeCode: String): Option[Row] =
    if (inlandModeCode.isMode5Or7Code || inlandModeCode.isRailCode) {
      None
    } else {
      getAnswerAndBuildRow[Boolean](
        page = AddNationalityAtDeparturePage,
        formatAnswer = formatAsYesOrNo,
        prefix = "addNationalityAtDeparture",
        id = Some("change-add-nationality-at-departure"),
        call = routes.AddNationalityAtDepartureController.onPageLoad(lrn, mode)
      )
    }

  private def getAnswerAndBuildModeRow(
    page: QuestionPage[String],
    transportModeList: TransportModeList,
    prefix: String,
    id: String,
    call: Call
  ): Option[Row] = {
    val format: String => Content = modeCode =>
      lit"${transportModeList
        .getTransportMode(modeCode)
        .fold(modeCode)(_.toString)}"

    getAnswerAndBuildRow[String](page, format, prefix, Some(id), call)
  }

  implicit private class ModeCode(modeCode: String) {
    def isMode5Or7Code: Boolean = Mode5or7.Constants.codes.map(_.toString).contains(modeCode)
    def isRailCode: Boolean     = Rail.Constants.codes.map(_.toString).contains(modeCode)
  }

}
