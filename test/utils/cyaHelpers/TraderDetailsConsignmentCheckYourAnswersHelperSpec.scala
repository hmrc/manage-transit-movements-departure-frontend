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

package utils.cyaHelpers

import base.SpecBase
import controllers.traderDetails.consignment.consignor.contact.{routes => contactRoutes}
import controllers.traderDetails.consignment.consignor.{routes => consignorRoutes}
import controllers.traderDetails.consignment.consignee.{routes => consigneeRoutes}
import generators.Generators
import models.{Address, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.traderDetails.consignment._
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import utils.cyaHelpers.traderDetails.TraderDetailsConsignmentCheckYourAnswersHelper

class TraderDetailsConsignmentCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "TraderDetailsConsignmentCheckYourAnswersHelper" - {

    "consignorEoriYesNo" - {
      "must return None" - {
        s"when ${consignor.EoriYesNoPage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
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

              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorEoriYesNo

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you know the consignor’s EORI number?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignorRoutes.EoriYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you know the consignor’s EORI number"),
                          attributes = Map()
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
              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
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

              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorEori

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignor’s EORI number".toText),
                  value = Value(eori.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignorRoutes.EoriController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignor’s EORI number"),
                          attributes = Map()
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
              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
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

              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(answers, mode)
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
                          attributes = Map()
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
              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.consignorAddress
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignor.AddressPage} is defined" in {
          forAll(arbitrary[Address], arbitrary[Mode]) {
            (address, mode) =>
              val answers = emptyUserAnswers.setValue(consignor.AddressPage, address)

              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorAddress

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignor’s address".toText),
                  value = Value(HtmlContent(Seq(address.line1, address.line2, address.postalCode, address.country).mkString("<br>"))),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = consignorRoutes.AddressController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignor’s address"),
                          attributes = Map()
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
              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
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

              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(answers, mode)
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
                          attributes = Map()
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
              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
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

              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorContactName

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignor’s contact name".toText),
                  value = Value(contactName.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contactRoutes.NameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignor’s contact name"),
                          attributes = Map()
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
              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
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

              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(answers, mode)
              val result = helper.consignorContactTelephoneNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("Consignor’s contact phone number".toText),
                  value = Value(contactTelephoneNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = contactRoutes.TelephoneNumberController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("consignor’s contact phone number"),
                          attributes = Map()
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
        s"when ${consignee.MoreThanOneConsigneePage} is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.moreThanOneConsignee
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when ${consignee.MoreThanOneConsigneePage} is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(consignee.MoreThanOneConsigneePage, true)

              val helper = new TraderDetailsConsignmentCheckYourAnswersHelper(answers, mode)
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
                          href = consigneeRoutes.MoreThanOneConsigneeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if there is more than one consignee"),
                          attributes = Map()
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
