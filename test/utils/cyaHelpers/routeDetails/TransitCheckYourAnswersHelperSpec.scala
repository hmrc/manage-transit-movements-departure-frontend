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
import controllers.routeDetails.transit.index.{routes => indexRoutes}
import generators.Generators
import models.SecurityDetailsType.NoSecurityDetails
import models.domain.UserAnswersReader
import models.journeyDomain.routeDetails.transit.OfficeOfTransitDomain
import models.reference.{Country, CustomsOffice}
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.{OfficeOfDeparturePage, SecurityDetailsTypePage}
import pages.routeDetails.routing.OfficeOfDestinationPage
import pages.routeDetails.transit._
import pages.routeDetails.transit.index.{AddOfficeOfTransitETAYesNoPage, OfficeOfTransitCountryPage, OfficeOfTransitPage}
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import utils.cyaHelpers.routeDetails.transit.TransitCheckYourAnswersHelper
import viewModels.ListItem

class TransitCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "TransitCheckYourAnswersHelper" - {

    "includesT2Declarations" - {
      "must return None" - {
        "when T2DeclarationTypeYesNoPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransitCheckYourAnswersHelper(emptyUserAnswers, mode)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
              val result = helper.includesT2Declarations
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when T2DeclarationTypeYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(T2DeclarationTypeYesNoPage, true)

              val helper = new TransitCheckYourAnswersHelper(answers, mode)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
              val result = helper.includesT2Declarations

              result mustBe Some(
                SummaryListRow(
                  key = Key("Does the transit include any T2 declarations?".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = controllers.routeDetails.transit.routes.T2DeclarationTypeYesNoController.onPageLoad(answers.lrn, mode).url,
                          visuallyHiddenText = Some("if the transit includes any T2 declarations"),
                          attributes = Map("id" -> "change-includes-t2-declarations")
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
                          attributes = Map("id" -> "change-add-office-of-transit")
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
        "when office of transit is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new TransitCheckYourAnswersHelper(emptyUserAnswers, mode)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
              val result = helper.officeOfTransit(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when office of transit is defined" in {
          forAll(arbitraryOfficeOfTransitAnswers(emptyUserAnswers, index), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val officeOfExit = UserAnswersReader[OfficeOfTransitDomain](
                OfficeOfTransitDomain.userAnswersReader(index, ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
              ).run(userAnswers).value

              val helper = new TransitCheckYourAnswersHelper(userAnswers, mode)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
              val result = helper.officeOfTransit(index).get

              result.key.value mustBe "Office of transit 1"
              result.value.value mustBe officeOfExit.label
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe indexRoutes.CheckOfficeOfTransitAnswersController.onPageLoad(userAnswers.lrn, mode, index).url
              action.visuallyHiddenText.get mustBe "office of transit 1"
              action.id mustBe "change-office-of-transit-1"
          }
        }
      }
    }

    "listItems" - {
      "must return list items" in {
        val mode = arbitrary[Mode].sample.value

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

        val helper = new TransitCheckYourAnswersHelper(answers, mode)(Seq(country1.code.code), Nil)
        helper.listItems mustBe Seq(
          Right(
            ListItem(
              name = s"$customsOffice1",
              changeUrl = indexRoutes.CheckOfficeOfTransitAnswersController.onPageLoad(lrn, mode, Index(0)).url,
              removeUrl = None
            )
          ),
          Right(
            ListItem(
              name = s"$country2 - $customsOffice2",
              changeUrl = indexRoutes.CheckOfficeOfTransitAnswersController.onPageLoad(lrn, mode, Index(1)).url,
              removeUrl = Some(indexRoutes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, mode, Index(1)).url)
            )
          ),
          Left(
            ListItem(
              name = s"$country3",
              changeUrl = indexRoutes.OfficeOfTransitController.onPageLoad(lrn, mode, Index(2)).url,
              removeUrl = Some(indexRoutes.ConfirmRemoveOfficeOfTransitController.onPageLoad(lrn, mode, Index(2)).url)
            )
          )
        )
      }
    }
  }
}
