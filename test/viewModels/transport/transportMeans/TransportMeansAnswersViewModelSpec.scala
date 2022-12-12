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

package viewModels.transport.transportMeans

import base.SpecBase
import generators.Generators
import models.Mode
import models.reference.Nationality
import models.transport.transportMeans.BorderModeOfTransport
import models.transport.transportMeans.departure.{InlandMode, Identification => DepartureIdentification}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.transport.transportMeans.{departure, AnotherVehicleCrossingYesNoPage, BorderModeOfTransportPage}
import pages.transport.transportMeans.departure.InlandModePage
import viewModels.transport.transportMeans.TransportMeansAnswersViewModel.TransportMeansAnswersViewModelProvider

class TransportMeansAnswersViewModelSpec extends SpecBase with Generators {

  private val mode = arbitrary[Mode].sample.value

  "TransportMeansAnswersViewModel" - {

    "must render an inland mode section" in {
      val userAnswers = emptyUserAnswers
        .setValue(InlandModePage, arbitrary[InlandMode].sample.value)

      val viewModelProvider = new TransportMeansAnswersViewModelProvider()
      val result            = viewModelProvider.apply(userAnswers, mode)

      val section = result.sections.head
      section.sectionTitle.get mustBe "Inland mode of transport"
      section.rows.size mustBe 1
      section.addAnotherLink must not be defined
    }

    "must render a departure means section" in {
      val userAnswers = emptyUserAnswers
        .setValue(departure.IdentificationPage, arbitrary[DepartureIdentification].sample.value)
        .setValue(departure.MeansIdentificationNumberPage, Gen.alphaNumStr.sample.value)
        .setValue(departure.VehicleCountryPage, arbitrary[Nationality].sample.value)

      val viewModelProvider = new TransportMeansAnswersViewModelProvider()
      val result            = viewModelProvider.apply(userAnswers, mode)

      val section = result.sections(1)
      section.sectionTitle.get mustBe "Departure means of transport"
      section.rows.size mustBe 3
      section.addAnotherLink must not be defined
    }

    "must render a border mode section" in {
      val userAnswers = emptyUserAnswers
        .setValue(AnotherVehicleCrossingYesNoPage, arbitrary[Boolean].sample.value)
        .setValue(BorderModeOfTransportPage, arbitrary[BorderModeOfTransport].sample.value)

      val viewModelProvider = new TransportMeansAnswersViewModelProvider()
      val result            = viewModelProvider.apply(userAnswers, mode)

      val section = result.sections(2)
      section.sectionTitle.get mustBe "Border mode of transport"
      section.rows.size mustBe 2
      section.addAnotherLink must not be defined
    }
  }
}
