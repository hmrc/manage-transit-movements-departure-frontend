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
import controllers.routeDetails.routing.index.{routes => indexRoutes}
import controllers.routeDetails.routing.{routes => routingRoutes}
import generators.Generators
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.routing._
import pages.routeDetails.routing.index.CountryOfRoutingPage
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.html.components.{ActionItem, Actions}
import utils.cyaHelpers.routeDetails.routing.RoutingCheckYourAnswersHelper
import viewModels.ListItem

class RoutingCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "RoutingCheckYourAnswersHelper" - {

    "countryOfDestination" - {
      "must return None" - {
        "when CountryOfDestinationPage undefined at index" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RoutingCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.countryOfDestination
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when CountryOfDestinationPage defined" in {
          forAll(arbitrary[Country], arbitrary[Mode]) {
            (country, mode) =>
              val answers = emptyUserAnswers.setValue(CountryOfDestinationPage, country)

              val helper = new RoutingCheckYourAnswersHelper(answers, mode)
              val result = helper.countryOfDestination

              result mustBe Some(
                SummaryListRow(
                  key = Key("Country of destination".toText),
                  value = Value(s"$country".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routingRoutes.CountryOfDestinationController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("country of destination"),
                          attributes = Map("id" -> "country-of-destination")
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

    "officeOfDestination" - {
      "must return None" - {
        "when OfficeOfDestinationPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RoutingCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.officeOfDestination
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when OfficeOfDestinationPage defined" in {
          forAll(arbitrary[CustomsOffice], arbitrary[Mode]) {
            (customsOffice, mode) =>
              val answers = emptyUserAnswers.setValue(OfficeOfDestinationPage, customsOffice)

              val helper = new RoutingCheckYourAnswersHelper(answers, mode)
              val result = helper.officeOfDestination

              result mustBe Some(
                SummaryListRow(
                  key = Key("Office of destination".toText),
                  value = Value(s"$customsOffice".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routingRoutes.OfficeOfDestinationController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("office of destination"),
                          attributes = Map("id" -> "office-of-destination")
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

    "bindingItinerary" - {
      "must return None" - {
        "when BindingItineraryPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RoutingCheckYourAnswersHelper(emptyUserAnswers, mode)
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

              val helper = new RoutingCheckYourAnswersHelper(answers, mode)
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
                          href = routingRoutes.BindingItineraryController.onPageLoad(answers.lrn, mode).url,
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
              val helper = new RoutingCheckYourAnswersHelper(emptyUserAnswers, mode)
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

              val helper = new RoutingCheckYourAnswersHelper(answers, mode)
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
                          href = routingRoutes.AddCountryOfRoutingYesNoController.onPageLoad(answers.lrn, mode).url,
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

    "countryOfRouting" - {
      "must return None" - {
        "when CountryOfRoutingPage undefined at index" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RoutingCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.countryOfRouting(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when CountryOfRoutingPage defined at index" in {
          forAll(arbitrary[Mode], arbitrary[Country]) {
            (mode, country) =>
              val answers = emptyUserAnswers
                .setValue(CountryOfRoutingPage(index), country)

              val helper = new RoutingCheckYourAnswersHelper(answers, mode)
              val result = helper.countryOfRouting(index)

              result mustBe Some(
                SummaryListRow(
                  key = Key("Country of routing 1".toText),
                  value = Value(country.description.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = indexRoutes.CountryOfRoutingController.onPageLoad(answers.lrn, mode, index).url,
                          visuallyHiddenText = Some("country of routing 1"),
                          attributes = Map("id" -> "change-country-of-routing-1")
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

    "listItems" - {
      "must return list items" in {
        val mode        = arbitrary[Mode].sample.value
        def countryCode = arbitrary[CountryCode].sample.value

        val answers = emptyUserAnswers
          .setValue(CountryOfRoutingPage(Index(0)), Country(countryCode, "France"))
          .setValue(CountryOfRoutingPage(Index(1)), Country(countryCode, "Portugal"))

        val helper = new RoutingCheckYourAnswersHelper(answers, mode)
        helper.listItems mustBe Seq(
          Right(
            ListItem(
              name = "France",
              changeUrl = indexRoutes.CountryOfRoutingController.onPageLoad(answers.lrn, mode, Index(0)).url,
              removeUrl = Some(indexRoutes.RemoveCountryOfRoutingYesNoController.onPageLoad(answers.lrn, mode, Index(0)).url)
            )
          ),
          Right(
            ListItem(
              name = "Portugal",
              changeUrl = indexRoutes.CountryOfRoutingController.onPageLoad(answers.lrn, mode, Index(1)).url,
              removeUrl = Some(indexRoutes.RemoveCountryOfRoutingYesNoController.onPageLoad(answers.lrn, mode, Index(1)).url)
            )
          )
        )
      }
    }
  }
}
