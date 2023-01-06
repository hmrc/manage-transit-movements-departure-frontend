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
import controllers.routeDetails.exit.index.routes
import generators.Generators
import models.Mode
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.exit.index.{OfficeOfExitCountryPage, OfficeOfExitPage}
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import utils.cyaHelpers.routeDetails.exit.OfficeOfExitCheckYourAnswersHelper

class OfficeOfExitCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "OfficeOfExitCheckYourAnswersHelper" - {

    "officeOfExitCountry" - {
      "must return None" - {
        "when OfficeOfExitCountryPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new OfficeOfExitCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.officeOfExitCountry
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when OfficeOfExitCountryPage defined" in {
          forAll(arbitrary[Mode], arbitrary[Country]) {
            (mode, country) =>
              val answers = emptyUserAnswers
                .setValue(OfficeOfExitCountryPage(index), country)
              val helper = new OfficeOfExitCheckYourAnswersHelper(answers, mode, index)
              val result = helper.officeOfExitCountry

              result mustBe Some(
                SummaryListRow(
                  key = Key("Country".toText),
                  value = Value(country.description.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.OfficeOfExitCountryController.onPageLoad(answers.lrn, index, mode).url,
                          visuallyHiddenText = Some("office of exit’s country"),
                          attributes = Map("id" -> "change-office-of-exit-country")
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

    "officeOfExit" - {
      "must return None" - {
        "when OfficeOfExitPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new OfficeOfExitCheckYourAnswersHelper(emptyUserAnswers, mode, index)
              val result = helper.officeOfExit
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when OfficeOfExitPage defined" in {
          forAll(arbitrary[Mode], arbitrary[CustomsOffice]) {
            (mode, customsOffice) =>
              val answers = emptyUserAnswers.setValue(OfficeOfExitPage(index), customsOffice)
              val helper  = new OfficeOfExitCheckYourAnswersHelper(answers, mode, index)
              val result  = helper.officeOfExit

              result mustBe Some(
                SummaryListRow(
                  key = Key("Office location".toText),
                  value = Value(customsOffice.toString.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.OfficeOfExitController.onPageLoad(answers.lrn, index, mode).url,
                          visuallyHiddenText = Some("office of exit’s location"),
                          attributes = Map("id" -> "change-office-of-exit")
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
