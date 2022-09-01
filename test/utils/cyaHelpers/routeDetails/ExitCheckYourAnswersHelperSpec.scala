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
import models.reference.{Country, CustomsOffice}
import models.{Index, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.exit.index.{OfficeOfExitCountryPage, OfficeOfExitPage}
import viewModels.ListItem

class ExitCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "ExitCheckYourAnswersHelper" - {

    "listItems" - {
      "must return list items" in {
        val country = arbitrary[Country].sample.value

        val customsOffice = arbitrary[CustomsOffice].sample.value

        val answers = emptyUserAnswers
          .setValue(OfficeOfExitCountryPage(Index(0)), country)
          .setValue(OfficeOfExitPage(Index(0)), customsOffice)
          .setValue(OfficeOfExitCountryPage(Index(1)), country)

        val helper = new ExitCheckYourAnswersHelper(answers, NormalMode)
        helper.listItems mustBe Seq(
          Right(
            ListItem(
              name = s"$country - $customsOffice",
              changeUrl = routes.CheckOfficeOfExitAnswersController.onPageLoad(lrn, Index(0)).url,
              removeUrl = Some(routes.ConfirmRemoveOfficeOfExitController.onPageLoad(lrn, Index(0)).url)
            )
          ),
          Left(
            ListItem(
              name = s"$country",
              changeUrl = routes.OfficeOfExitController.onPageLoad(lrn, Index(1), NormalMode).url,
              removeUrl = Some(routes.ConfirmRemoveOfficeOfExitController.onPageLoad(lrn, Index(1)).url)
            )
          )
        )
      }
    }
  }
}
