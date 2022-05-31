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

import base.SpecBase
import controllers.traderDetails.representative.routes
import generators.Generators
import models.Mode
import models.traderDetails.representative.RepresentativeCapacity
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.traderDetails.representative._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class RepresentativeCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "RepresentativeCheckYourAnswersHelper" - {

    "actingRepresentative" - {
      "must return None" - {
        "when ActingRepresentativePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RepresentativeCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.actingRepresentative
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ActingRepresentativePage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(ActingRepresentativePage, true)

              val helper = new RepresentativeCheckYourAnswersHelper(answers, mode)
              val result = helper.actingRepresentative

              result mustBe Some(
                SummaryListRow(
                  key = Key("Are you acting as a representative?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.ActingRepresentativeController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you are acting as a representative"),
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

    "representativeEori" - {
      "must return None" - {
        "when RepresentativeEoriPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RepresentativeCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.representativeEori
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when RepresentativeEoriPage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (eori, mode) =>
              val answers = emptyUserAnswers.setValue(RepresentativeEoriPage, eori)

              val helper = new RepresentativeCheckYourAnswersHelper(answers, mode)
              val result = helper.representativeEori

              result mustBe Some(
                SummaryListRow(
                  key = Key("Representative EORI number".toText),
                  value = Value(eori.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.RepresentativeEoriController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("representative’s EORI number"),
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

    "representativeName" - {
      "must return None" - {
        "when RepresentativeNamePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RepresentativeCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.representativeName
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when RepresentativeNamePage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (representativeName, mode) =>
              val answers = emptyUserAnswers.setValue(RepresentativeNamePage, representativeName)

              val helper = new RepresentativeCheckYourAnswersHelper(answers, mode)
              val result = helper.representativeName

              result mustBe Some(
                SummaryListRow(
                  key = Key("Representative’s name".toText),
                  value = Value(representativeName.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.RepresentativeNameController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("representative’s name"),
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

    "representativeCapacity" - {
      "must return None" - {
        "when RepresentativeCapacityPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RepresentativeCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.representativeCapacity
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when RepresentativeCapacityPage is defined" in {
          forAll(Gen.oneOf(RepresentativeCapacity.values), arbitrary[Mode]) {
            (capacity, mode) =>
              val answers = emptyUserAnswers.setValue(RepresentativeCapacityPage, capacity)

              val helper = new RepresentativeCheckYourAnswersHelper(answers, mode)
              val result = helper.representativeCapacity

              result mustBe Some(
                SummaryListRow(
                  key = Key("Representative capacity".toText),
                  value = Value(messages(s"traderDetails.representative.representativeCapacity.$capacity").toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.RepresentativeCapacityController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("representative capacity"),
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

    "RepresentativePhoneNumber" - {
      "must return None" - {
        "when RepresentativePhoneNumberPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RepresentativeCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.representativePhone
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when RepresentativePhoneNumberPage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (representativePhoneNumber, mode) =>
              val answers = emptyUserAnswers.setValue(RepresentativePhonePage, representativePhoneNumber)

              val helper = new RepresentativeCheckYourAnswersHelper(answers, mode)
              val result = helper.representativePhone

              result mustBe Some(
                SummaryListRow(
                  key = Key("Representative’s phone number".toText),
                  value = Value(representativePhoneNumber.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.RepresentativePhoneController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("representative’s phone number"),
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
