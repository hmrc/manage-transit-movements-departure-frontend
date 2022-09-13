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

import base.SpecBase
import controllers.routeDetails.locationOfGoods.{contact, routes}
import generators.Generators
import models.reference.{CustomsOffice, UnLocode}
import models.{Address, Coordinates, LocationOfGoodsIdentification, LocationType, Mode, PostalCodeAddress}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.locationOfGoods.contact.{LocationOfGoodsContactNamePage, TelephoneNumberPage}
import pages.routeDetails.locationOfGoods.{
  AddContactYesNoPage,
  AdditionalIdentifierPage,
  LocationOfGoodsAddIdentifierYesNoPage,
  LocationOfGoodsAddressPage,
  LocationOfGoodsAuthorisationNumberPage,
  LocationOfGoodsCoordinatesPage,
  LocationOfGoodsCustomsOfficeIdentifierPage,
  LocationOfGoodsEoriPage,
  LocationOfGoodsIdentificationPage,
  LocationOfGoodsPostalCodePage,
  LocationOfGoodsTypePage,
  LocationOfGoodsUnLocodePage
}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class LocationOfGoodsCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "LocationOfGoodsCheckYourAnswersHelper" - {

    "locationOfGoodsType" - {
      "must return None" - {
        "when locationOfGoodsTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationOfGoodsType
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsTypePage defined" in {
          forAll(arbitrary[Mode], arbitrary[LocationType]) {
            (mode, locationType) =>
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsTypePage, locationType)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.locationOfGoodsType

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"${LocationType.messageKeyPrefix}.checkYourAnswersLabel").toText),
                  value = Value(messages(s"${LocationType.messageKeyPrefix}.$locationType").toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationOfGoodsTypeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"${LocationType.messageKeyPrefix}.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-type")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "locationOfGoodsIdentification" - {
      "must return None" - {
        "when locationOfGoodsIdentificationPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationOfGoodsIdentification
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsIdentificationPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[LocationOfGoodsIdentification]) {
            (mode, locationOfGoodsIdentification) =>
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsIdentificationPage, locationOfGoodsIdentification)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.locationOfGoodsIdentification

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"${LocationOfGoodsIdentification.messageKeyPrefix}.checkYourAnswersLabel").toText),
                  value = Value(messages(s"${LocationOfGoodsIdentification.messageKeyPrefix}.$locationOfGoodsIdentification").toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationOfGoodsIdentificationController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"${LocationOfGoodsIdentification.messageKeyPrefix}.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-identification")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "locationOfGoodsCustomsOfficeIdentifier" - {
      "must return None" - {
        "when locationOfGoodsCustomsOfficeIdentifierPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationOfGoodsCustomsOfficeIdentifier
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsCustomsOfficeIdentifierPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[CustomsOffice]) {
            (mode, customsOffice) =>
              val prefix = "routeDetails.locationOfGoods.locationOfGoodsCustomsOfficeIdentifier"
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsCustomsOfficeIdentifierPage, customsOffice)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.locationOfGoodsCustomsOfficeIdentifier

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value(customsOffice.toString.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationOfGoodsCustomsOfficeIdentifierController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-customs-office-identifier")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "locationOfGoodsEori" - {
      "must return None" - {
        "when locationOfGoodsEoriPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationOfGoodsEori
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsEoriPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, eori) =>
              val prefix = "routeDetails.locationOfGoods.locationOfGoodsEori"
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsEoriPage, eori)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.locationOfGoodsEori

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value(eori.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationOfGoodsEoriController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-eori")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "locationOfGoodsAuthorisationNumber" - {
      "must return None" - {
        "when locationOfGoodsAuthorisationNumberPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationOfGoodsAuthorisationNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsAuthorisationNumberPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, locationOfGoodsAuthorisationNumber) =>
              val prefix = "routeDetails.locationOfGoods.locationOfGoodsAuthorisationNumber"
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsAuthorisationNumberPage, locationOfGoodsAuthorisationNumber)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.locationOfGoodsAuthorisationNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value(locationOfGoodsAuthorisationNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationOfGoodsAuthorisationNumberController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-authorisation-number")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "locationOfGoodsCoordinates" - {
      "must return None" - {
        "when locationOfGoodsCoordinatesPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationOfGoodsCoordinates
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsCoordinatesPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[Coordinates]) {
            (mode, coordinates) =>
              val prefix = "routeDetails.locationOfGoods.locationOfGoodsCoordinates"
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsCoordinatesPage, coordinates)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.locationOfGoodsCoordinates

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value(coordinates.toString.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationOfGoodsCoordinatesController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-coordinates")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "locationOfGoodsUnLocode" - {
      "must return None" - {
        "when locationOfGoodsUnLocodePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationOfGoodsUnLocode
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsUnLocodePage is defined" in {
          forAll(arbitrary[Mode], arbitrary[UnLocode]) {
            (mode, unLocode) =>
              val prefix = "routeDetails.locationOfGoods.locationOfGoodsUnLocode"
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsUnLocodePage, unLocode)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.locationOfGoodsUnLocode

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value(unLocode.toString.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationOfGoodsUnLocodeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-un-locode")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "locationOfGoodsAddress" - {
      "must return None" - {
        "when locationOfGoodsAddressPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationOfGoodsAddress
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsAddressPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[Address]) {
            (mode, address) =>
              val prefix = "routeDetails.locationOfGoods.locationOfGoodsAddress"
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsAddressPage, address)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.locationOfGoodsAddress

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value(HtmlContent(Seq(address.line1, address.line2, address.postalCode, address.country).mkString("<br>"))),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationOfGoodsAddressController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-address")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "locationOfGoodsPostalCode" - {
      "must return None" - {
        "when locationOfGoodsPostalCodePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationOfGoodsPostalCode
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsPostalCodePage is defined" in {
          forAll(arbitrary[Mode], arbitrary[PostalCodeAddress]) {
            (mode, postalCode) =>
              val prefix = "routeDetails.locationOfGoods.locationOfGoodsPostalCode"
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsPostalCodePage, postalCode)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.locationOfGoodsPostalCode

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value(HtmlContent(Seq(postalCode.streetNumber, postalCode.postalCode, postalCode.country).mkString("<br>"))),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationOfGoodsPostalCodeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-postal-code")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "additionalIdentifierYesNo" - {
      "must return None" - {
        "when locationOfGoodsAddIdentifierYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.additionalIdentifierYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsAddIdentifierYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val prefix = "routeDetails.locationOfGoods.locationOfGoodsAddIdentifierYesNo"
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsAddIdentifierYesNoPage, true)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.additionalIdentifierYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationOfGoodsAddIdentifierYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-add-identifier")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "additionalIdentifier" - {
      "must return None" - {
        "when additionalIdentifierPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.additionalIdentifier
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationOfGoodsPostalCodePage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, additionalIdentifier) =>
              val prefix = "routeDetails.locationOfGoods.additionalIdentifier"
              val answers = emptyUserAnswers
                .setValue(AdditionalIdentifierPage, additionalIdentifier)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.additionalIdentifier

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value(additionalIdentifier.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AdditionalIdentifierController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-additional-identifier")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "contactYesNo" - {
      "must return None" - {
        "when contactYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when addContactYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val prefix = "routeDetails.locationOfGoods.addContactLocationOfGoods"
              val answers = emptyUserAnswers
                .setValue(AddContactYesNoPage, true)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.contactYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AddContactYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-add-contact")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "contactName" - {
      "must return None" - {
        "when contactNamePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactName
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when contactNamePage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, contactName) =>
              val prefix = "routeDetails.locationOfGoods.contact.locationOfGoodsContactName"
              val answers = emptyUserAnswers
                .setValue(LocationOfGoodsContactNamePage, contactName)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.contactName

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value(contactName.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contact.routes.LocationOfGoodsContactNameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-contact")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "contactPhoneNumber" - {
      "must return None" - {
        "when contactPhoneNumber is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactPhoneNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when contactPhoneNumber is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, contactPhoneNumber) =>
              val prefix = "routeDetails.locationOfGoods.contact.telephoneNumber"
              val answers = emptyUserAnswers
                .setValue(TelephoneNumberPage, contactPhoneNumber)
              val helper = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result = helper.contactPhoneNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key(messages(s"$prefix.checkYourAnswersLabel").toText),
                  value = Value(contactPhoneNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contact.routes.TelephoneNumberController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some(messages(s"$prefix.change.hidden")),
                          attributes = Map("id" -> "location-of-goods-contact-telephone-number")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

  }
}