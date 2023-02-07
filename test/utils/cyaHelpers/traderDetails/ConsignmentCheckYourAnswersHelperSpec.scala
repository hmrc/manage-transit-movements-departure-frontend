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

package utils.cyaHelpers.traderDetails

import base.SpecBase
import controllers.traderDetails.consignment.consignee.{routes => consigneeRoutes}
import controllers.traderDetails.consignment.consignor.contact.{routes => contactRoutes}
import controllers.traderDetails.consignment.consignor.{routes => consignorRoutes}
import controllers.traderDetails.consignment.{routes => consignmentRoutes}
import generators.Generators
import models.reference.Country
import models.{DynamicAddress, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.traderDetails.consignment._
import pages.traderDetails.consignment.consignor.CountryPage
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class ConsignmentCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "ConsignmentCheckYourAnswersHelper" - {

    "approvedOperator" - {
      "must return None" - {
        s"when $ApprovedOperatorPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.approvedOperator
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $ApprovedOperatorPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(ApprovedOperatorPage, true)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.approvedOperator

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to use a reduced data set?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignmentRoutes.ApprovedOperatorController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you want to use a reduced data set"),
                          attributes = Map("id" -> "change-has-reduced-data-set")
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

    "consignorEoriYesNo" - {
      "must return None" - {
        s"when ${consignor.EoriYesNoPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consignorEoriYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignor.EoriYesNoPage} is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(consignor.EoriYesNoPage, true)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorEoriYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you know the consignor’s EORI number or Trader Identification Number (TIN)?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignorRoutes.EoriYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you know the consignor’s EORI number or Trader Identification Number (TIN)"),
                          attributes = Map("id" -> "change-has-consignor-eori")
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

    "consignorEori" - {
      "must return None" - {
        s"when ${consignor.EoriPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consignorEori
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignor.EoriPage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (eori, mode) =>
              val answers = emptyUserAnswers.setValue(consignor.EoriPage, eori)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorEori

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignor’s EORI number or Trader Identification Number (TIN)".toText),
                  value = Value(eori.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignorRoutes.EoriController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignor’s EORI number or Trader Identification Number (TIN)"),
                          attributes = Map("id" -> "change-consignor-eori-number")
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

    "consignorName" - {
      "must return None" - {
        s"when ${consignor.NamePage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consignorName
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignor.NamePage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (name, mode) =>
              val answers = emptyUserAnswers.setValue(consignor.NamePage, name)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorName

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignor’s name".toText),
                  value = Value(name.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignorRoutes.NameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignor’s name"),
                          attributes = Map("id" -> "change-consignor-name")
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

    "country" - {
      "must return None" - {
        s"when $CountryPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consignorCountry
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $CountryPage is defined" in {
          forAll(arbitrary[Country], arbitrary[Mode]) {
            (country, mode) =>
              val answers = emptyUserAnswers.setValue(CountryPage, country)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorCountry

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignor’s country".toText),
                  value = Value(country.description.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignorRoutes.CountryController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignor’s country"),
                          attributes = Map("id" -> "change-consignor-country")
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

    "consignorAddress" - {
      "must return None" - {
        s"when ${consignor.AddressPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consignorAddress
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignor.AddressPage} is defined" in {
          forAll(arbitrary[DynamicAddress], arbitrary[Mode]) {
            (address, mode) =>
              val answers = emptyUserAnswers.setValue(consignor.AddressPage, address)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorAddress

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignor’s address".toText),
                  value = Value(HtmlContent(Seq(Some(address.numberAndStreet), Some(address.city), address.postalCode).flatten.mkString("<br>"))),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignorRoutes.AddressController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignor’s address"),
                          attributes = Map("id" -> "change-consignor-address")
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

    "addConsignorContact" - {
      "must return None" - {
        s"when ${consignor.AddContactPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.addConsignorContact
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignor.AddContactPage} is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(consignor.AddContactPage, true)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.addConsignorContact

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add a contact for the consignor?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignorRoutes.AddContactController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you want to add a contact for the consignor"),
                          attributes = Map("id" -> "change-has-consignor-contact")
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

    "consignor contactName" - {
      "must return None" - {
        s"when ${consignor.contact.NamePage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consignorContactName
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignor.contact.NamePage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (contactName, mode) =>
              val answers = emptyUserAnswers.setValue(consignor.contact.NamePage, contactName)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorContactName

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignor contact’s name".toText),
                  value = Value(contactName.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contactRoutes.NameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignor contact’s name"),
                          attributes = Map("id" -> "change-consignor-contact-name")
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

    "consignor contactTelephoneNumber" - {
      "must return None" - {
        s"when ${consignor.contact.TelephoneNumberPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consignorContactTelephoneNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignor.contact.TelephoneNumberPage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (contactTelephoneNumber, mode) =>
              val answers = emptyUserAnswers.setValue(consignor.contact.TelephoneNumberPage, contactTelephoneNumber)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorContactTelephoneNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignor contact’s phone number".toText),
                  value = Value(contactTelephoneNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contactRoutes.TelephoneNumberController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignor contact’s phone number"),
                          attributes = Map("id" -> "change-consignor-contact-phone-number")
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

    "moreThanOneConsignee" - {
      "must return None" - {
        s"when $MoreThanOneConsigneePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.moreThanOneConsignee
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $MoreThanOneConsigneePage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(MoreThanOneConsigneePage, true)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.moreThanOneConsignee

              result mustBe Some(
                SummaryListRow(
                  key = Key("Is there more than one consignee?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignmentRoutes.MoreThanOneConsigneeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if there is more than one consignee"),
                          attributes = Map("id" -> "change-has-more-than-one-consignee")
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

    "consigneeEoriYesNo" - {
      "must return None" - {
        s"when ${consignee.EoriYesNoPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consigneeEoriYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignee.EoriYesNoPage} is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(consignee.EoriYesNoPage, true)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consigneeEoriYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you know the consignee’s EORI number or Trader Identification Number (TIN)?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consigneeRoutes.EoriYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you know the consignee’s EORI number or Trader Identification Number (TIN)"),
                          attributes = Map("id" -> "change-has-consignee-eori")
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

    "consigneeEori" - {
      "must return None" - {
        s"when ${consignee.EoriNumberPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consigneeEori
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignee.EoriNumberPage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (eori, mode) =>
              val answers = emptyUserAnswers.setValue(consignee.EoriNumberPage, eori)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consigneeEori

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignee’s EORI number or Trader Identification Number (TIN)".toText),
                  value = Value(eori.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consigneeRoutes.EoriNumberController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignee’s EORI number or Trader Identification Number (TIN)"),
                          attributes = Map("id" -> "change-consignee-eori-number")
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

    "consigneeName" - {
      "must return None" - {
        s"when ${consignee.NamePage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consigneeName
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignee.NamePage} is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (name, mode) =>
              val answers = emptyUserAnswers.setValue(consignee.NamePage, name)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consigneeName

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignee’s name".toText),
                  value = Value(name.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consigneeRoutes.NameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignee’s name"),
                          attributes = Map("id" -> "change-consignee-name")
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

    "consigneeCountry" - {
      "must return None" - {
        s"when ${consignee.CountryPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consigneeCountry
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignee.CountryPage} is defined" in {
          forAll(arbitrary[Country], arbitrary[Mode]) {
            (country, mode) =>
              val answers = emptyUserAnswers.setValue(consignee.CountryPage, country)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consigneeCountry

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignee’s country".toText),
                  value = Value(country.description.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consigneeRoutes.CountryController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignee’s country"),
                          attributes = Map("id" -> "change-consignee-country")
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

    "consigneeAddress" - {
      "must return None" - {
        s"when ${consignee.AddressPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consigneeAddress
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignee.AddressPage} is defined" in {
          forAll(arbitrary[DynamicAddress], arbitrary[Mode]) {
            (address, mode) =>
              val answers = emptyUserAnswers.setValue(consignee.AddressPage, address)

              val helper = new ConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consigneeAddress

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignee’s address".toText),
                  value = Value(HtmlContent(Seq(Some(address.numberAndStreet), Some(address.city), address.postalCode).flatten.mkString("<br>"))),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consigneeRoutes.AddressController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignee’s address"),
                          attributes = Map("id" -> "change-consignee-address")
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
