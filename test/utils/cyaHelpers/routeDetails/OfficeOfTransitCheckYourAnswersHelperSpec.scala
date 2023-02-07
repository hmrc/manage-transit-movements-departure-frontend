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

package utils.cyaHelpers.routeDetails

import base.SpecBase
import controllers.routeDetails.transit.index.routes
import generators.Generators
import models.reference.{Country, CustomsOffice}
import models.{DateTime, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.transit.index.{AddOfficeOfTransitETAYesNoPage, OfficeOfTransitCountryPage, OfficeOfTransitETAPage, OfficeOfTransitPage}
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import utils.cyaHelpers.routeDetails.transit.OfficeOfTransitCheckYourAnswersHelper

import java.time.LocalDateTime

class OfficeOfTransitCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "OfficeOfTransitCheckYourAnswersHelper" - {

    "officeOfTransitCountry" - {
      "must return None" - {
        "when OfficeOfTransitCountryPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new OfficeOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.officeOfTransitCountry
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when OfficeOfTransitCountryPage defined" in {
          forAll(arbitrary[Mode], arbitrary[Country], arbitrary[CustomsOffice]) {
            (mode, country, office) =>
              val answers = emptyUserAnswers
                .setValue(OfficeOfTransitCountryPage(index), country)
                .setValue(OfficeOfTransitPage(index), office)
                .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

              val helper = new OfficeOfTransitCheckYourAnswersHelper(answers, mode, index)
              val result = helper.officeOfTransitCountry

              result mustBe Some(
                SummaryListRow(
                  key = Key("Office of transit country".toText),
                  value = Value(country.description.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.OfficeOfTransitCountryController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("office of transit country"),
                          attributes = Map("id" -> "change-office-of-transit-country")
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

    "officeOfTransit" - {
      "must return None" - {
        "when OfficeOfTransitPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new OfficeOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.officeOfTransit
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when OfficeOfTransitPage defined" in {
          forAll(arbitrary[Mode], arbitrary[Country], arbitrary[CustomsOffice]) {
            (mode, country, office) =>
              val answers = emptyUserAnswers
                .setValue(OfficeOfTransitCountryPage(index), country)
                .setValue(OfficeOfTransitPage(index), office)
                .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

              val helper = new OfficeOfTransitCheckYourAnswersHelper(answers, mode, index)
              val result = helper.officeOfTransit

              result mustBe Some(
                SummaryListRow(
                  key = Key("Office of transit".toText),
                  value = Value(office.toString.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.OfficeOfTransitController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("office of transit"),
                          attributes = Map("id" -> "change-office-of-transit")
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

    "addOfficeOfTransitETA" - {
      "must return None" - {
        "when AddOfficeOfTransitETAPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new OfficeOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.addOfficeOfTransitETA
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddOfficeOfTransitETAPage defined" in {
          forAll(arbitrary[Mode], arbitrary[Country], arbitrary[CustomsOffice]) {
            (mode, country, office) =>
              val answers = emptyUserAnswers
                .setValue(OfficeOfTransitCountryPage(index), country)
                .setValue(OfficeOfTransitPage(index), office)
                .setValue(AddOfficeOfTransitETAYesNoPage(index), false)

              val helper = new OfficeOfTransitCheckYourAnswersHelper(answers, mode, index)
              val result = helper.addOfficeOfTransitETA

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add a time of arrival?".toText),
                  value = Value("No".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AddOfficeOfTransitETAYesNoController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("do you want to add a time of arrival"),
                          attributes = Map("id" -> "change-office-of-transit-add-eta")
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

    "OfficeOfTransitETA" - {
      "must return None" - {
        "when OfficeOfTransitETAPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new OfficeOfTransitCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.officeOfTransitETA
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when OfficeOfTransitETAPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val date     = LocalDateTime.of(2000: Int, 1: Int, 1: Int, 23: Int, 12: Int, 0: Int)
              val dateTime = DateTime(date.toLocalDate, date.toLocalTime)

              val answers = emptyUserAnswers.setValue(OfficeOfTransitETAPage(index), dateTime)

              val helper = new OfficeOfTransitCheckYourAnswersHelper(answers, mode, index)
              val result = helper.officeOfTransitETA

              result mustBe Some(
                SummaryListRow(
                  key = Key("Estimated date and time of arrival".toText),
                  value = Value("1 January 2000 23:12".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.OfficeOfTransitETAController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("the estimated date and time of arrival"),
                          attributes = Map("id" -> "change-office-of-transit-eta")
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
