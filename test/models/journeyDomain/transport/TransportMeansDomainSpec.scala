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

package models.journeyDomain.transport

import base.SpecBase
import generators.Generators
import models.{Index, SecurityDetailsType}
import models.SecurityDetailsType._
import models.domain.{EitherType, UserAnswersReader}
import models.reference.{CustomsOffice, Nationality}
import models.transport.transportMeans.BorderModeOfTransport
import models.transport.transportMeans.departure.{InlandMode, Identification => DepartureIdentification}
import models.transport.transportMeans.active.{Identification => ActiveIdentification}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.preTaskList.SecurityDetailsTypePage
import pages.transport.transportMeans.{AnotherVehicleCrossingYesNoPage, BorderModeOfTransportPage}
import pages.transport.transportMeans.departure.{
  InlandModePage,
  MeansIdentificationNumberPage,
  VehicleCountryPage,
  IdentificationPage => DepartureIdentificationPage
}
import pages.transport.transportMeans.active.{
  AddNationalityYesNoPage,
  ConveyanceReferenceNumberYesNoPage,
  CustomsOfficeActiveBorderPage,
  IdentificationNumberPage,
  IdentificationPage => ActiveIdentificationPage
}

class TransportMeansDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

//  "TransportMeansDomain" - {
//    "can be parsed from user answers" - {
//
//      "when inland mode is 5 (mail" in {
//        val inlandMode = InlandMode.Mail
//
//        val answers = emptyUserAnswers
//          .setValue(SecurityDetailsTypePage, NoSecurityDetails)
//          .setValue(InlandModePage, inlandMode)
//
//        val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain](
//          TransportMeansDomain.userAnswersReader
//        ).run(answers)
//
//        result.value.inlandMode mustBe inlandMode
//      }
//
//      "when inland mode is not 5 (mail)" - {
//        val inlandMode = Gen.oneOf(InlandMode.values.filterNot(_ == InlandMode.Mail)).sample.value
//
//        "and security type is in Set{0}" - {
//          "and another vehicle crossing border is true" in {
//            val initialAnswers = emptyUserAnswers
//              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
//              .setValue(InlandModePage, inlandMode)
//              .setValue(AnotherVehicleCrossingYesNoPage, true)
//
//            forAll(arbitraryTransportMeansAnswers(initialAnswers)) {
//              answers =>
//                val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain](
//                  TransportMeansDomain.userAnswersReader
//                ).run(answers)
//
//                result.value.inlandMode mustBe inlandMode
//                result.value.asInstanceOf[TransportMeansDomainWithOtherInlandMode].transportMeansActive mustBe defined
//            }
//          }
//
//          "and another vehicle crossing border is false" in {
//            val initialAnswers = emptyUserAnswers
//              .setValue(SecurityDetailsTypePage, NoSecurityDetails)
//              .setValue(InlandModePage, inlandMode)
//              .setValue(AnotherVehicleCrossingYesNoPage, false)
//
//            forAll(arbitraryTransportMeansAnswers(initialAnswers)) {
//              answers =>
//                val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain](
//                  TransportMeansDomain.userAnswersReader
//                ).run(answers)
//
//                result.value.inlandMode mustBe inlandMode
//                result.value.asInstanceOf[TransportMeansDomainWithOtherInlandMode].transportMeansActive must not be defined
//            }
//          }
//        }
//
//        "and security type is in Set{1, 2, 3}" in {
//          val securityType = arbitrary[SecurityDetailsType](arbitrarySomeSecurityDetailsType).sample.value
//
//          val initialAnswers = emptyUserAnswers
//            .setValue(SecurityDetailsTypePage, securityType)
//            .setValue(InlandModePage, inlandMode)
//
//          forAll(arbitraryTransportMeansAnswers(initialAnswers)) {
//            answers =>
//              val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain](
//                TransportMeansDomain.userAnswersReader
//              ).run(answers)
//
//              result.value.inlandMode mustBe inlandMode
//              result.value.asInstanceOf[TransportMeansDomainWithOtherInlandMode].transportMeansActive mustBe defined
//          }
//        }
//      }
//    }
//
//    "cannot be parsed from user answers" - {
//      "when inland mode is unanswered" in {
//        val result: EitherType[TransportMeansDomain] = UserAnswersReader[TransportMeansDomain](
//          TransportMeansDomain.userAnswersReader
//        ).run(emptyUserAnswers)
//
//        result.left.value.page mustBe InlandModePage
//      }
//    }
//  }

  "TransportMeansWithNoActiveBorder" - {

    val inlandMode              = InlandMode.Maritime
    val departureIdentification = arbitrary[DepartureIdentification].sample.value
    val identificationNumber    = "1234"
    val vehicleCountry          = arbitrary[Nationality].sample.value

    val userAnswers = emptyUserAnswers
      .setValue(InlandModePage, inlandMode)
      .setValue(DepartureIdentificationPage, departureIdentification)
      .setValue(MeansIdentificationNumberPage, identificationNumber)
      .setValue(VehicleCountryPage, vehicleCountry)

    "can be parsed from userAnswers" in {

      val result = UserAnswersReader[TransportMeansWithNoActiveBorder](inlandMode).run(userAnswers)

      val expectedResult =
        TransportMeansWithNoActiveBorder(inlandMode,
                                         TransportMeansDepartureDomainWithOtherInlandMode(departureIdentification, identificationNumber, vehicleCountry)
        )

      result.value mustBe expectedResult
    }
    "cannot be parsed from userAnswers" in {

      val mandatoryPages = Seq(InlandModePage, DepartureIdentificationPage, MeansIdentificationNumberPage, VehicleCountryPage)

      mandatoryPages.map {
        mandatoryPage =>
          val updatedAnswers = userAnswers.removeValue(mandatoryPage)
          val result         = UserAnswersReader[TransportMeansWithNoActiveBorder](inlandMode).run(updatedAnswers)

          result.left.value.page mustBe mandatoryPage
      }
    }
  }

  "TransportMeansWithMultipleActiveBorders" - {

    val inlandMode                    = InlandMode.Maritime
    val departureIdentification       = arbitrary[DepartureIdentification].sample.value
    val departureIdentificationNumber = "1234"
    val vehicleCountry                = arbitrary[Nationality].sample.value
    val borderMode                    = arbitrary[BorderModeOfTransport].sample.value
    val activeIdentification          = arbitrary[ActiveIdentification].sample.value
    val activeIdentificationNumber    = "1234"
    val transitOffice                 = arbitrary[CustomsOffice].sample.value

    val userAnswers = emptyUserAnswers
      .setValue(InlandModePage, inlandMode)
      .setValue(DepartureIdentificationPage, departureIdentification)
      .setValue(MeansIdentificationNumberPage, departureIdentificationNumber)
      .setValue(VehicleCountryPage, vehicleCountry)
      .setValue(AnotherVehicleCrossingYesNoPage, true)
      .setValue(BorderModeOfTransportPage, borderMode)
      .setValue(SecurityDetailsTypePage, NoSecurityDetails)
      .setValue(ActiveIdentificationPage(Index(0)), activeIdentification)
      .setValue(IdentificationNumberPage(Index(0)), activeIdentificationNumber)
      .setValue(ConveyanceReferenceNumberYesNoPage(Index(0)), false)
      .setValue(AddNationalityYesNoPage(Index(0)), false)
      .setValue(CustomsOfficeActiveBorderPage(Index(0)), transitOffice)
      .setValue(ActiveIdentificationPage(Index(1)), activeIdentification)
      .setValue(IdentificationNumberPage(Index(1)), activeIdentificationNumber)
      .setValue(ConveyanceReferenceNumberYesNoPage(Index(1)), false)
      .setValue(AddNationalityYesNoPage(Index(1)), false)
      .setValue(CustomsOfficeActiveBorderPage(Index(1)), transitOffice)

    "can be parsed from userAnswers" in {

      val result = UserAnswersReader[TransportMeansWithMultipleActiveBorders](inlandMode).run(userAnswers)

      val activeBorder = Seq(
        TransportMeansActiveDomain(activeIdentification, activeIdentificationNumber, None, transitOffice, None),
        TransportMeansActiveDomain(activeIdentification, activeIdentificationNumber, None, transitOffice, None)
      )

      val expectedResult =
        TransportMeansWithMultipleActiveBorders(
          inlandMode,
          TransportMeansDepartureDomainWithOtherInlandMode(departureIdentification, departureIdentificationNumber, vehicleCountry),
          activeBorder
        )

      result.value mustBe expectedResult
    }
    "cannot be parsed from userAnswers" in {

      val mandatoryPages = Seq(
        InlandModePage,
        DepartureIdentificationPage,
        MeansIdentificationNumberPage,
        VehicleCountryPage,
        BorderModeOfTransportPage,
        ActiveIdentificationPage(Index(0)),
        IdentificationNumberPage(Index(0)),
        ConveyanceReferenceNumberYesNoPage(Index(0)),
        AddNationalityYesNoPage(Index(0)),
        CustomsOfficeActiveBorderPage(Index(0))
      )

      mandatoryPages.map {
        mandatoryPage =>
          val updatedAnswers = userAnswers.removeValue(mandatoryPage)
          val result         = UserAnswersReader[TransportMeansWithMultipleActiveBorders](inlandMode).run(updatedAnswers)

          result.left.value.page mustBe mandatoryPage
      }
    }
  }
}
