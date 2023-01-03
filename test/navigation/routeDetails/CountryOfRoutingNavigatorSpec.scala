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

class CountryOfRoutingNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Country of Routing Navigator" - {

    "when in NormalMode" - {

      val mode                 = NormalMode
      val mockCountriesService = mock[CountriesService]
      val navigatorProvider    = new CountryOfRoutingNavigatorProviderImpl(mockCountriesService)
      val navigator            = navigatorProvider.apply(mode, index).futureValue

      "when answers complete" - {
        "must redirect to add another country of routing" in {
          forAll(arbitraryCountryOfRoutingAnswers(emptyUserAnswers, index)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(controllers.routeDetails.routing.routes.AddAnotherCountryOfRoutingController.onPageLoad(answers.lrn, mode))
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
      val navigatorProvider = new CountryOfRoutingNavigatorProviderImpl(mockCountriesService)
      val navigator         = navigatorProvider.apply(mode, index).futureValue

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
