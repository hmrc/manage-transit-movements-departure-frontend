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
import pages.routeDetails.locationOfGoods._
import pages.routeDetails.locationOfGoods.contact.{NamePage, TelephoneNumberPage}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class LocationOfGoodsCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "LocationOfGoodsCheckYourAnswersHelper" - {

    "locationType" - {
      "must return None" - {
        "when locationTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationType
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when locationTypePage defined" in {
          forAll(arbitrary[Mode], arbitrary[LocationType]) {
            (mode, locationType) =>
              val answers = emptyUserAnswers.setValue(LocationTypePage, locationType)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.locationType

              result mustBe Some(
                SummaryListRow(
                  key = Key("Location type".toText),
                  value = Value(messages(s"${LocationType.messageKeyPrefix}.$locationType").toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.LocationTypeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("location type"),
                          attributes = Map("id" -> "location-type")
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
              val answers = emptyUserAnswers.setValue(IdentificationPage, locationOfGoodsIdentification)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.locationOfGoodsIdentification

              result mustBe Some(
                SummaryListRow(
                  key = Key("Identifier type for the location of goods".toText),
                  value = Value(messages(s"${LocationOfGoodsIdentification.messageKeyPrefix}.$locationOfGoodsIdentification").toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.IdentificationController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("identifier type for the location of goods"),
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

    "customsOfficeIdentifier" - {
      "must return None" - {
        "when customsOfficeIdentifierPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.customsOfficeIdentifier
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when customsOfficeIdentifierPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[CustomsOffice]) {
            (mode, customsOffice) =>
              val answers = emptyUserAnswers.setValue(CustomsOfficeIdentifierPage, customsOffice)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.customsOfficeIdentifier

              result mustBe Some(
                SummaryListRow(
                  key = Key("What is the customs office identifier for the location of goods?".toText),
                  value = Value(customsOffice.toString.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.CustomsOfficeIdentifierController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the customs office identifier for the location of goods"),
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

    "eori" - {
      "must return None" - {
        "when eoriPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.eori
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when eoriPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, eori) =>
              val answers = emptyUserAnswers.setValue(EoriPage, eori)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.eori

              result mustBe Some(
                SummaryListRow(
                  key = Key("What is the EORI number for the location of goods?".toText),
                  value = Value(eori.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.EoriController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the EORI number for the location of goods"),
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

    "authorisationNumber" - {
      "must return None" - {
        "when authorisationNumberPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.authorisationNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when authorisationNumberPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, authorisationNumber) =>
              val answers = emptyUserAnswers.setValue(AuthorisationNumberPage, authorisationNumber)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.authorisationNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("What is the authorisation number for the location of goods?".toText),
                  value = Value(authorisationNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AuthorisationNumberController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the authorisation number for the location of goods"),
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

    "coordinates" - {
      "must return None" - {
        "when coordinatesPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.coordinates
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when coordinatesPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[Coordinates]) {
            (mode, coordinates) =>
              val answers = emptyUserAnswers.setValue(CoordinatesPage, coordinates)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.coordinates

              result mustBe Some(
                SummaryListRow(
                  key = Key("Coordinates".toText),
                  value = Value(coordinates.toString.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.CoordinatesController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("coordinates"),
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

    "unLocode" - {
      "must return None" - {
        "when unLocodePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.unLocode
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when unLocodePage is defined" in {
          forAll(arbitrary[Mode], arbitrary[UnLocode]) {
            (mode, unLocode) =>
              val answers = emptyUserAnswers.setValue(UnLocodePage, unLocode)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.unLocode

              result mustBe Some(
                SummaryListRow(
                  key = Key("UN/LOCODE for the location of goods".toText),
                  value = Value(unLocode.toString.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.UnLocodeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the UN/LOCODE for the location of goods"),
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

    "address" - {
      "must return None" - {
        "when addressPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.address
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when addressPage is defined" in {
          forAll(arbitrary[Mode], arbitrary[Address]) {
            (mode, address) =>
              val answers = emptyUserAnswers.setValue(AddressPage, address)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.address

              result mustBe Some(
                SummaryListRow(
                  key = Key("What is the address for the location of goods?".toText),
                  value = Value(HtmlContent(Seq(address.line1, address.line2, address.postalCode, address.country).mkString("<br>"))),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AddressController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the address for the location of goods"),
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

    "postalCode" - {
      "must return None" - {
        "when postalCodePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.postalCode
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when postalCodePage is defined" in {
          forAll(arbitrary[Mode], arbitrary[PostalCodeAddress]) {
            (mode, postalCode) =>
              val answers = emptyUserAnswers.setValue(PostalCodePage, postalCode)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.postalCode

              result mustBe Some(
                SummaryListRow(
                  key = Key("What is the address for the location of goods?".toText),
                  value = Value(HtmlContent(Seq(postalCode.streetNumber, postalCode.postalCode, postalCode.country).mkString("<br>"))),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.PostalCodeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the address for the location of goods"),
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
        "when addIdentifierYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new LocationOfGoodsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.additionalIdentifierYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when addIdentifierYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddIdentifierYesNoPage, true)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.additionalIdentifierYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add another identifier for the location of goods?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AddIdentifierYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you want to add another identifier for the location of goods"),
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
        "when postalCodePage is defined" in {
          forAll(arbitrary[Mode], arbitrary[String]) {
            (mode, additionalIdentifier) =>
              val answers = emptyUserAnswers.setValue(AdditionalIdentifierPage, additionalIdentifier)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.additionalIdentifier

              result mustBe Some(
                SummaryListRow(
                  key = Key("Additional identifier".toText),
                  value = Value(additionalIdentifier.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AdditionalIdentifierController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("additional identifier"),
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
              val answers = emptyUserAnswers.setValue(AddContactYesNoPage, true)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.contactYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add a contact for the location of goods?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AddContactYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you want to add a contact for the location of goods"),
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
              val answers = emptyUserAnswers.setValue(NamePage, contactName)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.contactName

              result mustBe Some(
                SummaryListRow(
                  key = Key("Who is the contact for the location of goods?".toText),
                  value = Value(contactName.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contact.routes.NameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the contact for the location of goods"),
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
              val answers = emptyUserAnswers.setValue(TelephoneNumberPage, contactPhoneNumber)
              val helper  = new LocationOfGoodsCheckYourAnswersHelper(answers, mode)
              val result  = helper.contactPhoneNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("What is the contact for the location of goods’ telephone number?".toText),
                  value = Value(contactPhoneNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contact.routes.TelephoneNumberController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("the contact for the location of goods’ telephone number"),
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
