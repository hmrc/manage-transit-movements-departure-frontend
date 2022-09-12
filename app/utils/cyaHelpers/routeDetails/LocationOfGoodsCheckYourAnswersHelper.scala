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

import models.reference.{CustomsOffice, UnLocode}
import models.{Address, Coordinates, LocationOfGoodsIdentification, LocationType, Mode, PostalCodeAddress, UserAnswers}
import pages.routeDetails.locationOfGoods._
import pages.routeDetails.locationOfGoods.contact.{LocationOfGoodsContactNamePage, TelephoneNumberPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class LocationOfGoodsCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def locationOfGoodsType: Option[SummaryListRow] = getAnswerAndBuildRow[LocationType](
    page = LocationOfGoodsTypePage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.locationOfGoodsType",
    id = Some("location-of-goods-type")
  )

  def locationOfGoodsIdentification: Option[SummaryListRow] = getAnswerAndBuildRow[LocationOfGoodsIdentification](
    page = LocationOfGoodsIdentificationPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.locationOfGoodsIdentification",
    id = Some("location-of-goods-identification")
  )

  def locationOfGoodsCustomsOfficeIdentifier: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = LocationOfGoodsCustomsOfficeIdentifierPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.locationOfGoodsCustomsOfficeIdentifier",
    id = Some("location-of-goods-customs-office-identifier")
  )

  def locationOfGoodsEori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = LocationOfGoodsEoriPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.locationOfGoodsEori",
    id = Some("location-of-goods-eori")
  )

  def locationOfGoodsAuthorisationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = LocationOfGoodsAuthorisationNumberPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.locationOfGoodsAuthorisationNumber",
    id = Some("location-of-goods-authorisation-number")
  )

  def locationOfGoodsCoordinates: Option[SummaryListRow] = getAnswerAndBuildRow[Coordinates](
    page = LocationOfGoodsCoordinatesPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.locationOfGoodsCoordinates",
    id = Some("location-of-goods-coordinates")
  )

  def locationOfGoodsUnLocode: Option[SummaryListRow] = getAnswerAndBuildRow[UnLocode](
    page = LocationOfGoodsUnLocodePage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.locationOfGoodsUnLocode",
    id = Some("location-of-goods-un-locode")
  )

  def locationOfGoodsAddress: Option[SummaryListRow] = getAnswerAndBuildRow[Address](
    page = LocationOfGoodsAddressPage,
    formatAnswer = formatAsAddress,
    prefix = "routeDetails.locationOfGoods.locationOfGoodsAddress",
    id = Some("location-of-goods-address")
  )

  def locationOfGoodsPostalCode: Option[SummaryListRow] = getAnswerAndBuildRow[PostalCodeAddress](
    page = LocationOfGoodsPostalCodePage,
    formatAnswer = formatAsPostalCodeAddress,
    prefix = "routeDetails.locationOfGoods.locationOfGoodsPostalCode",
    id = Some("location-of-goods-postal-code")
  )

  def additionalIdentifierYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = LocationOfGoodsAddIdentifierYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.locationOfGoods.locationOfGoodsAddIdentifierYesNo",
    id = Some("location-of-goods-add-identifier")
  )

  def additionalIdentifier: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = AdditionalIdentifierPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.additionalIdentifier",
    id = Some("location-of-goods-additional-identifier")
  )

  def contactYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddContactYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.locationOfGoods.addContactLocationOfGoods",
    id = Some("location-of-goods-add-contact")
  )

  def contactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = LocationOfGoodsContactNamePage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.contact.locationOfGoodsContactName",
    id = Some("location-of-goods-contact")
  )

  def contactPhoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = TelephoneNumberPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.contact.telephoneNumber",
    id = Some("location-of-goods-contact-telephone-number")
  )
}
