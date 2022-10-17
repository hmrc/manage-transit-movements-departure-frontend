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

package utils.cyaHelpers.routeDetails.locationOfGoods

import models.reference.{Country, CustomsOffice, UnLocode}
import models.{Coordinates, DynamicAddress, LocationOfGoodsIdentification, LocationType, Mode, PostalCodeAddress, UserAnswers}
import pages.routeDetails.locationOfGoods._
import pages.routeDetails.locationOfGoods.contact.{NamePage, TelephoneNumberPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import utils.cyaHelpers.AnswersHelper

class LocationOfGoodsCheckYourAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def locationType: Option[SummaryListRow] = getAnswerAndBuildRow[LocationType](
    page = LocationTypePage,
    formatAnswer = formatEnumAsText(LocationType.messageKeyPrefix),
    prefix = "routeDetails.locationOfGoods.locationType",
    id = Some("location-type")
  )

  def locationOfGoodsIdentification: Option[SummaryListRow] = getAnswerAndBuildRow[LocationOfGoodsIdentification](
    page = IdentificationPage,
    formatAnswer = formatEnumAsText(LocationOfGoodsIdentification.messageKeyPrefix),
    prefix = "routeDetails.locationOfGoods.identification",
    id = Some("location-of-goods-identification")
  )

  def customsOfficeIdentifier: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = CustomsOfficeIdentifierPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.customsOfficeIdentifier",
    id = Some("location-of-goods-customs-office-identifier")
  )

  def eori: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = EoriPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.eori",
    id = Some("location-of-goods-eori")
  )

  def authorisationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = AuthorisationNumberPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.authorisationNumber",
    id = Some("location-of-goods-authorisation-number")
  )

  def coordinates: Option[SummaryListRow] = getAnswerAndBuildRow[Coordinates](
    page = CoordinatesPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.coordinates",
    id = Some("location-of-goods-coordinates")
  )

  def unLocode: Option[SummaryListRow] = getAnswerAndBuildRow[UnLocode](
    page = UnLocodePage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.unLocode",
    id = Some("location-of-goods-un-locode")
  )

  def country: Option[SummaryListRow] = getAnswerAndBuildRow[Country](
    page = CountryPage,
    formatAnswer = formatAsCountry,
    prefix = "routeDetails.locationOfGoods.country",
    id = Some("transit-holder-country")
  )

  def address: Option[SummaryListRow] = getAnswerAndBuildRow[DynamicAddress](
    page = AddressPage,
    formatAnswer = formatAsDynamicAddress,
    prefix = "routeDetails.locationOfGoods.address",
    id = Some("location-of-goods-address")
  )

  def postalCode: Option[SummaryListRow] = getAnswerAndBuildRow[PostalCodeAddress](
    page = PostalCodePage,
    formatAnswer = formatAsPostalCodeAddress,
    prefix = "routeDetails.locationOfGoods.postalCode",
    id = Some("location-of-goods-postal-code")
  )

  def additionalIdentifierYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddIdentifierYesNoPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "routeDetails.locationOfGoods.addIdentifierYesNo",
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
    page = NamePage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.contact.name",
    id = Some("location-of-goods-contact")
  )

  def contactPhoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = TelephoneNumberPage,
    formatAnswer = formatAsText,
    prefix = "routeDetails.locationOfGoods.contact.telephoneNumber",
    id = Some("location-of-goods-contact-telephone-number")
  )
}
