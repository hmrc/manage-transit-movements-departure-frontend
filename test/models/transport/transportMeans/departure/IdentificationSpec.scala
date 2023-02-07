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

package models.transport.transportMeans.departure

import base.SpecBase
import generators.Generators
import models.transport.transportMeans.departure.Identification._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.transport.transportMeans.departure.InlandModePage
import play.api.libs.json.{JsError, JsString, Json}

class IdentificationSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Identification" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(Identification.values)

      forAll(gen) {
        identification =>
          JsString(identification.toString).validate[Identification].asOpt.value mustEqual identification
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!Identification.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>
          JsString(invalidValue).validate[Identification] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(Identification.values)

      forAll(gen) {
        identification =>
          Json.toJson(identification) mustEqual JsString(identification.toString)
      }
    }

    "must have an associated arg value" - {

      "when sea going vessel" in {
        val result = SeaGoingVessel.arg
        result mustBe "name of the sea-going vessel"
      }
      "when IATA flight number" in {
        val result = IataFlightNumber.arg
        result mustBe "IATA flight number"
      }
      "when inland waterways vehicle" in {
        val result = InlandWaterwaysVehicle.arg
        result mustBe "name of the inland waterways vehicle"
      }
      "when IMO ship identification number" in {
        val result = ImoShipIdNumber.arg
        result mustBe "IMO ship identification number"
      }
      "when wagon number" in {
        val result = WagonNumber.arg
        result mustBe "wagon number"
      }
      "when train number" in {
        val result = TrainNumber.arg
        result mustBe "train number"
      }
      "when reg number of road vehicle" in {
        val result = RegNumberRoadVehicle.arg
        result mustBe "registration number of the road vehicle"
      }
      "when reg number of road trailer" in {
        val result = RegNumberRoadTrailer.arg
        result mustBe "registration number of the road trailer"
      }
      "when reg number of aircraft" in {
        val result = RegNumberAircraft.arg
        result mustBe "registration number of the aircraft"
      }
      "when european vessel identification number" in {
        val result = EuropeanVesselIdNumber.arg
        result mustBe "European vessel identification number (ENI code)"
      }
      "when unknown" in {
        val result = Unknown.arg
        result mustBe "ask gavin what this should be"
      }
    }

    "Radio options" - {

      "Must return the correct number of radios" - {
        "When InlandMode is 'Maritime'" in {
          val answers = emptyUserAnswers
            .setValue(InlandModePage, InlandMode.Maritime)

          val radios = Identification.valuesU(answers)
          val expected = Seq(
            Identification.SeaGoingVessel,
            Identification.ImoShipIdNumber
          )

          radios mustBe expected
        }

        "When InlandMode is 'Rail'" in {
          val answers = emptyUserAnswers
            .setValue(InlandModePage, InlandMode.Rail)

          val radios = Identification.valuesU(answers)
          val expected = Seq(
            Identification.WagonNumber,
            Identification.TrainNumber
          )

          radios mustBe expected
        }

        "When InlandMode is 'Road'" in {
          val answers = emptyUserAnswers
            .setValue(InlandModePage, InlandMode.Road)

          val radios = Identification.valuesU(answers)
          val expected = Seq(
            Identification.RegNumberRoadVehicle,
            Identification.RegNumberRoadTrailer
          )

          radios mustBe expected
        }

        "When InlandMode is 'Air'" in {
          val answers = emptyUserAnswers
            .setValue(InlandModePage, InlandMode.Air)

          val radios = Identification.valuesU(answers)
          val expected = Seq(
            Identification.IataFlightNumber,
            Identification.RegNumberAircraft
          )

          radios mustBe expected
        }

        "When InlandMode is 'Fixed'" in {
          val answers = emptyUserAnswers
            .setValue(InlandModePage, InlandMode.Fixed)

          val radios = Identification.valuesU(answers)
          val expected = Seq(
            Identification.SeaGoingVessel,
            Identification.IataFlightNumber,
            Identification.InlandWaterwaysVehicle,
            Identification.ImoShipIdNumber,
            Identification.WagonNumber,
            Identification.TrainNumber,
            Identification.RegNumberRoadVehicle,
            Identification.RegNumberRoadTrailer,
            Identification.RegNumberAircraft,
            Identification.EuropeanVesselIdNumber,
            Identification.Unknown
          )

          radios mustBe expected
        }

        "When InlandMode is 'Waterway'" in {
          val answers = emptyUserAnswers
            .setValue(InlandModePage, InlandMode.Waterway)

          val radios = Identification.valuesU(answers)
          val expected = Seq(
            Identification.InlandWaterwaysVehicle,
            Identification.EuropeanVesselIdNumber
          )

          radios mustBe expected
        }

        "When InlandMode is 'Unknown'" in {
          val answers = emptyUserAnswers
            .setValue(InlandModePage, InlandMode.Unknown)

          val radios = Identification.valuesU(answers)
          val expected = Seq(
            Identification.Unknown
          )

          radios mustBe expected
        }
      }
    }
  }
}
