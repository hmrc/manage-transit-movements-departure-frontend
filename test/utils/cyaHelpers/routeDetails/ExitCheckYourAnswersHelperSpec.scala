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
import models.domain.UserAnswersReader
import models.journeyDomain.routeDetails.exit.OfficeOfExitDomain
import models.reference.{Country, CustomsOffice}
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.exit.index.{OfficeOfExitCountryPage, OfficeOfExitPage}
import utils.cyaHelpers.routeDetails.exit.ExitCheckYourAnswersHelper
import viewModels.ListItem

class ExitCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "ExitCheckYourAnswersHelper" - {

    "officeOfExit" - {
      "must return None" - {
        "when office of exit is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new ExitCheckYourAnswersHelper(emptyUserAnswers, mode)
              val result = helper.officeOfExit(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when office of exit is defined" in {
          forAll(arbitraryOfficeOfExitAnswers(emptyUserAnswers, index), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val officeOfExit = UserAnswersReader[OfficeOfExitDomain](
                OfficeOfExitDomain.userAnswersReader(index)
              ).run(userAnswers).value

              val helper = new ExitCheckYourAnswersHelper(userAnswers, mode)
              val result = helper.officeOfExit(index).get

              result.key.value mustBe "Office of exit 1"
              result.value.value mustBe officeOfExit.label
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.CheckOfficeOfExitAnswersController.onPageLoad(userAnswers.lrn, index, mode).url
              action.visuallyHiddenText.get mustBe "office of exit 1"
              action.id mustBe "change-office-of-exit-1"
          }
        }
      }
    }

    "listItems" - {
      "must return list items" in {
        val mode          = arbitrary[Mode].sample.value
        val country       = arbitrary[Country].sample.value
        val customsOffice = arbitrary[CustomsOffice].sample.value

        val answers = emptyUserAnswers
          .setValue(OfficeOfExitCountryPage(Index(0)), country)
          .setValue(OfficeOfExitPage(Index(0)), customsOffice)
          .setValue(OfficeOfExitCountryPage(Index(1)), country)

        val helper = new ExitCheckYourAnswersHelper(answers, mode)
        helper.listItems mustBe Seq(
          Right(
            ListItem(
              name = s"$country - $customsOffice",
              changeUrl = routes.CheckOfficeOfExitAnswersController.onPageLoad(lrn, Index(0), mode).url,
              removeUrl = Some(routes.ConfirmRemoveOfficeOfExitController.onPageLoad(lrn, Index(0), mode).url)
            )
          ),
          Left(
            ListItem(
              name = s"$country",
              changeUrl = routes.OfficeOfExitController.onPageLoad(lrn, Index(1), mode).url,
              removeUrl = Some(routes.ConfirmRemoveOfficeOfExitController.onPageLoad(lrn, Index(1), mode).url)
            )
          )
        )
      }
    }
  }
}
