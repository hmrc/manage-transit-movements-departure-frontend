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
import generators.Generators
import models.reference.{Country, CountryCode, CustomsOffice}
import models.{Index, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.transit._
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

class TransitCheckYourAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "TransitCheckYourAnswersHelper" - {

    "listItems" - {
      "must return list items" in {
        val countryCode    = arbitrary[CountryCode].sample.value
        val customsOffice1 = arbitrary[CustomsOffice].sample.value
        val customsOffice2 = arbitrary[CustomsOffice].sample.value
        val answers = emptyUserAnswers
          .setValue(OfficeOfTransitCountryPage(Index(0)), Country(countryCode, "France"))
          .setValue(OfficeOfTransitPage(Index(0)), customsOffice1)
          .setValue(AddOfficeOfTransitETAYesNoPage(Index(0)), false)
          .setValue(OfficeOfTransitCountryPage(Index(1)), Country(countryCode, "Portugal"))
          .setValue(OfficeOfTransitPage(Index(1)), customsOffice2)
          .setValue(AddOfficeOfTransitETAYesNoPage(Index(1)), false)
        val helper = new TransitCheckYourAnswersHelper(answers, NormalMode)
        helper.listItems mustBe Seq(
          Right(
            ListItem(
              name = "France",
              changeUrl = controllers.routeDetails.transit.routes.AddAnotherOfficeOfTransitController.onPageLoad(answers.lrn).url,
              removeUrl = controllers.routeDetails.routing.routes.RemoveCountryOfRoutingYesNoController.onPageLoad(answers.lrn, Index(0)).url
            )
          ),
          Right(
            ListItem(
              name = "Portugal",
              changeUrl = controllers.routeDetails.transit.routes.AddAnotherOfficeOfTransitController.onPageLoad(answers.lrn).url,
              removeUrl = controllers.routeDetails.routing.routes.RemoveCountryOfRoutingYesNoController.onPageLoad(answers.lrn, Index(1)).url
            )
          )
        )
      }
    }
  }
}
