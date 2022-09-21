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

package viewModels.taskList

import base.SpecBase
import generators.{Generators, RouteDetailsUserAnswersGenerator}
import models.NormalMode
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.routing.{CountryOfDestinationPage, OfficeOfDestinationPage}
import viewModels.taskList.TaskStatus._

class RouteDetailsTaskSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RouteDetailsUserAnswersGenerator {

  "name" - {
    "must be Route details" - {
      "when status is CannotStartYet" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = RouteDetailsTask(CannotStartYet, href)
            task.name mustBe "Route details"
        }
      }
    }

    "must be Add route details" - {
      "when status is NotStarted" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = RouteDetailsTask(NotStarted, href)
            task.name mustBe "Add route details"
        }
      }
    }

    "must be Edit route details" - {
      "when status is Completed" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = RouteDetailsTask(Completed, href)
            task.name mustBe "Edit route details"
        }
      }

      "when status is InProgress" in {
        forAll(Gen.option(Gen.alphaNumStr)) {
          href =>
            val task = RouteDetailsTask(InProgress, href)
            task.name mustBe "Edit route details"
        }
      }
    }
  }

  "id" - {
    "must be route-details" in {
      val task = RouteDetailsTask(emptyUserAnswers)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
      task.id mustBe "route-details"
    }
  }

  "apply" - {
    "when NotStarted" in {
      val userAnswers = emptyUserAnswers
      val task        = RouteDetailsTask(userAnswers)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
      task.status mustBe NotStarted
      task.href.get mustBe
        controllers.routeDetails.routing.routes.CountryOfDestinationController.onPageLoad(userAnswers.lrn, NormalMode).url
    }

    "when InProgress" in {
      val userAnswers = emptyUserAnswers
        .setValue(CountryOfDestinationPage, arbitrary[Country].sample.value)
        .setValue(OfficeOfDestinationPage, arbitrary[CustomsOffice].sample.value)
      val task = RouteDetailsTask(userAnswers)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
      task.status mustBe InProgress
      task.href.get mustBe
        controllers.routeDetails.routing.routes.BindingItineraryController.onPageLoad(userAnswers.lrn, NormalMode).url
    }

    "when Completed" ignore {
      forAll(arbitraryRouteDetailsAnswers(emptyUserAnswers)) {
        userAnswers =>
          val task = RouteDetailsTask(userAnswers)(ctcCountryCodes, customsSecurityAgreementAreaCountryCodes)
          task.status mustBe Completed
          task.href.get mustBe ???
      }
    }
  }
}
