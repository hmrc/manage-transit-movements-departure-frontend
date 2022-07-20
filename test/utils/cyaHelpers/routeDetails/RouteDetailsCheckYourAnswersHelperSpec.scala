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
import controllers.routeDetails.routes
import generators.Generators
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

class RouteDetailsCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "RouteDetailsCheckYourAnswersHelper" - {

    "bindingItinerary" - {
      "must return None" - {
        "when BindingItineraryPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RouteDetailsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.bindingItinerary
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when BindingItineraryPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(BindingItineraryPage, true)

              val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
              val result = helper.bindingItinerary

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want the transit to follow a binding itinerary?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.BindingItineraryController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you want the transit to follow a binding itinerary"),
                          attributes = Map("id" -> "binding-itinerary")
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

    "addCountryOfRouting" - {
      "must return None" - {
        "when AddCountryOfRoutingYesNoPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RouteDetailsCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.addCountryOfRouting
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddCountryOfRoutingYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddCountryOfRoutingYesNoPage, true)

              val helper = new RouteDetailsCheckYourAnswersHelper(answers, mode)
              val result = helper.addCountryOfRouting

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add a country to the transit route?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AddCountryOfRoutingYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you want to add a country to the transit route"),
                          attributes = Map("id" -> "add-country-of-routing")
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
