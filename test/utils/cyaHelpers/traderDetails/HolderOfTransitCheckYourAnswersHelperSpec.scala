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

package utils.cyaHelpers.traderDetails

import base.SpecBase
import controllers.traderDetails.holderOfTransit.contact.{routes => contactRoutes}
import controllers.traderDetails.holderOfTransit.{routes => hotRoutes}
import generators.Generators
import models.{Address, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.traderDetails.holderOfTransit._
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class HolderOfTransitCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "HolderOfTransitCheckYourAnswersHelper" - {

    "tirIdentificationYesNo" - {
      "must return None" - {
        s"when $TirIdentificationYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.tirIdentificationYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $TirIdentificationYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(TirIdentificationYesNoPage, true)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.tirIdentificationYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Is the TIR holder’s identification number known?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.TirIdentificationYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you know the Transit procedure TIR identification number"),
                          attributes = Map("id" -> "has-transit-holder-tir-id")
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

    "tirIdentification" - {
      "must return None" - {
        s"when $TirIdentificationPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.tirIdentification
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $TirIdentificationPage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (eori, mode) =>
              val answers = emptyUserAnswers.setValue(TirIdentificationPage, eori)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.tirIdentification

              result mustBe Some(
                SummaryListRow(
                  key = Key("TIR holder’s identification number".toText),
                  value = Value(eori.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.TirIdentificationController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("TIR holder’s identification number"),
                          attributes = Map("id" -> "transit-holder-tir-id-number")
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

    "eoriYesNo" - {
      "must return None" - {
        s"when $EoriYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.eoriYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $EoriYesNoPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(EoriYesNoPage, true)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.eoriYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you know the transit holder’s EORI number or TIN?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.EoriYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you know the transit holder’s EORI number or TIN"),
                          attributes = Map("id" -> "has-transit-holder-eori")
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
        s"when $EoriPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.eori
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $EoriPage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (eori, mode) =>
              val answers = emptyUserAnswers.setValue(EoriPage, eori)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.eori

              result mustBe Some(
                SummaryListRow(
                  key = Key("Transit holder’s EORI number or TIN".toText),
                  value = Value(eori.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.EoriController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("transit holder’s EORI number or TIN"),
                          attributes = Map("id" -> "transit-holder-eori-number")
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

    "name" - {
      "must return None" - {
        s"when $NamePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.name
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $NamePage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (name, mode) =>
              val answers = emptyUserAnswers.setValue(NamePage, name)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.name

              result mustBe Some(
                SummaryListRow(
                  key = Key("Transit holder’s name".toText),
                  value = Value(name.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.NameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("transit holder’s name"),
                          attributes = Map("id" -> "transit-holder-name")
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
        s"when $AddressPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.address
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $AddressPage is defined" in {
          forAll(arbitrary[Address], arbitrary[Mode]) {
            (address, mode) =>
              val answers = emptyUserAnswers.setValue(AddressPage, address)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.address

              result mustBe Some(
                SummaryListRow(
                  key = Key("Transit holder’s address".toText),
                  value = Value(HtmlContent(Seq(address.line1, address.line2, address.postalCode, address.country).mkString("<br>"))),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.AddressController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("transit holder’s address"),
                          attributes = Map("id" -> "transit-holder-address")
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

    "addContact" - {
      "must return None" - {
        s"when $AddContactPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.addContact
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $AddContactPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddContactPage, true)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.addContact

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add a contact?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = hotRoutes.AddContactController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you want to add a contact"),
                          attributes = Map("id" -> "has-transit-holder-contact")
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
        s"when ${contact.NamePage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactName
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${contact.NamePage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (contactName, mode) =>
              val answers = emptyUserAnswers.setValue(contact.NamePage, contactName)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.contactName

              result mustBe Some(
                SummaryListRow(
                  key = Key("Contact’s name".toText),
                  value = Value(contactName.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contactRoutes.NameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("contact’s name"),
                          attributes = Map("id" -> "transit-holder-contact-name")
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

    "contactTelephoneNumber" - {
      "must return None" - {
        s"when ${contact.TelephoneNumberPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new HolderOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactTelephoneNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${contact.TelephoneNumberPage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (contactTelephoneNumber, mode) =>
              val answers = emptyUserAnswers.setValue(contact.TelephoneNumberPage, contactTelephoneNumber)

              val helper = new HolderOfTransitCheckYourAnswersHelper(answers, mode)
              val result = helper.contactTelephoneNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("Transit holder’s contact phone number".toText),
                  value = Value(contactTelephoneNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contactRoutes.TelephoneNumberController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("contact’s phone number"),
                          attributes = Map("id" -> "transit-holder-contact-phone-number")
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
