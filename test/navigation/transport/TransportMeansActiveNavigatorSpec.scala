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

package navigation.transport

import base.SpecBase
import generators.Generators
import models._
import models.reference.{Country, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.routeDetails.transit.index.{OfficeOfTransitCountryPage, OfficeOfTransitPage}

class TransportMeansActiveNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Pre Requisites Navigator" - {

    "when in NormalMode" - {

      val mode              = NormalMode
      val navigatorProvider = new TransportMeansActiveNavigatorProviderImpl()
      val navigator         = navigatorProvider.apply(mode, activeIndex)

      "when answers complete and office of transit is present" - {
        "must redirect to AddAnotherBorderTransportPage CYA" in {
          val userAnswers = emptyUserAnswers
            .setValue(OfficeOfTransitCountryPage(index), arbitrary[Country].sample.value)
            .setValue(OfficeOfTransitPage(index), arbitrary[CustomsOffice].sample.value)
          forAll(arbitraryPreRequisitesAnswers(userAnswers)) {
            initialAnswers =>
              forAll(arbitraryTransportMeansDepartureAnswers(initialAnswers)) {
                secondaryAnswers =>
                  forAll(arbitraryTransportMeansActiveAnswers(secondaryAnswers, activeIndex)) {
                    answers =>
                      navigator
                        .nextPage(answers)
                        .mustBe(controllers.transport.transportMeans.active.routes.AddAnotherBorderTransportController.onPageLoad(answers.lrn, mode))
                  }
              }
          }
        }
      }

      "when answers complete and office of transit is not present" - {
        "must redirect to TransportMeansCheckYourAnswersController CYA" in {
          forAll(arbitraryPreRequisitesAnswers(emptyUserAnswers)) {
            initialAnswers =>
              forAll(arbitraryTransportMeansDepartureAnswers(initialAnswers)) {
                secondaryAnswers =>
                  forAll(arbitraryTransportMeansActiveAnswers(secondaryAnswers, activeIndex)) {
                    answers =>
                      navigator
                        .nextPage(answers)
                        .mustBe(controllers.transport.transportMeans.routes.TransportMeansCheckYourAnswersController.onPageLoad(answers.lrn, mode))
                  }
              }
          }
        }
      }
// TODO - CHANGE MUST BE TO CHECK YOUR ANSWERS PAGE ONCE IMPLEMENTED
      "when in CheckMode" - {

        val mode              = CheckMode
        val navigatorProvider = new TransportMeansNavigatorProviderImpl()
        val navigator         = navigatorProvider.apply(mode)

        "when answers complete" - {
          "must redirect to final check your answers" ignore {
            forAll(arbitraryTransportAnswers(emptyUserAnswers)) {
              initialAnswers =>
                forAll(arbitraryTransportMeansDepartureAnswers(initialAnswers)) {
                  answers =>
                    navigator
                      .nextPage(answers)
                      .mustBe(???)
                }
            }
          }
        }
      }
    }
  }
}
