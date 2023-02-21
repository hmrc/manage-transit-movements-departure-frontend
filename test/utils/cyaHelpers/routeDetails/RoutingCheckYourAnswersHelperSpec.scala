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
import controllers.routeDetails.routing.index.{routes => indexRoutes}
import controllers.routeDetails.routing.{routes => routingRoutes}
import generators.Generators
import models.domain.UserAnswersReader
import models.journeyDomain.routeDetails.routing.CountryOfRoutingDomain
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.routing._
import pages.routeDetails.routing.index.CountryOfRoutingPage
import pages.sections.routeDetails.routing.CountryOfRoutingSection
import play.api.libs.json.Json
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
                          attributes = Map("id" -> "change-country-of-destination")
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
                          attributes = Map("id" -> "change-office-of-destination")
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
                  key = Key("Are you using a binding itinerary?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routingRoutes.BindingItineraryController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you are using a binding itinerary"),
                          attributes = Map("id" -> "change-binding-itinerary")
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
                          attributes = Map("id" -> "change-add-country-of-routing")
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
        "when country of routing is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RoutingCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.countryOfRouting(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when country of routing is defined" in {
          forAll(arbitraryCountryOfRoutingAnswers(emptyUserAnswers, index), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val countryOfRouting = UserAnswersReader[CountryOfRoutingDomain](
                CountryOfRoutingDomain.userAnswersReader(index)
              ).run(userAnswers).value

              val helper = new RoutingCheckYourAnswersHelper(userAnswers, mode)
              val result = helper.countryOfRouting(index).get

              result.key.value mustBe "Country of routing 1"
              result.value.value mustBe countryOfRouting.country.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe indexRoutes.CountryOfRoutingController.onPageLoad(userAnswers.lrn, mode, index).url
              action.visuallyHiddenText.get mustBe "country of routing 1"
              action.id mustBe "change-country-of-routing-1"
          }
        }
      }
    }

    "addOrRemoveCountriesOfRouting" - {
      "must return None" - {
        "when countries of routing array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new RoutingCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.addOrRemoveCountriesOfRouting
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when countries of routing array is non-empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(CountryOfRoutingSection(Index(0)), Json.obj("foo" -> "bar"))
              val helper  = new RoutingCheckYourAnswersHelper(answers, mode)
              val result  = helper.addOrRemoveCountriesOfRouting.get

              result.id mustBe "add-or-remove-transit-route-countries"
              result.text mustBe "Add or remove transit route countries"
              result.href mustBe routingRoutes.AddAnotherCountryOfRoutingController.onPageLoad(answers.lrn, mode).url
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
