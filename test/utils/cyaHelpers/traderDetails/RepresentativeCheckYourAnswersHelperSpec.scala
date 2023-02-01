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
import controllers.traderDetails.representative.routes
import generators.Generators
import models.Mode
import models.traderDetails.representative.RepresentativeCapacity
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.traderDetails.ActingAsRepresentativePage
import pages.traderDetails.representative._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class RepresentativeCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "RepresentativeCheckYourAnswersHelper" - {

    "actingAsRepresentative" - {
      "must return None" - {
        s"when $ActingAsRepresentativePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RepresentativeCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.actingAsRepresentative
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $ActingAsRepresentativePage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(ActingAsRepresentativePage, true)

              val helper = new RepresentativeCheckYourAnswersHelper(answers, mode)
              val result = helper.actingAsRepresentative

              result mustBe Some(
                SummaryListRow(
                  key = Key("Are you acting as a representative?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.traderDetails.routes.ActingAsRepresentativeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you are acting as a representative"),
                          attributes = Map("id" -> "change-has-acting-representative")
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
              val helper = new RepresentativeCheckYourAnswersHelper(emptyUserAnswers, mode)
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

              val helper = new RepresentativeCheckYourAnswersHelper(answers, mode)
              val result = helper.eori

              result mustBe Some(
                SummaryListRow(
                  key = Key("Representative’s EORI number or Trader Identification Number (TIN)".toText),
                  value = Value(eori.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.EoriController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("representative’s EORI number or Trader Identification Number (TIN)"),
                          attributes = Map("id" -> "change-representative-eori-number")
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

    "addDetails" - {
      "must return None" - {
        s"when $AddDetailsPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RepresentativeCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.addDetails
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $AddDetailsPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddDetailsPage, true)

              val helper = new RepresentativeCheckYourAnswersHelper(answers, mode)
              val result = helper.addDetails

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add your details?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.traderDetails.representative.routes.AddDetailsController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you want to add your details"),
                          attributes = Map("id" -> "change-add-details")
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
              val helper = new RepresentativeCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.name
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $NamePage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (representativeName, mode) =>
              val answers = emptyUserAnswers.setValue(NamePage, representativeName)

              val helper = new RepresentativeCheckYourAnswersHelper(answers, mode)
              val result = helper.name

              result mustBe Some(
                SummaryListRow(
                  key = Key("Representative’s name".toText),
                  value = Value(representativeName.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.NameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("representative’s name"),
                          attributes = Map("id" -> "change-representative-name")
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

    "capacity" - {
      "must return None" - {
        s"when $CapacityPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RepresentativeCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.capacity
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $CapacityPage is defined" in {
          forAll(Gen.oneOf(RepresentativeCapacity.values), arbitrary[Mode]) {
            (capacity, mode) =>
              val answers = emptyUserAnswers.setValue(CapacityPage, capacity)

              val helper = new RepresentativeCheckYourAnswersHelper(answers, mode)
              val result = helper.capacity

              result mustBe Some(
                SummaryListRow(
                  key = Key("Representative capacity".toText),
                  value = Value(messages(s"traderDetails.representative.capacity.$capacity").toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.CapacityController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("representative capacity"),
                          attributes = Map("id" -> "change-representative-capacity")
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

    "phoneNumber" - {
      "must return None" - {
        s"when $TelephoneNumberPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RepresentativeCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.phoneNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        s"when $TelephoneNumberPage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (representativePhoneNumber, mode) =>
              val answers = emptyUserAnswers.setValue(TelephoneNumberPage, representativePhoneNumber)

              val helper = new RepresentativeCheckYourAnswersHelper(answers, mode)
              val result = helper.phoneNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("Representative’s phone number".toText),
                  value = Value(representativePhoneNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.TelephoneNumberController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("representative’s phone number"),
                          attributes = Map("id" -> "change-representative-phone-number")
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
