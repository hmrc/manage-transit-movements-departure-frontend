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

package navigation.routeDetails

import base.SpecBase
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import models._
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.transit.index.{AddOfficeOfTransitETAYesNoPage, OfficeOfTransitCountryPage, OfficeOfTransitPage}
import pages.routeDetails.transit.{AddOfficeOfTransitYesNoPage, T2DeclarationTypeYesNoPage}

class OfficeOfTransitNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RouteDetailsUserAnswersGenerator {

  private val navigator = new OfficeOfTransitNavigator(index)

  "Office of Transit Country Navigator" - {

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers complete" - {
        "must redirect to check your answers for office of transit" in {
          forAll(arbitraryOfficeOfTransitAnswers(emptyUserAnswers, index)) {
            answers =>
              navigator
                .nextPage(answers, mode)
                .mustBe(controllers.routeDetails.transit.index.routes.CheckOfficeOfTransitAnswersController.onPageLoad(lrn, index))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "when answers complete" - {
        "must redirect to the task list" ignore {
          forAll(arbitrary[Country], arbitrary[CustomsOffice], arbitraryRoutingAnswers(emptyUserAnswers)) {
            (country, office, routingAnswers) =>
              val answers = routingAnswers
                .setValue(T2DeclarationTypeYesNoPage, false)
                .setValue(AddOfficeOfTransitYesNoPage, true)
                .setValue(OfficeOfTransitCountryPage(index), country)
                .setValue(OfficeOfTransitPage(index), office)
                .setValue(AddOfficeOfTransitETAYesNoPage(index), false)
              navigator
                .nextPage(answers, mode)
                .mustBe(controllers.routes.TaskListController.onPageLoad(lrn))
          }
        }
      }
    }
  }
}
