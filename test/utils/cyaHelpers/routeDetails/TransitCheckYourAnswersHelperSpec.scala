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
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import models.SecurityDetailsType.NoSecurityDetails
import models.reference.{Country, CustomsOffice}
import models.{Index, Mode, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.{OfficeOfDeparturePage, SecurityDetailsTypePage}
import pages.routeDetails.routing.OfficeOfDestinationPage
import pages.routeDetails.transit.AddOfficeOfTransitYesNoPage
import pages.routeDetails.transit.index.{AddOfficeOfTransitETAYesNoPage, OfficeOfTransitCountryPage, OfficeOfTransitPage}
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import viewModels.ListItem

class TransitCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RouteDetailsUserAnswersGenerator {

  "TransitCheckYourAnswersHelper" - {

    "addOfficeOfTransit" - {
      "must return None" - {
        "when AddOfficeOfTransitYesNoPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransitCheckYourAnswersHelper(emptyUserAnswers, mode)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
              val result = helper.addOfficeOfTransit
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddOfficeOfTransitYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddOfficeOfTransitYesNoPage, true)

              val helper = new TransitCheckYourAnswersHelper(answers, mode)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
              val result = helper.addOfficeOfTransit

              result mustBe Some(
                SummaryListRow(
                  key = Key("Do you want to add an office of transit?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.routeDetails.transit.routes.AddOfficeOfTransitYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if you want to add an office of transit"),
                          attributes = Map("id" -> "add-office-of-transit")
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
        "when OfficeOfTransitPage undefined at index" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransitCheckYourAnswersHelper(emptyUserAnswers, mode)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
              val result = helper.officeOfTransit(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when OfficeOfTransitPage defined at index" in {
          forAll(arbitrary[Mode], arbitrary[CustomsOffice]) {
            (mode, customsOffice) =>
              val initialAnswers = emptyUserAnswers.setValue(OfficeOfTransitPage(index), customsOffice)

              forAll(arbitraryOfficeOfTransitAnswers(initialAnswers, index)) {
                answers =>
                  val helper = new TransitCheckYourAnswersHelper(answers, mode)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
                  val result = helper.officeOfTransit(index)

                  result mustBe Some(
                    SummaryListRow(
                      key = Key("Office of transit 1".toText),
                      value = Value(customsOffice.toString.toText),
                      actions = Some(
                        Actions(
                          items = List(
                            ActionItem(
                              content = "Change".toText,
                              href = controllers.routeDetails.transit.index.routes.CheckOfficeOfTransitAnswersController.onPageLoad(answers.lrn, index).url,
                              visuallyHiddenText = Some("office of transit 1"),
                              attributes = Map("id" -> "change-office-of-transit-1")
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

    "listItems" - {
      "must return list items" in {
        val country1 = arbitrary[Country].sample.value
        val country2 = arbitrary[Country].sample.value
        val country3 = arbitrary[Country].sample.value

        def customsOffice  = arbitrary[CustomsOffice].sample.value
        val customsOffice1 = customsOffice.copy(id = country1.code.code)
        val customsOffice2 = customsOffice.copy(id = country2.code.code)

        val answers = emptyUserAnswers
          .setValue(OfficeOfDeparturePage, customsOffice)
          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
          .setValue(OfficeOfDestinationPage, customsOffice1)
          .setValue(OfficeOfTransitCountryPage(Index(0)), country1)
          .setValue(OfficeOfTransitPage(Index(0)), customsOffice1)
          .setValue(AddOfficeOfTransitETAYesNoPage(Index(0)), false)
          .setValue(OfficeOfTransitCountryPage(Index(1)), country2)
          .setValue(OfficeOfTransitPage(Index(1)), customsOffice2)
          .setValue(AddOfficeOfTransitETAYesNoPage(Index(1)), false)
          .setValue(OfficeOfTransitCountryPage(Index(2)), country3)

        val helper = new TransitCheckYourAnswersHelper(answers, NormalMode)(Seq(country1.code.code), Nil)
        helper.listItems mustBe Seq(
          Right(
            ListItem(
              name = s"$customsOffice1",
              changeUrl = controllers.routeDetails.transit.index.routes.CheckOfficeOfTransitAnswersController.onPageLoad(lrn, Index(0)).url,
              removeUrl = None
            )
          ),
          Right(
            ListItem(
              name = s"$country2 - $customsOffice2",
              changeUrl = controllers.routeDetails.transit.index.routes.CheckOfficeOfTransitAnswersController.onPageLoad(lrn, Index(1)).url,
              removeUrl = Some(controllers.routeDetails.transit.index.routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, Index(1)).url)
            )
          ),
          Left(
            ListItem(
              name = s"$country3",
              changeUrl = controllers.routeDetails.transit.index.routes.OfficeOfTransitController.onPageLoad(lrn, NormalMode, Index(2)).url,
              removeUrl = Some(controllers.routeDetails.transit.index.routes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, Index(2)).url)
            )
          )
        )
      }
    }
  }
}
