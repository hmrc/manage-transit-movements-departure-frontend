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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import services.CountriesService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TransitNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with RouteDetailsUserAnswersGenerator {

  private val navigator = new TransitNavigator(ctcCountryCodes, euCountryCodes, customsSecurityAgreementAreaCountryCodes)

  "Transit Navigator" - {

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers complete" - {
        "must redirect to transit check your answers" ignore {
          forAll(arbitraryTransitAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers, mode)
                .mustBe(controllers.routeDetails.transit.routes.AddAnotherOfficeOfTransitController.onPageLoad(answers.lrn))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "when answers complete" - {
        "must redirect to route details check your answers" ignore {
          forAll(arbitraryRouteDetailsAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers, mode)
                .mustBe(???)
          }
        }
      }
    }
  }

  "Transit Navigator Provider" - {

    "must retrieve reference data lists" in {
      val mockService = mock[CountriesService]

      val ctcCountries                          = arbitrary[CountryList].sample.value
      val euCountries                           = arbitrary[CountryList].sample.value
      val customsSecurityAgreementAreaCountries = arbitrary[CountryList].sample.value

      when(mockService.getTransitCountries()(any()))
        .thenReturn(Future.successful(ctcCountries))
      when(mockService.getCommunityCountries()(any()))
        .thenReturn(Future.successful(euCountries))
      when(mockService.getCustomsSecurityAgreementAreaCountries()(any()))
        .thenReturn(Future.successful(customsSecurityAgreementAreaCountries))

      val provider = new TransitNavigatorProviderImpl(mockService)
      provider.apply().futureValue

      verify(mockService).getTransitCountries()(any())
      verify(mockService).getCommunityCountries()(any())
      verify(mockService).getCustomsSecurityAgreementAreaCountries()(any())
    }
  }
}
