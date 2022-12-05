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
import generators.Generators
import models._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import services.CountriesService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExitNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Exit Navigator" - {

    "when in NormalMode" - {

      val mode                 = NormalMode
      val mockCountriesService = mock[CountriesService]
      val navigatorProvider    = new ExitNavigatorProviderImpl(mockCountriesService)
      val navigator            = navigatorProvider.apply(mode).futureValue

      "when answers complete" - {
        "must redirect to exit check your answers" in {
          forAll(arbitraryExitAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(controllers.routeDetails.exit.routes.AddAnotherOfficeOfExitController.onPageLoad(answers.lrn, mode))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode                 = CheckMode
      val mockCountriesService = mock[CountriesService]
      when(mockCountriesService.getCountryCodesCTC()(any()))
        .thenReturn(Future.successful(CountryList(ctcCountries)))
      when(mockCountriesService.getCustomsSecurityAgreementAreaCountries()(any()))
        .thenReturn(Future.successful(CountryList(customsSecurityAgreementAreaCountries)))
      val navigatorProvider = new ExitNavigatorProviderImpl(mockCountriesService)
      val navigator         = navigatorProvider.apply(mode).futureValue

      "when answers complete" - {
        "must redirect to route details check your answers" in {
          forAll(arbitraryRouteDetailsAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(controllers.routeDetails.routes.RouteDetailsAnswersController.onPageLoad(answers.lrn))
          }
        }
      }
    }
  }
}
